package com.ss.editor;

import static com.jme3.environment.LightProbeFactory.makeProbe;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static com.ss.rlib.util.Utils.run;
import static java.nio.file.Files.createDirectories;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.audio.AudioRenderer;
import com.jme3.audio.Environment;
import com.jme3.bounding.BoundingSphere;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.font.BitmapFont;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
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
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.config.Config;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.executor.impl.EditorThreadExecutor;
import com.ss.editor.extension.loader.SceneLoader;
import com.ss.editor.manager.*;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.WindowChangeFocusEvent;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerLevel;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.logging.impl.FolderFileListener;
import com.ss.rlib.manager.InitializeManager;
import jme3_ext_xbuf.XbufLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.filter.TonegodTranslucentBucketFilter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;

/**
 * The implementation of the {@link com.jme3.app.Application} of this Editor.
 *
 * @author JavaSaBr
 */
public class Editor extends JmeToJFXApplication {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(Editor.class);

    /**
     * The empty job adapter for handling creating {@link LightProbe}.
     */
    @NotNull
    private static final JobProgressAdapter<LightProbe> EMPTY_JOB_ADAPTER = new JobProgressAdapter<LightProbe>() {
        public void done(final LightProbe result) {
        }
    };

    @NotNull
    private static final Editor EDITOR = new Editor();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static Editor getInstance() {
        return EDITOR;
    }

    /**
     * Prepare to start editor.
     *
     * @return the editor
     */
    @NotNull
    static Editor prepareToStart() {

        if (Config.DEV_DEBUG) {
            System.err.println("config is loaded.");
        }

        configureLogger();
        try {

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

    private static void configureLogger() {

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
            run(() -> createDirectories(logFolder));
        }

        LoggerManager.addListener(new FolderFileListener(logFolder));
    }

    /**
     * The main synchronizer of this application.
     */
    @NotNull
    private final StampedLock lock;

    /**
     * The node for preview.
     */
    @NotNull
    private final Node previewNode;

    /**
     * The preview view port.
     */
    @Nullable
    private ViewPort previewViewPort;

    /**
     * The camera for preview.
     */
    @Nullable
    private Camera previewCamera;

    /**
     * The environment camera.
     */
    @Nullable
    private EnvironmentCamera environmentCamera;

    /**
     * The preview environment camera.
     */
    @Nullable
    private EnvironmentCamera previewEnvironmentCamera;

    /**
     * The light probe.
     */
    @Nullable
    private LightProbe lightProbe;

    /**
     * The preview light probe.
     */
    @Nullable
    private LightProbe previewLightProbe;

    /**
     * The processor of post effects.
     */
    @Nullable
    private FilterPostProcessor postProcessor;

    /**
     * The FXAA filter.
     */
    @Nullable
    private FXAAFilter fxaaFilter;

    /**
     * The filter of color correction.
     */
    @Nullable
    private ToneMapFilter toneMapFilter;

    /**
     * The translucent bucket filter.
     */
    @Nullable
    private TonegodTranslucentBucketFilter translucentBucketFilter;

    /**
     * The default material.
     */
    @Nullable
    private Material defaultMaterial;

    private Editor() {
        this.lock = new StampedLock();
        this.previewNode = new Node("Preview Node");
    }

    /**
     * Lock the render thread for other actions.
     *
     * @return the long
     */
    public final long asyncLock() {
        return lock.readLock();
    }

    /**
     * Unlock the render thread.
     *
     * @param stamp the stamp
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

        SceneLoader.install(this);

        assetManager.registerLoader(XbufLoader.class, FileExtensions.MODEL_XBUF);

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

        defaultMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

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

        translucentBucketFilter = new TonegodTranslucentBucketFilter(true);

        postProcessor.addFilter(fxaaFilter);
        postProcessor.addFilter(toneMapFilter);
        postProcessor.addFilter(translucentBucketFilter);

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

        createProbe();

        new EditorThread(new ThreadGroup("JavaFX"), JFXApplication::start, "JavaFX Launch").start();
    }

    /**
     * Lock the render thread for doing actions with game scene.
     */
    private long syncLock() {
        return lock.writeLock();
    }

    /**
     * Unlock the render thread.
     */
    private void syncUnlock(final long stamp) {
        lock.unlockWrite(stamp);
    }

    /**
     * Try to lock render thread for doing actions with game scene.
     *
     * @return the long
     */
    public long trySyncLock() {
        return lock.tryWriteLock();
    }

    @Override
    public void loseFocus() {
        super.loseFocus();

        final WindowChangeFocusEvent event = new WindowChangeFocusEvent();
        event.setFocused(false);

        final FXEventManager eventManager = FXEventManager.getInstance();
        eventManager.notify(event);
    }

    @Override
    public void gainFocus() {
        super.gainFocus();

        final WindowChangeFocusEvent event = new WindowChangeFocusEvent();
        event.setFocused(true);

        final FXEventManager eventManager = FXEventManager.getInstance();
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
        final long stamp = syncLock();
        try {

            final EditorThreadExecutor editorThreadExecutor = EditorThreadExecutor.getInstance();
            editorThreadExecutor.execute();

            //System.out.println(cam.getRotation());
            //System.out.println(cam.getLocation());

            super.update();

        } catch (final AssetNotFoundException | RendererException | AssertionError | ArrayIndexOutOfBoundsException |
                NullPointerException | StackOverflowError | IllegalStateException | UnsupportedOperationException e) {
            LOGGER.warning(e);
            finishWorkOnError(e);
        } finally {
            syncUnlock(stamp);
        }

        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());
    }

    private void finishWorkOnError(@NotNull final Throwable e) {

        GAnalytics.sendException(e, true);
        GAnalytics.waitForSend();

        final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
        workspaceManager.clear();

        System.exit(2);
    }

    /**
     * Gets post processor.
     *
     * @return the processor of post effects.
     */
    @NotNull
    public FilterPostProcessor getPostProcessor() {
        return notNull(postProcessor);
    }

    /**
     * Create the light probe for the PBR render.
     */
    private void createProbe() {

        final EnvironmentCamera environmentCamera = getEnvironmentCamera();
        final EnvironmentCamera previewEnvironmentCamera = getPreviewEnvironmentCamera();

        if (environmentCamera == null || previewEnvironmentCamera == null) {
            return;
        }

        if (environmentCamera.getApplication() == null) {
            final EditorThreadExecutor gameThreadExecutor = EditorThreadExecutor.getInstance();
            gameThreadExecutor.addToExecute(this::createProbe);
            return;
        }

        lightProbe = makeProbe(environmentCamera, rootNode, EMPTY_JOB_ADAPTER);
        previewLightProbe = makeProbe(previewEnvironmentCamera, previewNode, EMPTY_JOB_ADAPTER);

        BoundingSphere bounds = (BoundingSphere) lightProbe.getBounds();
        bounds.setRadius(100);

        bounds = (BoundingSphere) previewLightProbe.getBounds();
        bounds.setRadius(100);

        rootNode.addLight(lightProbe);
        previewNode.addLight(previewLightProbe);
    }

    /**
     * Update the light probe.
     *
     * @param progressAdapter the progress adapter
     */
    public void updateProbe(@NotNull final JobProgressAdapter<LightProbe> progressAdapter) {

        final LightProbe lightProbe = getLightProbe();
        final EnvironmentCamera environmentCamera = getEnvironmentCamera();

        if (lightProbe == null || environmentCamera == null) {
            progressAdapter.done(null);
            return;
        }

        LightProbeFactory.updateProbe(lightProbe, environmentCamera, rootNode, progressAdapter);
    }

    /**
     * Update the light probe.
     *
     * @param progressAdapter the progress adapter
     */
    public void updatePreviewProbe(@NotNull final JobProgressAdapter<LightProbe> progressAdapter) {

        final LightProbe lightProbe = getPreviewLightProbe();
        final EnvironmentCamera environmentCamera = getPreviewEnvironmentCamera();

        if (lightProbe == null || environmentCamera == null) {
            progressAdapter.done(null);
            return;
        }

        LightProbeFactory.updateProbe(lightProbe, environmentCamera, previewNode, progressAdapter);
    }

    /**
     * @return the light probe.
     */
    @Nullable
    private LightProbe getLightProbe() {
        return lightProbe;
    }

    /**
     * Gets preview view port.
     *
     * @return The preview view port.
     */
    @NotNull
    public ViewPort getPreviewViewPort() {
        return notNull(previewViewPort);
    }

    /**
     * @return the preview light probe.
     */
    @Nullable
    private LightProbe getPreviewLightProbe() {
        return previewLightProbe;
    }

    /**
     * @return the environment camera.
     */
    @Nullable
    private EnvironmentCamera getEnvironmentCamera() {
        return environmentCamera;
    }

    /**
     * Gets preview camera.
     *
     * @return the camera for preview.
     */
    @NotNull
    public Camera getPreviewCamera() {
        return notNull(previewCamera);
    }

    /**
     * @return the preview environment camera.
     */
    @Nullable
    private EnvironmentCamera getPreviewEnvironmentCamera() {
        return previewEnvironmentCamera;
    }

    /**
     * Gets preview node.
     *
     * @return the node for preview.
     */
    @NotNull
    public Node getPreviewNode() {
        return notNull(previewNode);
    }

    /**
     * Gets tone map filter.
     *
     * @return the filter of color correction.
     */
    @NotNull
    public ToneMapFilter getToneMapFilter() {
        return notNull(toneMapFilter);
    }

    /**
     * Gets fxaa filter.
     *
     * @return The FXAA filter.
     */
    public FXAAFilter getFXAAFilter() {
        return fxaaFilter;
    }

    /**
     * Gets translucent bucket filter.
     *
     * @return the translucent bucket filter.
     */
    @NotNull
    public TonegodTranslucentBucketFilter getTranslucentBucketFilter() {
        return notNull(translucentBucketFilter);
    }

    /**
     * Sets paused.
     *
     * @param paused true if this app is paused.
     */
    void setPaused(final boolean paused) {
        this.paused = paused;
    }

    /**
     * Gets gui font.
     *
     * @return the gui font.
     */
    @Nullable
    public BitmapFont getGuiFont() {
        return notNull(guiFont);
    }

    /**
     * Gets a default material.
     *
     * @return the default material.
     */
    @NotNull
    public Material getDefaultMaterial() {
        return notNull(defaultMaterial);
    }
}
