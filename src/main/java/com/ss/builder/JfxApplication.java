package com.ss.builder;

import static com.jme3.jfx.injfx.processor.FrameTransferSceneProcessor.TransferMode.ON_CHANGES;
import static com.ss.builder.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_OPEN_GL;
import static com.ss.builder.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_STOP_RENDER_ON_LOST_FOCUS;
import static com.ss.builder.config.DefaultSettingsProvider.Preferences.PREF_ANALYTICS_GOOGLE;
import static com.ss.builder.config.DefaultSettingsProvider.Preferences.PREF_OPEN_GL;
import static com.ss.builder.config.DefaultSettingsProvider.Preferences.PREF_STOP_RENDER_ON_LOST_FOCUS;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.jfx.injfx.JmeToJfxIntegrator;
import com.jme3.jfx.injfx.processor.FrameTransferSceneProcessor;
import com.jme3.util.LWJGLBufferAllocator;
import com.ss.builder.analytics.google.GAEvent;
import com.ss.builder.analytics.google.GAnalytics;
import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.config.CommandLineConfig;
import com.ss.builder.config.Config;
import com.ss.builder.config.EditorConfig;
import com.ss.builder.executor.impl.JmeThreadExecutor;
import com.ss.builder.fx.builder.EditorFxSceneBuilder;
import com.ss.builder.fx.css.CssRegistry;
import com.ss.builder.fx.dialog.ConfirmDialog;
import com.ss.builder.fx.event.FxEventManager;
import com.ss.builder.fx.event.impl.*;
import com.ss.builder.fx.scene.EditorFxScene;
import com.ss.builder.manager.*;
import com.ss.builder.plugin.api.RenderFilterRegistry;
import com.ss.builder.task.CheckNewVersionTask;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.util.OpenGLVersion;
import com.ss.builder.util.TimeTracker;
import com.ss.builder.util.svg.SvgImageLoaderFactory;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerLevel;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.logging.impl.FolderFileListener;
import com.ss.rlib.common.manager.InitializeManager;
import com.ss.rlib.common.util.ArrayUtils;
import com.ss.rlib.common.util.FileUtils;
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

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
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
     * @see EditorUtils
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

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_2)
                .finishAndStart(() -> "initialized configuration");

        AsyncEventManager.CombinedAsyncEventHandlerBuilder.of(JfxApplication::createSceneProcessor)
                .add(JmeContextCreatedEvent.EVENT_TYPE)
                .add(FxContextCreatedEvent.EVENT_TYPE)
                .add(ImageSystemInitializedEvent.EVENT_TYPE)
                .add(AllPluginsExtensionsRegisteredEvent.EVENT_TYPE)
                .buildAndRegister();

        InitializeManager.register(ExecutorManager.class);
        InitializeManager.register(FxEventManager.class);
        InitializeManager.register(ClasspathManager.class);
        InitializeManager.register(ResourceManager.class);
        InitializeManager.register(JavaFxImageManager.class);
        InitializeManager.register(FileIconManager.class);
        InitializeManager.register(WorkspaceManager.class);
        InitializeManager.register(CssRegistry.class);
        InitializeManager.register(RenderFilterRegistry.class);
        InitializeManager.register(PluginManager.class);
        InitializeManager.register(RemoteControlManager.class);
        InitializeManager.initialize();

        new EditorThread(new ThreadGroup("JavaFX"),
                JfxApplication::start, "JavaFX Launch").start();
        new EditorThread(new ThreadGroup("LWJGL"),
                JfxApplication::startJmeApplication, "LWJGL Render").start();

        AsyncEventManager.getInstance()
                .notify(new ManagersInitializedEvent());

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_2)
                .finish(() -> "initializing of all managers");
    }

    @FxThread
    private static void configureLogger() {

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_3)
                .start();

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

        LoggerManager.getLogger(AsyncEventManager.class)
                .setEnabled(LoggerLevel.DEBUG, Config.DEV_DEBUG_ASYNC_EVENT_MANAGER);

        var logFolder = Config.getFolderForLog();

        if (!Files.exists(logFolder)) {
            FileUtils.createDirectories(logFolder);
        }

        if (!LoggerLevel.DEBUG.isEnabled()) {
            LoggerManager.addListener(new FolderFileListener(logFolder));
        }

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_3)
                .finish(() -> "configuring of the logger");
    }

    /**
     * Start the new jME application.
     */
    @JmeThread
    private static void startJmeApplication() {

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_2)
                .start();

        var application = JmeApplication.getInstance();
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
            var stage = EditorUtils.getFxStage();

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
                    windows -> windows.findAny(Window::isFocused));

            jmeApplication.setPaused(window == null);
        };
    }

    @BackgroundThread
    private static void createSceneProcessor() {
        ExecutorManager.getInstance()
                .addFxTask(JfxApplication::createSceneProcessorImpl);
    }

    @FxThread
    private static void createSceneProcessorImpl() {

        var jfxApplication = getInstance();
        var jmeApplication = JmeApplication.getInstance();

        var scene = jfxApplication.getScene();
        var stage = jfxApplication.getStage();

        var sceneProcessor = JmeToJfxIntegrator.bind(jmeApplication, scene.getCanvas(), jmeApplication.getViewPort());
        sceneProcessor.setEnabled(false);
        sceneProcessor.setTransferMode(ON_CHANGES);

        jfxApplication.sceneProcessor = sceneProcessor;

        stage.focusedProperty()
                .addListener(makeFocusedListener());

        ExecutorManager.getInstance()
                .addBackgroundTask(scene::notifyFinishBuild);
    }

    /**
     * Start.
     */
    @FromAnyThread
    public static void start() {
        launch();
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
        EditorUtils.setJfxApplication(this);
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
    public void start(@NotNull Stage stage) {
        JfxApplication.instance = this;
        this.stage = stage;

        addWindow(stage);
        try {

            TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_3)
                    .start();

            var icons = stage.getIcons();
            icons.add(new Image("/ui/icons/app/256x256.png"));
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

            TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_3)
                    .finishAndStart(() -> "initialized of the FX stage");

            ObservableUtils.onChangeIf(stage.widthProperty(), number -> !stage.isMaximized(),
                    number -> config.setScreenWidth(number.intValue()));
            ObservableUtils.onChangeIf(stage.heightProperty(), number -> !stage.isMaximized(),
                    number -> config.setScreenHeight(number.intValue()));

            ObservableUtils.onChange(stage.maximizedProperty(), config::setMaximized);

            AsyncEventManager.SingleAsyncEventHandlerBuilder.of(FxSceneAttachedEvent.EVENT_TYPE)
                    .add(this::initializeImageSystem)
                    .buildAndRegister();

            ExecutorManager.getInstance()
                    .addBackgroundTask(this::buildScene);

        } catch (Throwable e) {
            LOGGER.error(this, e);
            throw e;
        }

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_2)
                .finish(() -> "initializing of jFX application");
        TimeTracker.getStartupTracker()
                .finish(() -> "showing of the main window editor");
    }

    /**
     * Initialize the image system.
     */
    @BackgroundThread
    private void initializeImageSystem() {
        var executorManager = ExecutorManager.getInstance();
        executorManager.addFxTask(() -> {

            SvgImageLoaderFactory.install();
            try {
                ImageIO.read(getClass().getResourceAsStream("/ui/icons/svg/picture.svg"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            LOGGER.info("image system is initialized.");

            AsyncEventManager.getInstance()
                    .notify(new ImageSystemInitializedEvent());
        });
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

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_5)
                .finishAndStart(() -> "creating of the FX scene");

        AsyncEventManager.getInstance()
                .notify(new FxSceneCreatedEvent());

        GAnalytics.forceSendEvent(GAEvent.Category.APPLICATION,
                GAEvent.Action.APPLICATION_LAUNCHED, GAEvent.Label.THE_EDITOR_APP_WAS_LAUNCHED);

        var executorManager = ExecutorManager.getInstance();
        executorManager.addBackgroundTask(new CheckNewVersionTask());

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
