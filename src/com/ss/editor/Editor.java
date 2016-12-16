package com.ss.editor;

import static java.nio.file.Files.createDirectories;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.Environment;
import com.jme3.bounding.BoundingSphere;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.input.InputManager;
import com.jme3.light.LightProbe;
import com.jme3.material.TechniqueDef;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.ToneMapFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RendererException;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.system.NativeLibraryLoader;
import com.jme3x.jfx.injfx.JmeToJFXApplication;
import com.jme3x.jfx.util.os.OperatingSystem;
import com.ss.editor.config.Config;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.config.ScreenSize;
import com.ss.editor.executor.impl.EditorThreadExecutor;
import com.ss.editor.manager.ClasspathManager;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.WindowChangeFocusEvent;
import com.ss.editor.ui.util.UIUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;

import rlib.logging.Logger;
import rlib.logging.LoggerLevel;
import rlib.logging.LoggerManager;
import rlib.logging.impl.FolderFileListener;
import rlib.manager.InitializeManager;
import rlib.util.Util;

/**
 * The implementation of the {@link com.jme3.app.Application} of this Editor.
 *
 * @author JavaSaBr
 */
public class Editor extends JmeToJFXApplication {

    private static final Logger LOGGER = LoggerManager.getLogger(Editor.class);

    /**
     * The empty job adapter for handling creating {@link LightProbe}.
     */
    private static final JobProgressAdapter<LightProbe> EMPTY_JOB_ADAPTER = new JobProgressAdapter<LightProbe>() {
        public void done(final LightProbe result) {
        }
    };

    private static final Editor EDITOR = new Editor();

    public static Editor getInstance() {
        return EDITOR;
    }

    public static Editor prepareToStart() {

        if (Config.DEV_DEBUG) {
            System.err.println("config is loaded.");
        }

        configureLogger();
        try {

            ScreenSize.init();

            final EditorConfig config = EditorConfig.getInstance();
            final AppSettings settings = config.getSettings();

            EDITOR.setSettings(settings);
            EDITOR.setShowSettings(false);
            EDITOR.setDisplayStatView(false);
            EDITOR.setDisplayFps(false);

        } catch (final Exception e) {
            LOGGER.warning(e);
            throw new RuntimeException(e);
        }

        return EDITOR;
    }

    protected static void configureLogger() {

        // disable the standard logger
        if (!Config.DEV_DEBUG) {
            java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
        }

        // configure our logger
        LoggerLevel.DEBUG.setEnabled(Config.DEV_DEBUG);
        LoggerLevel.INFO.setEnabled(true);
        LoggerLevel.ERROR.setEnabled(true);
        LoggerLevel.WARNING.setEnabled(true);

        final Path logFolder = Config.getFolderForLog();

        if (!Files.exists(logFolder)) {
            Util.safeExecute(() -> createDirectories(logFolder));
        }

        LoggerManager.addListener(new FolderFileListener(logFolder));
    }

    /**
     * The main synchronizer of this application.
     */
    private final StampedLock lock;

    /**
     * The node for preview.
     */
    private final Node previewNode;

    /**
     * The preview view port.
     */
    private ViewPort previewViewPort;

    /**
     * The camera for preview.
     */
    private Camera previewCamera;

    /**
     * The environment camera.
     */
    private EnvironmentCamera environmentCamera;

    /**
     * The preview environment camera.
     */
    private EnvironmentCamera previewEnvironmentCamera;

    /**
     * The light probe.
     */
    private LightProbe lightProbe;

    /**
     * The preview light probe.
     */
    private LightProbe previewLightProbe;

    /**
     * The processor of post effects.
     */
    private FilterPostProcessor postProcessor;

    /**
     * The FXAA filter.
     */
    private FXAAFilter fxaaFilter;

    /**
     * The filter of color correction.
     */
    private ToneMapFilter toneMapFilter;

    private Editor() {
        this.lock = new StampedLock();
        this.previewNode = new Node("Preview Node");
    }

    /**
     * Lock the render thread for other actions.
     */
    public final long asyncLock() {
        return lock.readLock();
    }

    /**
     * Unlock the render thread.
     */
    public final void asyncUnlock(final long stamp) {
        lock.unlockRead(stamp);
    }

    @Override
    public void destroy() {
        super.destroy();

        final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
        workspaceManager.save();

        System.exit(0);
    }

    @Override
    @NotNull
    public Camera getCamera() {
        return super.getCamera();
    }

    @Override
    public void start() {

        NativeLibraryLoader.loadNativeLibrary("jinput", true);
        NativeLibraryLoader.loadNativeLibrary("jinput-dx8", true);

        super.start();
    }

    @Override
    public void simpleInitApp() {
        renderManager.setPreferredLightMode(TechniqueDef.LightMode.SinglePass);
        renderManager.setSinglePassLightBatchSize(5);

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final OperatingSystem system = new OperatingSystem();

        LOGGER.info(this, "OS: " + system.getDistribution());

        final AssetManager assetManager = getAssetManager();
        assetManager.registerLocator("", FolderAssetLocator.class);
        assetManager.addAssetEventListener(EditorConfig.getInstance());

        final AudioRenderer audioRenderer = getAudioRenderer();
        audioRenderer.setEnvironment(new Environment(Environment.Garage));

        viewPort.setBackgroundColor(new ColorRGBA(50 / 255F, 50 / 255F, 50 / 255F, 1F));
        cam.setFrustumPerspective(55, (float) cam.getWidth() / cam.getHeight(), 1f, 10000);

        // create preview view port
        previewCamera = cam.clone();
        previewViewPort = renderManager.createPostView("Preview viewport", previewCamera);
        previewViewPort.setClearFlags(true, true, true);
        previewViewPort.attachScene(previewNode);
        previewViewPort.setBackgroundColor(viewPort.getBackgroundColor());

        final Node guiNode = getGuiNode();
        guiNode.detachAllChildren();

        ExecutorManager.getInstance();

        flyCam.setDragToRotate(true);
        flyCam.setEnabled(false);

        postProcessor = new FilterPostProcessor(assetManager);
        postProcessor.initialize(renderManager, viewPort);

        fxaaFilter = new FXAAFilter();
        fxaaFilter.setEnabled(editorConfig.isFXAA());
        fxaaFilter.setSubPixelShift(1.0f / 4.0f);
        fxaaFilter.setVxOffset(0.0f);
        fxaaFilter.setSpanMax(8.0f);
        fxaaFilter.setReduceMul(1.0f / 8.0f);

        toneMapFilter = new ToneMapFilter();
        toneMapFilter.setWhitePoint(editorConfig.getToneMapFilterWhitePoint());
        toneMapFilter.setEnabled(editorConfig.isToneMapFilter());

        postProcessor.addFilter(fxaaFilter);
        postProcessor.addFilter(toneMapFilter);

        viewPort.addProcessor(postProcessor);

        InitializeManager.register(ResourceManager.class);
        InitializeManager.register(JavaFXImageManager.class);
        InitializeManager.register(FileIconManager.class);
        InitializeManager.register(WorkspaceManager.class);
        InitializeManager.register(ClasspathManager.class);
        InitializeManager.initialize();

        if (Config.ENABLE_PBR) {
            environmentCamera = new EnvironmentCamera(64, Vector3f.ZERO);
            previewEnvironmentCamera = new EnvironmentCamera(64, Vector3f.ZERO);
            stateManager.attach(environmentCamera);
            stateManager.attach(previewEnvironmentCamera);
        }

        UIUtils.overrideTooltipBehavior(1000, 3000, 500);

        createProbe();

        new EditorThread(new ThreadGroup("JavaFX"), JFXApplication::start, "JavaFX Launch").start();

//        JFXPlatform.runInFXThread(() -> {
//            final JFXApplication jfxApplication = JFXApplication.getInstance();
//            jfxApplication.buildScene();
//        });
    }

    /**
     * Lock the render thread for doing actions with game scene.
     */
    public final long syncLock() {
        return lock.writeLock();
    }

    /**
     * Unlock the render thread.
     */
    public final void syncUnlock(final long stamp) {
        lock.unlockWrite(stamp);
    }

    /**
     * Try to lock render thread for doing actions with game scene.
     */
    public long trySyncLock() {
        return lock.tryWriteLock();
    }

    @Override
    public void loseFocus() {
        super.loseFocus();

        final WindowChangeFocusEvent event = new WindowChangeFocusEvent();
        event.setFocused(false);

        FXEventManager eventManager = FXEventManager.getInstance();
        eventManager.notify(event);
    }

    @Override
    public void gainFocus() {
        super.gainFocus();

        final WindowChangeFocusEvent event = new WindowChangeFocusEvent();
        event.setFocused(true);

        FXEventManager eventManager = FXEventManager.getInstance();
        eventManager.notify(event);
    }

    @Override
    public void simpleUpdate(final float tpf) {
        super.simpleUpdate(tpf);

        previewNode.updateLogicalState(tpf);
        previewNode.updateGeometricState();
    }

    @Override
    public void update() {

        final InputManager inputManager = getInputManager();
        //final JmeFxContainer fxContainer = getFxContainer();

        //if (fxContainer.isVisibleCursor() != inputManager.isCursorVisible()) {
        //fxContainer.setVisibleCursor(inputManager.isCursorVisible());
        //}

        final long stamp = syncLock();
        try {

            final EditorThreadExecutor editorThreadExecutor = EditorThreadExecutor.getInstance();
            editorThreadExecutor.execute();

            //System.out.println(cam.getRotation());
            //System.out.println(cam.getLocation());

            if (paused) return;

            super.update();

        } catch (final AssetNotFoundException | ArrayIndexOutOfBoundsException | NullPointerException | StackOverflowError e) {
            LOGGER.warning(e);
            final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
            workspaceManager.save();
            System.exit(1);
        } catch (final RendererException | IllegalStateException e) {
            LOGGER.warning(e);
            final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
            workspaceManager.clear();
            workspaceManager.save();
            System.exit(1);
        } finally {
            syncUnlock(stamp);
        }

        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());
    }

    /**
     * @return the processor of post effects.
     */
    public FilterPostProcessor getPostProcessor() {
        return postProcessor;
    }

    /**
     * Create the light probe for the PBR render.
     */
    private void createProbe() {

        final EnvironmentCamera environmentCamera = getEnvironmentCamera();
        if (environmentCamera == null) return;

        if (environmentCamera.getApplication() == null) {
            final EditorThreadExecutor gameThreadExecutor = EditorThreadExecutor.getInstance();
            gameThreadExecutor.addToExecute(this::createProbe);
            return;
        }

        lightProbe = LightProbeFactory.makeProbe(getEnvironmentCamera(), rootNode, EMPTY_JOB_ADAPTER);
        previewLightProbe = LightProbeFactory.makeProbe(getPreviewEnvironmentCamera(), previewNode, EMPTY_JOB_ADAPTER);

        BoundingSphere bounds = (BoundingSphere) lightProbe.getBounds();
        bounds.setRadius(100);

        bounds = (BoundingSphere) previewLightProbe.getBounds();
        bounds.setRadius(100);

        rootNode.addLight(lightProbe);
        previewNode.addLight(previewLightProbe);
    }

    /**
     * Update the light probe.
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
     * Update the light probe.
     */
    public void updatePreviewProbe(final JobProgressAdapter<LightProbe> progressAdapter) {

        final LightProbe lightProbe = getPreviewLightProbe();

        if (lightProbe == null) {
            progressAdapter.done(null);
            return;
        }

        LightProbeFactory.updateProbe(lightProbe, getPreviewEnvironmentCamera(), previewNode, progressAdapter);
    }

    /**
     * @return the light probe.
     */
    public LightProbe getLightProbe() {
        return lightProbe;
    }

    /**
     * @return The preview view port.
     */
    public ViewPort getPreviewViewPort() {
        return previewViewPort;
    }

    /**
     * @return the preview light probe.
     */
    public LightProbe getPreviewLightProbe() {
        return previewLightProbe;
    }

    /**
     * @return the environment camera.
     */
    public EnvironmentCamera getEnvironmentCamera() {
        return environmentCamera;
    }

    /**
     * @return the camera for preview.
     */
    public Camera getPreviewCamera() {
        return previewCamera;
    }

    /**
     * @return the preview environment camera.
     */
    public EnvironmentCamera getPreviewEnvironmentCamera() {
        return previewEnvironmentCamera;
    }

    /**
     * @return The node for preview.
     */
    public Node getPreviewNode() {
        return previewNode;
    }

    /**
     * @return the filter of color correction.
     */
    public ToneMapFilter getToneMapFilter() {
        return toneMapFilter;
    }

    /**
     * @return The FXAA filter.
     */
    public FXAAFilter getFXAAFilter() {
        return fxaaFilter;
    }
}
