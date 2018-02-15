package com.ss.editor;

import static com.jme3x.jfx.injfx.JmeToJFXIntegrator.bind;
import static com.jme3x.jfx.injfx.processor.FrameTransferSceneProcessor.TransferMode.ON_CHANGES;
import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_OPEN_GL;
import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_STOP_RENDER_ON_LOST_FOCUS;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.*;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static com.ss.rlib.util.Utils.run;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newOutputStream;
import com.jme3.renderer.Renderer;
import com.jme3.system.JmeContext;
import com.jme3.util.LWJGLBufferAllocator;
import com.jme3x.jfx.injfx.JmeToJFXApplication;
import com.jme3x.jfx.injfx.processor.FrameTransferSceneProcessor;
import com.ss.editor.analytics.google.GAEvent;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.config.CommandLineConfig;
import com.ss.editor.config.Config;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.executor.impl.JmeThreadExecutor;
import com.ss.editor.file.converter.FileConverterRegistry;
import com.ss.editor.manager.*;
import com.ss.editor.plugin.api.settings.SettingsProviderRegistry;
import com.ss.editor.task.CheckNewVersionTask;
import com.ss.editor.ui.builder.EditorFxSceneBuilder;
import com.ss.editor.ui.component.asset.tree.AssetTreeContextMenuFillerRegistry;
import com.ss.editor.ui.component.creator.FileCreatorRegistry;
import com.ss.editor.ui.component.editor.EditorRegistry;
import com.ss.editor.ui.component.log.LogView;
import com.ss.editor.ui.control.property.builder.PropertyBuilderRegistry;
import com.ss.editor.ui.control.tree.node.factory.TreeNodeFactoryRegistry;
import com.ss.editor.ui.css.CssRegistry;
import com.ss.editor.ui.dialog.ConfirmDialog;
import com.ss.editor.ui.preview.FilePreviewFactoryRegistry;
import com.ss.editor.ui.scene.EditorFxScene;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.OpenGLVersion;
import com.ss.editor.util.svg.SvgImageLoaderFactory;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerLevel;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.logging.impl.FolderFileListener;
import com.ss.rlib.manager.InitializeManager;
import com.ss.rlib.util.ArrayUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.array.ConcurrentArray;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.Configuration;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.logging.Level;

/**
 * The starter of the JavaFX application.
 *
 * @author JavaSaBr
 */
public class JfxApplication extends Application {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(JfxApplication.class);

    @Nullable
    private static JfxApplication instance;

    /**
     * It's an internal method.
     *
     * @see EditorUtil
     */
    @Deprecated
    @FromAnyThread
    public static @NotNull JfxApplication getInstance() {
        return notNull(instance);
    }

    /**
     * Main.
     *
     * @param args the args
     * @throws IOException the io exception
     */
    public static void main(final String[] args) {
        configureLogger();

        // need to disable to work on macos
        Configuration.GLFW_CHECK_THREAD0.set(false);
        // use jemalloc
        Configuration.MEMORY_ALLOCATOR.set("jemalloc");

        // JavaFX
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        System.setProperty("javafx.animation.fullspeed", "false");

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final OpenGLVersion openGLVersion = editorConfig.getEnum(PREF_OPEN_GL, PREF_DEFAULT_OPEN_GL);

        // set a render if it isn't override
        if(System.getProperty("jfx.background.render") == null) {
            System.setProperty("jfx.background.render", openGLVersion.getRender());
        }

        System.setProperty(LWJGLBufferAllocator.PROPERTY_CONCURRENT_BUFFER_ALLOCATOR, "true");

        // some settings for the render of JavaFX
        System.setProperty("prism.printrendergraph", "false");
        System.setProperty("javafx.pulseLogger", "false");

        //System.setProperty("prism.cacheshapes", "true");
        //System.setProperty("prism.scrollcacheopt", "true");
        //System.setProperty("prism.allowhidpi", "true");

        //System.setProperty("prism.order", "sw");
        //System.setProperty("prism.showdirty", "true");
        //System.setProperty("prism.showoverdraw", "true");
        //System.setProperty("prism.debug", "true");
        //System.setProperty("prism.verbose", "true");

        CommandLineConfig.args(args);

        JmeToJFXApplication application;
        try {
            application = JmeApplication.prepareToStart();
        } catch (final Throwable e) {
            printError(e);
            System.exit(-1);
            return;
        }

        InitializeManager.register(ClasspathManager.class);
        InitializeManager.register(ResourceManager.class);
        InitializeManager.register(JavaFxImageManager.class);
        InitializeManager.register(FileIconManager.class);
        InitializeManager.register(WorkspaceManager.class);
        InitializeManager.register(PluginManager.class);
        InitializeManager.register(RemoteControlManager.class);
        InitializeManager.initialize();

        new EditorThread(new ThreadGroup("LWJGL"),
                () -> startJmeApplication(application), "LWJGL Render").start();
    }

    @FxThread
    private static void configureLogger() {

        // disable the standard logger
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);

        // configure our logger
        LoggerLevel.DEBUG.setEnabled(Config.DEV_DEBUG);
        LoggerLevel.INFO.setEnabled(true);
        LoggerLevel.ERROR.setEnabled(true);
        LoggerLevel.WARNING.setEnabled(true);

        final Path logFolder = Config.getFolderForLog();

        if (!Files.exists(logFolder)) {
            run(() -> createDirectories(logFolder));
        }

        if (!LoggerLevel.DEBUG.isEnabled()) {
            LoggerManager.addListener(new FolderFileListener(logFolder));
        }
    }

    /**
     * Start the new jME application.
     *
     * @param application the new jME application.
     */
    @JmeThread
    private static void startJmeApplication(@NotNull final JmeToJFXApplication application) {

        final InitializationManager initializationManager = InitializationManager.getInstance();
        initializationManager.onBeforeCreateJmeContext();

        application.start();

        final JmeContext context = application.getContext();
        final Renderer renderer = context.getRenderer();

        if (renderer == null) {
            final EditorConfig editorConfig = EditorConfig.getInstance();
            editorConfig.set(PREF_OPEN_GL, OpenGLVersion.GL_20);
            editorConfig.save();
        }
    }

    /**
     * Create a new focus listener.
     *
     * @return the new focus listener.
     */
    @FxThread
    private static @NotNull ChangeListener<Boolean> makeFocusedListener() {
        return (observable, oldValue, newValue) -> {

            final JmeApplication jmeApplication = JmeApplication.getInstance();
            final Stage stage = EditorUtil.getFxStage();

            if (newValue || stage.isFocused()) {
                jmeApplication.setPaused(false);
                return;
            }

            final EditorConfig editorConfig = EditorConfig.getInstance();
            if (!editorConfig.getBoolean(PREF_STOP_RENDER_ON_LOST_FOCUS, PREF_DEFAULT_STOP_RENDER_ON_LOST_FOCUS)) {
                jmeApplication.setPaused(false);
                return;
            }

            final JfxApplication application = JfxApplication.getInstance();
            final Window window = ArrayUtils.getInReadLock(application.openedWindows,
                    windows -> windows.search(Window::isFocused));

            jmeApplication.setPaused(window == null);
        };
    }

    /**
     * Start.
     */
    @FromAnyThread
    public static void start() {
        launch();
    }

    @FromAnyThread
    private static void printError(@NotNull final Throwable throwable) {
        throwable.printStackTrace();

        final String userHome = System.getProperty("user.home");
        final String fileName = "jmonkeybuilder-error.log";

        try (final PrintStream out = new PrintStream(newOutputStream(Paths.get(userHome, fileName)))) {
            throwable.printStackTrace(out);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The list of opened windows.
     */
    @NotNull
    private final ConcurrentArray<Window> openedWindows;

    /**
     * The JavaFX scene.
     */
    @Nullable
    private volatile EditorFxScene scene;

    /**
     * The scene processor.
     */
    @Nullable
    private volatile FrameTransferSceneProcessor sceneProcessor;

    /**
     * The stage.
     */
    @Nullable
    private Stage stage;

    public JfxApplication() {
        EditorUtil.setJfxApplication(this);
        this.openedWindows = ArrayFactory.newConcurrentStampedLockArray(Window.class);
    }

    /**
     * Request focus of this window.
     */
    public void requestFocus() {
        notNull(stage).requestFocus();
    }

    /**
     * Add the new opened window.
     *
     * @param window the new opened window.
     */
    @FxThread
    public void addWindow(@NotNull final Window window) {
        window.focusedProperty().addListener(makeFocusedListener());
        ArrayUtils.runInWriteLock(openedWindows, window, Collection::add);
    }

    /**
     * Remove the opened window.
     *
     * @param window the opened window.
     */
    @FxThread
    public void removeWindow(@NotNull final Window window) {
        ArrayUtils.runInWriteLock(openedWindows, window, Array::slowRemove);
    }

    /**
     * Gets the last opened window.
     *
     * @return the last opened window.
     */
    @FxThread
    public @NotNull Window getLastWindow() {
        return notNull(ArrayUtils.getInReadLock(openedWindows, Array::last));
    }

    @Override
    @FxThread
    public void start(final Stage stage) throws Exception {
        JfxApplication.instance = this;
        this.stage = stage;

        addWindow(stage);
        try {

            // initialize javaFX events in javaFX thread.
            ArrayFactory.asArray(ComboBoxBase.ON_SHOWN);

            final ResourceManager resourceManager = ResourceManager.getInstance();
            resourceManager.reload();

            final InitializationManager initializationManager = InitializationManager.getInstance();
            initializationManager.onBeforeCreateJavaFxContext();

            final PluginManager pluginManager = PluginManager.getInstance();
            pluginManager.handlePlugins(editorPlugin -> editorPlugin.register(CssRegistry.getInstance()));

            LogView.getInstance();
            SvgImageLoaderFactory.install();
            ImageIO.read(getClass().getResourceAsStream("/ui/icons/test/test.jpg"));

            final ObservableList<Image> icons = stage.getIcons();
            icons.add(new Image("/ui/icons/app/256x256.png"));
            icons.add(new Image("/ui/icons/app/128x128.png"));
            icons.add(new Image("/ui/icons/app/96x96.png"));
            icons.add(new Image("/ui/icons/app/64x64.png"));
            icons.add(new Image("/ui/icons/app/48x48.png"));
            icons.add(new Image("/ui/icons/app/32x32.png"));
            icons.add(new Image("/ui/icons/app/24x24.png"));
            icons.add(new Image("/ui/icons/app/16x16.png"));

            final EditorConfig config = EditorConfig.getInstance();

            stage.initStyle(StageStyle.DECORATED);
            stage.setMinHeight(600);
            stage.setMinWidth(800);
            stage.setWidth(config.getScreenWidth());
            stage.setHeight(config.getScreenHeight());
            stage.setMaximized(config.isMaximized());
            stage.setTitle(Config.TITLE);
            stage.show();


            if (!stage.isMaximized()) {
                stage.centerOnScreen();
            }

            stage.widthProperty().addListener((observable, oldValue, newValue) -> {
                if (stage.isMaximized()) return;
                config.setScreenWidth(newValue.intValue());
            });
            stage.heightProperty().addListener((observable, oldValue, newValue) -> {
                if (stage.isMaximized()) return;
                config.setScreenHeight(newValue.intValue());
            });

            stage.maximizedProperty()
                    .addListener((observable, oldValue, newValue) -> config.setMaximized(newValue));

            buildScene();

        } catch (final Throwable e) {
            LOGGER.error(this, e);
            throw e;
        }
    }

    @Override
    @FxThread
    public void stop() throws Exception {
        super.stop();
        onExit();
    }

    /**
     * On exit.
     */
    @FxThread
    protected void onExit() {

        GAnalytics.forceSendEvent(GAEvent.Category.APPLICATION,
                GAEvent.Action.APPLICATION_CLOSED, GAEvent.Label.THE_EDITOR_APP_WAS_CLOSED);

        final EditorConfig config = EditorConfig.getInstance();
        config.save();

        final JmeThreadExecutor executor = JmeThreadExecutor.getInstance();
        executor.addToExecute(() -> {
            final JmeApplication jmeApplication = JmeApplication.getInstance();
            jmeApplication.destroy();
        });

        GAnalytics.waitForSend();
    }

    /**
     * Build the scene.
     */
    @FxThread
    private void buildScene() {
        this.scene = EditorFxSceneBuilder.build(notNull(stage));

        final InitializationManager initializationManager = InitializationManager.getInstance();
        initializationManager.onAfterCreateJavaFxContext();

        final PluginManager pluginManager = PluginManager.getInstance();
        pluginManager.handlePlugins(editorPlugin -> {
            editorPlugin.register(FileCreatorRegistry.getInstance());
            editorPlugin.register(EditorRegistry.getInstance());
            editorPlugin.register(FileIconManager.getInstance());
            editorPlugin.register(FileConverterRegistry.getInstance());
            editorPlugin.register(AssetTreeContextMenuFillerRegistry.getInstance());
            editorPlugin.register(TreeNodeFactoryRegistry.getInstance());
            editorPlugin.register(PropertyBuilderRegistry.getInstance());
            editorPlugin.register(FilePreviewFactoryRegistry.getInstance());
            editorPlugin.register(SettingsProviderRegistry.getInstance());
        });

        final EditorFxScene scene = getScene();

        final JmeApplication jmeApplication = JmeApplication.getInstance();
        final JmeThreadExecutor executor = JmeThreadExecutor.getInstance();
        executor.addToExecute(() -> createSceneProcessor(scene, jmeApplication));

        JmeFilePreviewManager.getInstance();

        GAnalytics.forceSendEvent(GAEvent.Category.APPLICATION,
                GAEvent.Action.APPLICATION_LAUNCHED, GAEvent.Label.THE_EDITOR_APP_WAS_LAUNCHED);

        final ExecutorManager executorManager = ExecutorManager.getInstance();
        executorManager.addBackgroundTask(new CheckNewVersionTask());

        final EditorConfig editorConfig = EditorConfig.getInstance();
        if (editorConfig.isAnalyticsQuestion()) {
            return;
        }

        editorConfig.set(PREF_ANALYTICS_GOOGLE, false);
        editorConfig.save();

        Platform.runLater(() -> {

            final Stage stage = notNull(getStage());
            final ConfirmDialog confirmDialog = new ConfirmDialog(result -> {

                editorConfig.setAnalyticsQuestion(true);
                editorConfig.set(PREF_ANALYTICS_GOOGLE, Boolean.TRUE.equals(result));
                editorConfig.save();

            }, Messages.ANALYTICS_CONFIRM_DIALOG_MESSAGE);

            confirmDialog.show(stage);
        });
    }

    @FxThread
    private void createSceneProcessor(@NotNull final EditorFxScene scene, @NotNull final JmeApplication jmeApplication) {

        final FrameTransferSceneProcessor sceneProcessor = bind(jmeApplication, scene.getCanvas(), jmeApplication.getViewPort());
        sceneProcessor.setEnabled(false);
        sceneProcessor.setTransferMode(ON_CHANGES);

        this.sceneProcessor = sceneProcessor;

        final Stage stage = notNull(getStage());
        stage.focusedProperty().addListener(makeFocusedListener());

        Platform.runLater(scene::notifyFinishBuild);
    }

    /**
     * Get the current JavaFX scene.
     *
     * @return the JavaFX scene.
     */
    @FromAnyThread
    public @NotNull EditorFxScene getScene() {
        return notNull(scene, "Scene can't be null.");
    }

    /**
     * Get the current stage of JavaFX.
     *
     * @return the current stage.
     */
    @FromAnyThread
    public @NotNull Stage getStage() {
        return notNull(stage);
    }

    /**
     * Get the current scene processor of this application.
     *
     * @return the scene processor.
     */
    @FromAnyThread
    public @NotNull FrameTransferSceneProcessor getSceneProcessor() {
        return notNull(sceneProcessor, "Scene processor can't be null.");
    }
}
