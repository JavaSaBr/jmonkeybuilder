package com.ss.editor;

import static com.jme3.environment.LightProbeFactory.makeProbe;
import static com.ss.editor.config.DefaultSettingsProvider.Defaults.*;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.*;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.app.DebugKeysAppState;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.audio.Environment;
import com.jme3.bounding.BoundingSphere;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.environment.util.EnvMapUtils;
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
import com.jme3x.jfx.injfx.JmeToJFXApplication;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.asset.locator.FileSystemAssetLocator;
import com.ss.editor.asset.locator.FolderAssetLocator;
import com.ss.editor.config.Config;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.executor.impl.JmeThreadExecutor;
import com.ss.editor.extension.loader.SceneLoader;
import com.ss.editor.filter.EditorFxaaFilter;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.InitializationManager;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.WindowChangeFocusEvent;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.util.os.OperatingSystem;
import jme3_ext_xbuf.XbufLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.locks.StampedLock;

/**
 * The implementation of the {@link com.jme3.app.Application} of this Editor.
 *
 * @author JavaSaBr
 */
public class JmeApplication extends JmeToJFXApplication {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(JmeApplication.class);

    /**
     * The empty job adapter for handling creating {@link LightProbe}.
     */
    @NotNull
    private static final JobProgressAdapter<LightProbe> EMPTY_JOB_ADAPTER = new JobProgressAdapter<LightProbe>() {
        public void done(@NotNull LightProbe result) {
        }
    };

    @NotNull
    private static final JmeApplication JME_APPLICATION = new JmeApplication();

    /**
     * It's an internal method.
     *
     * @see EditorUtil
     */
    @Deprecated
    @FromAnyThread
    public static @NotNull JmeApplication getInstance() {
        return JME_APPLICATION;
    }

    /**
     * Prepare to start editor.
     *
     * @return the editor
     */
    @JmeThread
    static @NotNull JmeApplication prepareToStart() {

        if (Config.DEV_DEBUG) {
            System.err.println("config was loaded.");
        }

        try {

            var config = EditorConfig.getInstance();
            var settings = config.getSettings();

            JME_APPLICATION.setSettings(settings);
            JME_APPLICATION.setShowSettings(false);
            JME_APPLICATION.setDisplayStatView(false);
            JME_APPLICATION.setDisplayFps(false);

        } catch (final Exception e) {
            LOGGER.warning(e);
            throw new RuntimeException(e);
        }

        return JME_APPLICATION;
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
     * The default material.
     */
    @Nullable
    private Material defaultMaterial;

    private JmeApplication() {
        EditorUtil.setJmeApplication(this);
        this.lock = new StampedLock();
        this.previewNode = new Node("Preview Node");
    }

    /**
     * Lock the render thread for other actions.
     *
     * @return the long
     */
    @FromAnyThread
    public final long asyncLock() {
        return lock.readLock();
    }

    /**
     * Unlock the render thread.
     *
     * @param stamp the stamp
     */
    @FromAnyThread
    public final void asyncUnlock(long stamp) {
        lock.unlockRead(stamp);
    }

    @Override
    @JmeThread
    public void destroy() {
        super.destroy();

        var workspaceManager = WorkspaceManager.getInstance();
        workspaceManager.save();

        System.exit(0);
    }

    @Override
    @FromAnyThread
    public @NotNull Camera getCamera() {
        return super.getCamera();
    }

    @Override
    @JmeThread
    public void simpleInitApp() {
        super.simpleInitApp();

        renderManager.setPreferredLightMode(TechniqueDef.LightMode.SinglePass);
        renderManager.setSinglePassLightBatchSize(10);

        assetManager.registerLoader(XbufLoader.class, FileExtensions.MODEL_XBUF);

        var editorConfig = EditorConfig.getInstance();
        var system = new OperatingSystem();

        LOGGER.debug(this, "OS: " + system.getDistribution());

        var assetManager = getAssetManager();
        assetManager.unregisterLocator("", ClasspathLocator.class);
        assetManager.unregisterLocator("/", ClasspathLocator.class);
        assetManager.registerLocator("", FolderAssetLocator.class);
        assetManager.registerLocator("", FileSystemAssetLocator.class);
        assetManager.registerLocator("", ClasspathLocator.class);
        assetManager.addAssetEventListener(EditorConfig.getInstance());

        var audioRenderer = getAudioRenderer();
        audioRenderer.setEnvironment(new Environment(Environment.Garage));

        viewPort.setBackgroundColor(new ColorRGBA(50 / 255F, 50 / 255F, 50 / 255F, 1F));
        cam.setFrustumPerspective(55, (float) cam.getWidth() / cam.getHeight(), 1f, Integer.MAX_VALUE);

        defaultMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        // create preview view port
        previewCamera = cam.clone();

        previewViewPort = renderManager.createPostView("Preview viewport", previewCamera);
        previewViewPort.setClearFlags(true, true, true);
        previewViewPort.attachScene(previewNode);
        previewViewPort.setBackgroundColor(viewPort.getBackgroundColor());

        var guiNode = getGuiNode();
        guiNode.detachAllChildren();

        ExecutorManager.getInstance();

        flyCam.setDragToRotate(true);
        flyCam.setEnabled(false);

        var postProcessor = getPostProcessor();

        fxaaFilter = new EditorFxaaFilter();
        fxaaFilter.setEnabled(editorConfig.getBoolean(PREF_FILTER_FXAA, PREF_DEFAULT_FXAA_FILTER));
        fxaaFilter.setSubPixelShift(1.0f / 4.0f);
        fxaaFilter.setVxOffset(0.0f);
        fxaaFilter.setSpanMax(8.0f);
        fxaaFilter.setReduceMul(1.0f / 8.0f);

        toneMapFilter = new ToneMapFilter();
        toneMapFilter.setWhitePoint(editorConfig.getVector3f(PREF_FILTER_TONEMAP_WHITE_POINT, PREF_DEFAULT_TONEMAP_WHITE_POINT));
        toneMapFilter.setEnabled(editorConfig.getBoolean(PREF_FILTER_TONEMAP, PREF_DEFAULT_TONEMAP_FILTER));

        postProcessor.addFilter(fxaaFilter);
        postProcessor.addFilter(toneMapFilter);

        SceneLoader.install(this, postProcessor);

        if (Config.ENABLE_PBR) {
            environmentCamera = new EnvironmentCamera(64, Vector3f.ZERO);
            previewEnvironmentCamera = new EnvironmentCamera(64, Vector3f.ZERO);
            stateManager.attach(environmentCamera);
            stateManager.attach(previewEnvironmentCamera);
        }

        createLightProbes();
        stateManager.detach(stateManager.getState(DebugKeysAppState.class));

        var initializationManager = InitializationManager.getInstance();
        initializationManager.onAfterCreateJmeContext();

        new EditorThread(new ThreadGroup("JavaFX"), JfxApplication::start, "JavaFX Launch").start();
    }

    /**
     * Lock the render thread for doing actions with game scene.
     *
     * @return the lock stamp.
     */
    @FromAnyThread
    public long syncLock() {
        return lock.writeLock();
    }

    /**
     * Unlock the render thread.
     *
     * @param stamp the stamp of the lock.
     */
    @FromAnyThread
    public void syncUnlock(long stamp) {
        lock.unlockWrite(stamp);
    }

    /**
     * Try to lock render thread for doing actions with game scene.
     *
     * @return the long
     */
    @FromAnyThread
    public long trySyncLock() {
        return lock.tryWriteLock();
    }

    @Override
    @JmeThread
    public void loseFocus() {
        super.loseFocus();

        var event = new WindowChangeFocusEvent();
        event.setFocused(false);

        var eventManager = FxEventManager.getInstance();
        eventManager.notify(event);
    }

    @Override
    @JmeThread
    public void gainFocus() {
        super.gainFocus();

        var event = new WindowChangeFocusEvent();
        event.setFocused(true);

        var eventManager = FxEventManager.getInstance();
        eventManager.notify(event);
    }

    @Override
    @JmeThread
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);

        previewNode.updateLogicalState(tpf);
        previewNode.updateGeometricState();
    }

    @Override
    @JmeThread
    public void update() {
        var stamp = syncLock();
        try {

            var executor = JmeThreadExecutor.getInstance();
            executor.execute();

            //System.out.println(cam.getRotation());
            //System.out.println(cam.getLocation());

            if (Config.ENABLE_3D) {
                super.update();
            }

        } catch (final AssetNotFoundException | NoSuchMethodError | RendererException | AssertionError |
                ArrayIndexOutOfBoundsException | NullPointerException | StackOverflowError |
                IllegalStateException | UnsupportedOperationException e) {
            LOGGER.warning(e);
            finishWorkOnError(e);
        } finally {
            syncUnlock(stamp);
        }

        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());
    }

    @JmeThread
    private void finishWorkOnError(@NotNull final Throwable e) {

        GAnalytics.sendException(e, true);
        GAnalytics.waitForSend();

        var workspaceManager = WorkspaceManager.getInstance();
        workspaceManager.clear();

        System.exit(2);
    }

    /**
     * Gets post processor.
     *
     * @return the processor of post effects.
     */
    @JmeThread
    public @NotNull FilterPostProcessor getPostProcessor() {
        return notNull(postProcessor);
    }

    /**
     * Create the light probes for the PBR render.
     */
    @JmeThread
    private void createLightProbes() {

        var environmentCamera = getEnvironmentCamera();
        var previewEnvironmentCamera = getPreviewEnvironmentCamera();

        if (environmentCamera == null || previewEnvironmentCamera == null) {
            return;
        }

        if (environmentCamera.getApplication() == null) {
            final JmeThreadExecutor gameThreadExecutor = JmeThreadExecutor.getInstance();
            gameThreadExecutor.addToExecute(this::createLightProbes);
            return;
        }

        lightProbe = makeProbe(environmentCamera, rootNode, EnvMapUtils.GenerationType.Fast, EMPTY_JOB_ADAPTER);
        previewLightProbe = makeProbe(previewEnvironmentCamera, previewNode, EnvMapUtils.GenerationType.Fast, EMPTY_JOB_ADAPTER);

        var bounds = (BoundingSphere) lightProbe.getBounds();
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
    @JmeThread
    public void updateLightProbe(@NotNull JobProgressAdapter<LightProbe> progressAdapter) {

        var lightProbe = getLightProbe();
        var environmentCamera = getEnvironmentCamera();

        if (lightProbe == null || environmentCamera == null) {
            progressAdapter.done(null);
            return;
        }

        LightProbeFactory.updateProbe(lightProbe, environmentCamera, rootNode,
                EnvMapUtils.GenerationType.Fast, progressAdapter);
    }

    /**
     * Disable PBR Light probe.
     */
    @JmeThread
    public void disableLightProbe() {

        var lightProbe = getLightProbe();

        if (lightProbe != null) {
            rootNode.removeLight(lightProbe);
        }
    }

    /**
     * Enable PBR Light probe.
     */
    @JmeThread
    public void enableLightProbe() {

        var lightProbe = getLightProbe();
        if (lightProbe == null) {
            return;
        }

        var lightList = rootNode.getLocalLightList();

        for (int i = 0; i < lightList.size(); i++) {
            if (lightList.get(i) == lightProbe) {
                return;
            }
        }

        rootNode.addLight(lightProbe);
    }

    /**
     * Update the light probe.
     *
     * @param progressAdapter the progress adapter
     */
    @JmeThread
    public void updatePreviewLightProbe(@NotNull final JobProgressAdapter<LightProbe> progressAdapter) {

        var lightProbe = getPreviewLightProbe();
        var environmentCamera = getPreviewEnvironmentCamera();

        if (lightProbe == null || environmentCamera == null) {
            progressAdapter.done(null);
            return;
        }

        LightProbeFactory.updateProbe(lightProbe, environmentCamera, previewNode,
                EnvMapUtils.GenerationType.Fast, progressAdapter);
    }

    /**
     * Get the light probe.
     *
     * @return the light probe.
     */
    @JmeThread
    private @Nullable LightProbe getLightProbe() {
        return lightProbe;
    }

    /**
     * Get the preview view port.
     *
     * @return The preview view port.
     */
    @JmeThread
    public @NotNull ViewPort getPreviewViewPort() {
        return notNull(previewViewPort);
    }

    /**
     * Get the preview light probe.
     *
     * @return the preview light probe.
     */
    @JmeThread
    private @Nullable LightProbe getPreviewLightProbe() {
        return previewLightProbe;
    }

    /**
     * Get the environment camera.
     *
     * @return the environment camera.
     */
    @JmeThread
    private @Nullable EnvironmentCamera getEnvironmentCamera() {
        return environmentCamera;
    }

    /**
     * Get the preview camera.
     *
     * @return the preview camera.
     */
    @JmeThread
    public @NotNull Camera getPreviewCamera() {
        return notNull(previewCamera);
    }

    /**
     * Get the preview environment camera.
     *
     * @return the preview environment camera.
     */
    @JmeThread
    private @Nullable EnvironmentCamera getPreviewEnvironmentCamera() {
        return previewEnvironmentCamera;
    }

    /**
     * Get the preview node.
     *
     * @return the preview node.
     */
    @JmeThread
    public @NotNull Node getPreviewNode() {
        return notNull(previewNode);
    }

    /**
     * Get the tone map filter.
     *
     * @return the tone map filter.
     */
    @JmeThread
    public @NotNull ToneMapFilter getToneMapFilter() {
        return notNull(toneMapFilter);
    }

    /**
     * Get the FXAA filter.
     *
     * @return the FXAA filter.
     */
    @JmeThread
    public @NotNull FXAAFilter getFXAAFilter() {
        return notNull(fxaaFilter);
    }

    /**
     * Sets paused.
     *
     * @param paused true if this app is paused.
     */
    @JmeThread
    void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * Get the gui font.
     *
     * @return the gui font.
     */
    @JmeThread
    public @Nullable BitmapFont getGuiFont() {
        return notNull(guiFont);
    }

    /**
     * Get the default material.
     *
     * @return the default material.
     */
    @JmeThread
    public @NotNull Material getDefaultMaterial() {
        return notNull(defaultMaterial);
    }
}
