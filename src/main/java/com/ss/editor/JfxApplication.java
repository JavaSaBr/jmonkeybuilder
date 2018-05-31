package com.ss.editor;

import static com.jme3.jfx.injfx.JmeToJfxIntegrator.bind;
import static com.jme3.jfx.injfx.processor.FrameTransferSceneProcessor.TransferMode.ON_CHANGES;
import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_OPEN_GL;
import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_STOP_RENDER_ON_LOST_FOCUS;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.*;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static com.ss.rlib.common.util.Utils.run;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.newOutputStream;
import com.jme3.jfx.injfx.JmeToJfxApplication;
import com.jme3.jfx.injfx.processor.FrameTransferSceneProcessor;
import com.jme3.util.LWJGLBufferAllocator;
import com.ss.editor.analytics.google.GAEvent;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.annotation.BackgroundThread;
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
import com.ss.editor.ui.control.property.builder.PropertyBuilderRegistry;
import com.ss.editor.ui.control.tree.node.factory.TreeNodeFactoryRegistry;
import com.ss.editor.ui.css.CssRegistry;
import com.ss.editor.ui.dialog.ConfirmDialog;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.FxSceneCreatedEvent;
import com.ss.editor.ui.event.impl.ManagersInitializedEvent;
import com.ss.editor.ui.preview.FilePreviewFactoryRegistry;
import com.ss.editor.ui.scene.EditorFxScene;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.OpenGLVersion;
import com.ss.editor.util.TimeTracker;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerLevel;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.logging.impl.FolderFileListener;
import com.ss.rlib.common.manager.InitializeManager;
import com.ss.rlib.common.util.ArrayUtils;
import com.ss.rlib.common.util.Utils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.array.ConcurrentArray;
import com.ss.rlib.fx.util.ObservableUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.Configuration;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

/**
 * The starter of the JavaFX application.
 *
 * @author JavaSaBr
 */
public class JfxApplication extends Application {

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
     * The start application method.
     */
    public static void main(@NotNull String[] args) {

        TimeTracker.getStartupTracker()
                .start();
        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_2)
                .start();

        configureLogger();

        // need to disable to work on macos
        Configuration.GLFW_CHECK_THREAD0.set(false);
        // use jemalloc
        Configuration.MEMORY_ALLOCATOR.set("jemalloc");

        // JavaFX
        System.setProperty("prism.lcdtext", "false");
        //System.setProperty("prism.text", "t2k");

        var editorConfig = EditorConfig.getInstance();
        var openGLVersion = editorConfig.getEnum(PREF_OPEN_GL, PREF_DEFAULT_OPEN_GL);

        // set a render if it isn't override
        if(System.getProperty("jfx.background.render") == null) {
            System.setProperty("jfx.background.render", openGLVersion.getRender());
        }

        System.setProperty(LWJGLBufferAllocator.PROPERTY_CONCURRENT_BUFFER_ALLOCATOR, "true");

        CommandLineConfig.args(args);

        JmeToJfxApplication application;
        try {
            application = JmeApplication.prepareToStart();
        } catch (Throwable e) {
            printError(e);
            System.exit(-1);
            return;
        }

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_2)
                .finishAndStart(() -> "Initialized configuration");

        InitializeManager.register(ExecutorManager.class);
        InitializeManager.register(AsyncEventManager.class);
        InitializeManager.register(FxEventManager.class);
        InitializeManager.register(ClasspathManager.class);
        InitializeManager.register(ResourceManager.class);
        InitializeManager.register(JavaFxImageManager.class);
        InitializeManager.register(FileIconManager.class);
        InitializeManager.register(WorkspaceManager.class);
        InitializeManager.register(CssRegistry.class);
        InitializeManager.register(PluginManager.class);
        InitializeManager.register(RemoteControlManager.class);
        InitializeManager.initialize();

        AsyncEventManager.getInstance()
                .notify(new ManagersInitializedEvent());

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_2)
                .finish(() -> "Initialized all managers");

        new EditorThread(new ThreadGroup("LWJGL"),
                () -> startJmeApplication(application), "LWJGL Render").start();
        new EditorThread(new ThreadGroup("JavaFX"), JfxApplication::start, "JavaFX Launch")
                .start();
    }

    @FxThread
    private static void configureLogger() {

        // disable the standard logger
        if (!Config.DEV_DEBUG) {
            java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
        } else {
            java.util.logging.Logger.getLogger("").setLevel(Level.ALL);
        }

        // configure our logger
        LoggerLevel.DEBUG.setEnabled(Config.DEV_DEBUG);
        LoggerLevel.INFO.setEnabled(true);
        LoggerLevel.ERROR.setEnabled(true);
        LoggerLevel.WARNING.setEnabled(true);

        var logFolder = Config.getFolderForLog();

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
    private static void startJmeApplication(@NotNull JmeToJfxApplication application) {

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_2)
                .start();

        application.start();

        var context = application.getContext();
        var renderer = context.getRenderer();

        if (renderer == null) {
            var editorConfig = EditorConfig.getInstance();
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

            var jmeApplication = JmeApplication.getInstance();
            var stage = EditorUtil.getFxStage();

            if (newValue || stage.isFocused()) {
                jmeApplication.setPaused(false);
                return;
            }

            var editorConfig = EditorConfig.getInstance();
            if (!editorConfig.getBoolean(PREF_STOP_RENDER_ON_LOST_FOCUS, PREF_DEFAULT_STOP_RENDER_ON_LOST_FOCUS)) {
                jmeApplication.setPaused(false);
                return;
            }

            var application = JfxApplication.getInstance();
            var window = ArrayUtils.getInReadLock(application.openedWindows,
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
    private static void printError(@NotNull Throwable throwable) {
        throwable.printStackTrace();

        var userHome = System.getProperty("user.home");
        var fileName = "jmonkeybuilder-error.log";

        try (var out = new PrintStream(newOutputStream(Paths.get(userHome, fileName)))) {
            throwable.printStackTrace(out);
        } catch (IOException e) {
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
    public void addWindow(@NotNull Window window) {
        window.focusedProperty().addListener(makeFocusedListener());
        ArrayUtils.runInWriteLock(openedWindows, window, Collection::add);
    }

    /**
     * Remove the opened window.
     *
     * @param window the opened window.
     */
    @FxThread
    public void removeWindow(@NotNull Window window) {
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
    public void start(@NotNull Stage stage) throws Exception {

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_3)
                .finish(() -> "Initialized jFX thread");

        JfxApplication.instance = this;
        this.stage = stage;

        addWindow(stage);
        try {

            TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_4)
                    .start();

            //LogView.getInstance();

            //SvgImageLoaderFactory.install();
            //ImageIO.read(getClass().getResourceAsStream("/ui/icons/test/test.jpg"));

            var icons = stage.getIcons();
            icons.add(new Image("/ui/icons/app/256x256.png"));
            icons.add(new Image("/ui/icons/app/128x128.png"));
            icons.add(new Image("/ui/icons/app/96x96.png"));
            icons.add(new Image("/ui/icons/app/64x64.png"));
            icons.add(new Image("/ui/icons/app/48x48.png"));
            icons.add(new Image("/ui/icons/app/32x32.png"));
            icons.add(new Image("/ui/icons/app/24x24.png"));
            icons.add(new Image("/ui/icons/app/16x16.png"));

            var config = EditorConfig.getInstance();

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

            stage.setAlwaysOnTop(true);

            ObservableUtils.onChangeIf(stage.widthProperty(), number -> !stage.isMaximized(),
                    number -> config.setScreenWidth(number.intValue()));

            ObservableUtils.onChangeIf(stage.heightProperty(), number -> !stage.isMaximized(),
                    number -> config.setScreenHeight(number.intValue()));

            ObservableUtils.onChange(stage.maximizedProperty(), config::setMaximized);

            ExecutorManager.getInstance()
                    .addBackgroundTask(this::buildScene);

            TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_4)
                    .finishAndStart(() -> "Initialized FX stage");

        } catch (Throwable e) {
            LOGGER.error(this, e);
            throw e;
        }

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_2)
                .finish(() -> "Initialized jFX application");
        TimeTracker.getStartupTracker()
                .finish(() -> "Initialized editor");
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

        var config = EditorConfig.getInstance();
        config.save();

        var waiter = new CountDownLatch(1);

        var executor = JmeThreadExecutor.getInstance();
        executor.addToExecute(() -> {
            JmeApplication.getInstance().destroy();
            waiter.countDown();
        });

        GAnalytics.waitForSend();
        Utils.run(waiter::await);
    }

    /**
     * Build the scene.
     */
    @BackgroundThread
    private void buildScene() {

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_5)
                .start();

        this.scene = EditorFxSceneBuilder.build(notNull(stage));

        AsyncEventManager.getInstance()
                .notify(new FxSceneCreatedEvent());

        var pluginManager = PluginManager.getInstance();
        pluginManager.handlePluginsNow(editorPlugin -> {
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

        var scene = getScene();

        var jmeApplication = JmeApplication.getInstance();
        var executor = JmeThreadExecutor.getInstance();
        executor.addToExecute(() -> createSceneProcessor(scene, jmeApplication));

        JmeFilePreviewManager.getInstance();

        GAnalytics.forceSendEvent(GAEvent.Category.APPLICATION,
                GAEvent.Action.APPLICATION_LAUNCHED, GAEvent.Label.THE_EDITOR_APP_WAS_LAUNCHED);

        var executorManager = ExecutorManager.getInstance();
        executorManager.addBackgroundTask(new CheckNewVersionTask());

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_5)
                .finishAndStart(() -> "Created FX scene");

        var editorConfig = EditorConfig.getInstance();
        if (editorConfig.isAnalyticsQuestion()) {
            return;
        }

        editorConfig.set(PREF_ANALYTICS_GOOGLE, false);
        editorConfig.save();

        Platform.runLater(() -> {

            var stage = notNull(getStage());
            var confirmDialog = new ConfirmDialog(result -> {

                editorConfig.setAnalyticsQuestion(true);
                editorConfig.set(PREF_ANALYTICS_GOOGLE, Boolean.TRUE.equals(result));
                editorConfig.save();

            }, Messages.ANALYTICS_CONFIRM_DIALOG_MESSAGE);

            confirmDialog.show(stage);
        });
    }

    @FxThread
    private void createSceneProcessor(@NotNull EditorFxScene scene, @NotNull JmeApplication jmeApplication) {

        var sceneProcessor = bind(jmeApplication, scene.getCanvas(), jmeApplication.getViewPort());
        sceneProcessor.setEnabled(false);
        sceneProcessor.setTransferMode(ON_CHANGES);

        this.sceneProcessor = sceneProcessor;

        var stage = notNull(getStage());
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
