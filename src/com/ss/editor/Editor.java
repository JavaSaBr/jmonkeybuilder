package com.ss.editor;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.Environment;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.input.InputManager;
import com.jme3.light.LightProbe;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3x.jfx.JmeFxContainer;
import com.jme3x.jfx.util.os.OperatingSystem;
import com.ss.editor.config.CommandLineConfig;
import com.ss.editor.config.Config;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.config.ScreenSize;
import com.ss.editor.executor.impl.EditorThreadExecutor;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.ui.builder.EditorFXSceneBuilder;
import com.ss.editor.ui.cursor.UbuntuCursorProvider;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.ui.util.UIUtils;
import com.sun.javafx.cursor.CursorType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;

import javafx.scene.Parent;
import rlib.concurrent.atomic.AtomicInteger;
import rlib.logging.Logger;
import rlib.logging.LoggerLevel;
import rlib.logging.LoggerManager;
import rlib.logging.impl.FolderFileListener;
import rlib.manager.InitializeManager;

/**
 * Стартовый класс редактора.
 *
 * @author Ronn
 */
public class Editor extends SimpleApplication {

    private static final Logger LOGGER = LoggerManager.getLogger(Editor.class);

    private static final JobProgressAdapter<LightProbe> EMPTY_JOB_ADAPTER = new JobProgressAdapter<LightProbe>() {

        @Override
        public void done(LightProbe result) {
        }
    };

    private static final Editor EDITOR = new Editor();

    public static Editor getInstance() {
        return EDITOR;
    }

    public static void start(String[] args) throws IOException {

        // фикс рендера шрифтов в FX
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");

        // настройки для JavaFX
        System.setProperty("prism.vsync", "true");
        System.setProperty("javafx.animation.fullspeed", "false");
        System.setProperty("prism.cacheshapes", "true");

        // инициализация конфига
        if (Config.DEV_DEBUG) {
            System.err.println("config is loaded.");
        }

        CommandLineConfig.args(args);

        configureLogger();

        try {

            ScreenSize.init();

            final EditorConfig config = EditorConfig.getInstance();
            final AppSettings settings = config.getSettings();

            EDITOR.setSettings(settings);
            EDITOR.setShowSettings(false);
            EDITOR.setDisplayStatView(false);
            EDITOR.setDisplayFps(false);
            EDITOR.start();

        } catch (final Exception e) {
            LOGGER.warning(e);
        }
    }

    protected static void configureLogger() {

        // выключаем стандартный логгер
        if (!Config.DEV_DEBUG) {
            java.util.logging.Logger.getLogger("").setLevel(Level.SEVERE);
        }

        // настраиваем логгер
        LoggerLevel.DEBUG.setEnabled(Config.DEV_DEBUG);
        LoggerLevel.INFO.setEnabled(true);
        LoggerLevel.ERROR.setEnabled(true);
        LoggerLevel.WARNING.setEnabled(true);

        final Path logFolder = Paths.get(Config.PROJECT_PATH, "log");

        if (Files.exists(logFolder)) {
            return;
        }

        try {
            Files.createDirectories(logFolder);
            LoggerManager.addListener(new FolderFileListener(logFolder));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Синхронизатор.
     */
    private final StampedLock lock;

    /**
     * Камера окружения.
     */
    private EnvironmentCamera environmentCamera;

    /**
     * Свет окружения сцены.
     */
    private LightProbe lightProbe;

    /**
     * Контейнер UI JavaFX.
     */
    private JmeFxContainer fxContainer;

    /**
     * Текущая сцена интерфейса редактора.
     */
    private EditorFXScene scene;

    /**
     * Процессор пост эффетков.
     */
    private FilterPostProcessor postProcessor;

    private Editor() {
        this.lock = new StampedLock();
    }

    /**
     * @return текущая сцена интерфейса редактора.
     */
    public EditorFXScene getScene() {
        return scene;
    }

    /**
     * Блокировка рендера для каких-то асинхронных действий.
     */
    public final long asyncLock() {
        return lock.readLock();
    }

    /**
     * Разблокировка рендера.
     */
    public final void asyncUnlock(final long stamp) {
        lock.unlockRead(stamp);
    }

    @Override
    public void destroy() {
        super.destroy();
        System.exit(0);
    }

    @Override
    public Camera getCamera() {
        return super.getCamera();
    }

    /**
     * @return контейнер UI JavaFX.
     */
    public JmeFxContainer getFxContainer() {
        return fxContainer;
    }

    @Override
    public void restart() {

        final JmeFxContainer fxContainer = getFxContainer();
        final AtomicInteger waitCount = fxContainer.getWaitCount();
        waitCount.incrementAndGet();

        super.restart();
    }

    @Override
    public void simpleInitApp() {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final OperatingSystem system = new OperatingSystem();

        LOGGER.info(this, "OS: " + system.getDistribution());

        final AssetManager assetManager = getAssetManager();
        assetManager.registerLocator("", FolderAssetLocator.class);
        assetManager.addAssetEventListener(EditorConfig.getInstance());

        final AudioRenderer audioRenderer = getAudioRenderer();
        audioRenderer.setEnvironment(new Environment(Environment.Garage));

        viewPort.setBackgroundColor(new ColorRGBA(50 / 255F, 50 / 255F, 50 / 255F, 1F));
        cam.setFrustumPerspective(55, (float) cam.getWidth() / cam.getHeight(), 1f, 1000);

        final Node guiNode = getGuiNode();
        guiNode.detachAllChildren();

        ExecutorManager.getInstance();

        final UbuntuCursorProvider cursorDisplayProvider = new UbuntuCursorProvider(this, this.assetManager, inputManager);

        for (final CursorType type : CursorType.values()) {
            cursorDisplayProvider.setup(type);
        }

        flyCam.setDragToRotate(true);
        flyCam.setEnabled(false);

        postProcessor = new FilterPostProcessor(this.assetManager);
        postProcessor.initialize(renderManager, viewPort);

        if (editorConfig.isFXAA()) {

            final FXAAFilter filter = new FXAAFilter();
            filter.setEnabled(true);
            filter.setSubPixelShift(1.0f / 4.0f);
            filter.setVxOffset(0.0f);
            filter.setSpanMax(8.0f);
            filter.setReduceMul(1.0f / 8.0f);

            postProcessor.addFilter(filter);
        }

        viewPort.addProcessor(postProcessor);

        InitializeManager.register(ResourceManager.class);
        InitializeManager.register(JavaFXImageManager.class);
        InitializeManager.register(FileIconManager.class);
        InitializeManager.initialize();

        if (Config.ENABLE_PBR) {
            environmentCamera = new EnvironmentCamera(64, new Vector3f(0, 0, 0));
            stateManager.attach(environmentCamera);
        }

        fxContainer = JmeFxContainer.install(this, guiNode, true, cursorDisplayProvider);
        scene = EditorFXSceneBuilder.build(fxContainer);

        UIUtils.overrideTooltipBehavior(1000, 3000, 500);

        createProbe();
    }

    /**
     * Блокировать синхронизированную область.
     */
    public final long syncLock() {
        return lock.writeLock();
    }

    /**
     * Разблокировать синхронизированную область.
     */
    public final void syncUnlock(final long stamp) {
        lock.unlockWrite(stamp);
    }

    /**
     * Попытка произвести синхронизирующую блокировку.
     */
    public long trySyncLock() {
        return lock.tryWriteLock();
    }

    @Override
    public void update() {

        final ExecutorManager executorManager = ExecutorManager.getInstance();
        final InputManager inputManager = getInputManager();
        final EditorFXScene scene = getScene();
        final Parent root = scene.getRoot();

        if (inputManager.isCursorVisible() && root.isMouseTransparent()) {
            executorManager.addFXTask(() -> root.setMouseTransparent(false));
        } else if (!inputManager.isCursorVisible() && !root.isMouseTransparent()) {
            executorManager.addFXTask(() -> root.setMouseTransparent(true));
        }

        final JmeFxContainer fxContainer = getFxContainer();

        final long stamp = syncLock();
        try {

            final EditorThreadExecutor editorThreadExecutor = EditorThreadExecutor.getInstance();
            editorThreadExecutor.execute();

            if (paused) {
                return;
            }

            if (fxContainer.isNeedWriteToJME()) {
                fxContainer.writeToJME();
            }

            super.update();

        } catch (final ArrayIndexOutOfBoundsException | NullPointerException e) {
            LOGGER.warning(e);
            System.exit(1);
        } catch (final IllegalStateException e) {
            LOGGER.warning(e);
            System.exit(0);
        } finally {
            syncUnlock(stamp);
        }

        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());
    }

    /**
     * @return процессор пост эффетков.
     */
    public FilterPostProcessor getPostProcessor() {
        return postProcessor;
    }

    /**
     * Процесс создание пробы окружения.
     */
    private void createProbe() {

        final EnvironmentCamera environmentCamera = getEnvironmentCamera();

        if (environmentCamera == null) {
            return;
        }

        if (environmentCamera.getApplication() == null) {
            final EditorThreadExecutor gameThreadExecutor = EditorThreadExecutor.getInstance();
            gameThreadExecutor.addToExecute(this::createProbe);
            return;
        }

        lightProbe = LightProbeFactory.makeProbe(getEnvironmentCamera(), rootNode, EMPTY_JOB_ADAPTER);
        rootNode.addLight(lightProbe);
    }

    /**
     * Обновить пробу окружения.
     */
    public void updateProbe(final JobProgressAdapter<LightProbe> progressAdapter) {

        final LightProbe lightProbe = getLightProbe();

        if (lightProbe == null) {
            progressAdapter.done(null);
            return;
        }

        LightProbeFactory.updateProbe(lightProbe, getEnvironmentCamera(), rootNode, progressAdapter);
    }

    /**
     * @return свет окружения сцены.
     */
    public LightProbe getLightProbe() {
        return lightProbe;
    }

    /**
     * @return камера окружения сцены.
     */
    public EnvironmentCamera getEnvironmentCamera() {
        return environmentCamera;
    }
}
