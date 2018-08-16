package com.ss.editor.manager;

import static com.jme3.jfx.injfx.JmeToJfxIntegrator.bind;
import static com.jme3.jfx.injfx.processor.FrameTransferSceneProcessor.TransferMode.ON_CHANGES;
import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_TANGENT_GENERATION;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_TANGENT_GENERATION;
import static com.ss.editor.util.EditorUtils.getAssetFile;
import static com.ss.editor.util.EditorUtils.toAssetPath;
import static com.ss.rlib.common.util.FileUtils.getExtension;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.jfx.injfx.processor.FrameTransferSceneProcessor;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.RendererException;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;
import com.jme3.util.SkyFactory;
import com.ss.editor.FileExtensions;
import com.ss.editor.JmeApplication;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.asset.locator.FolderAssetLocator;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.executor.impl.JmeThreadExecutor;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.util.EditorUtils;
import com.ss.editor.util.TangentGenerator;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The class to manage previews of JME files.
 *
 * @author JavaSaBr
 */
public class JmeFilePreviewManager extends AbstractControl {

    private static final Logger LOGGER = LoggerManager.getLogger(JmeFilePreviewManager.class);

    private static final Vector3f LIGHT_DIRECTION =
            new Vector3f(0.007654993F, 0.39636374F, 0.9180617F).negate();

    private static final Vector3f CAMERA_LOCATION =
            new Vector3f(13.660254F, 5.176381F, 13.660254F);

    private static final Quaternion CAMERA_ROTATION =
            new Quaternion(0.9159756F, 0.04995022F, -0.37940952F, 0.12059049F);

    private static final Array<String> JME_FORMATS = ArrayFactory.newArray(String.class);
    private static final Array<String> MODELS_FORMATS = ArrayFactory.newArray(String.class);
    private static final Array<String> AUDIO_FORMATS = ArrayFactory.newArray(String.class);

    private static final EditorConfig EDITOR_CONFIG = EditorConfig.getInstance();

    static {
        JME_FORMATS.add(FileExtensions.JME_MATERIAL);
        JME_FORMATS.add(FileExtensions.JME_OBJECT);
        AUDIO_FORMATS.add(FileExtensions.AUDIO_OGG);
        AUDIO_FORMATS.add(FileExtensions.AUDIO_MP3);
        AUDIO_FORMATS.add(FileExtensions.AUDIO_WAV);
        MODELS_FORMATS.add(FileExtensions.JME_OBJECT);
        MODELS_FORMATS.add(FileExtensions.MODEL_BLENDER);
        MODELS_FORMATS.add(FileExtensions.MODEL_GLTF);
    }

    private static final JmeThreadExecutor EDITOR_THREAD_EXECUTOR = JmeThreadExecutor.getInstance();

    @Nullable
    private static volatile JmeFilePreviewManager instance;

    @FromAnyThread
    public static @NotNull JmeFilePreviewManager getInstance() {
        if (instance == null) {
            synchronized (JmeFilePreviewManager.class) {
                if (instance == null) {
                    instance = new JmeFilePreviewManager();
                }
            }
        }
        return notNull(instance);
    }

    /**
     * Check the file.
     *
     * @param file the file
     * @return true is the file is a file of a model.
     */
    @FromAnyThread
    public static boolean isModelFile(@Nullable Path file) {
        return file != null && MODELS_FORMATS.contains(getExtension(file));
    }

    /**
     * Check the file by the asset path.
     *
     * @param assetPath the asset path.
     * @return true is the file is a file of a model.
     */
    @FromAnyThread
    public static boolean isModelFile(@Nullable String assetPath) {
        return !StringUtils.isEmpty(assetPath) &&
                MODELS_FORMATS.contains(getExtension(assetPath));
    }

    /**
     * Check the file.
     *
     * @param file the file
     * @return true is the file is a JME file.
     */
    @FromAnyThread
    public static boolean isJmeFile(@Nullable Path file) {
        return file != null && JME_FORMATS.contains(getExtension(file));
    }

    /**
     * Check the file by the asset path.
     *
     * @param assetPath the asset path.
     * @return true is the file is a JME file.
     */
    @FromAnyThread
    public static boolean isJmeFile(@Nullable String assetPath) {
        return !StringUtils.isEmpty(assetPath) &&
                JME_FORMATS.contains(getExtension(assetPath));
    }

    /**
     * Check the file.
     *
     * @param file the file
     * @return true is the file is an audio file.
     */
    @FromAnyThread
    public static boolean isAudioFile(@Nullable Path file) {
        return file != null && AUDIO_FORMATS.contains(getExtension(file));
    }

    /**
     * Check the asset path.
     *
     * @param assetPath the asset path
     * @return true is the asset path is an audio file.
     */
    @FromAnyThread
    public static boolean isAudioFile(@Nullable String assetPath) {
        return assetPath != null && AUDIO_FORMATS.contains(getExtension(assetPath));
    }

    private final JobProgressAdapter<LightProbe> probeHandler = new JobProgressAdapter<LightProbe>() {

        @Override
        public void done(final LightProbe result) {
            notifyProbeComplete();
        }
    };

    /**
     * The preview container.
     */
    @NotNull
    private final ImageView imageView;

    /**
     * The test box.
     */
    @NotNull
    private final Geometry testBox;

    /**
     * The model node.
     */
    @NotNull
    private final Node modelNode;

    /**
     * The transfer processor.
     */
    @Nullable
    private volatile FrameTransferSceneProcessor processor;

    /**
     * The count of frames.
     */
    private int frame;

    private JmeFilePreviewManager() {
        this.imageView = new ImageView();
        this.testBox = new Geometry("Box", new Box(2, 2, 2));
        this.modelNode = new Node("Model Node");

        var executorManager = ExecutorManager.getInstance();
        executorManager.addFxTask(() -> {

            var scene = EditorUtils.getFxScene();
            var container = scene.getHideLayer();

            FxUtils.addChild(container, imageView);
            TangentGenerator.useMikktspaceGenerator(testBox);

            var executor = JmeThreadExecutor.getInstance();
            executor.addToExecute(this::prepareScene);
        });
    }

    @Override
    @JmeThread
    protected void controlUpdate(float tpf) {

        if (frame == 2) {
            var jmeApplication = JmeApplication.getInstance();
            jmeApplication.updatePreviewLightProbe(probeHandler);
        }

        frame++;
    }

    @JmeThread
    private void notifyProbeComplete() {
        var rootNode = EditorUtils.getPreviewNode();
        rootNode.attachChild(modelNode);
    }

    @Override
    @JmeThread
    protected void controlRender(@NotNull RenderManager renderManager, @NotNull ViewPort viewPort) {
    }

    /**
     * Show the file.
     *
     * @param file      the file.
     * @param fitWidth  the target width of preview.
     * @param fitHeight the target height of preview.
     */
    @FromAnyThread
    public void show(@NotNull Path file, int fitWidth, int fitHeight) {

        imageView.setFitHeight(fitHeight);
        imageView.setFitWidth(fitWidth);

        var assetFile = notNull(getAssetFile(file), "File can't be null.");
        var path = toAssetPath(assetFile);

        showPreview(path, getExtension(assetFile), false);
    }

    /**
     * Show the external file.
     *
     * @param file      the file.
     * @param fitWidth  the target width of preview.
     * @param fitHeight the target height of preview.
     */
    @FromAnyThread
    public void showExternal(@NotNull Path file, int fitWidth, int fitHeight) {
        imageView.setFitHeight(fitHeight);
        imageView.setFitWidth(fitWidth);
        showPreview(file.toString(), getExtension(file), true);
    }

    /**
     * Show a preview of the file by the asset path.
     *
     * @param path      the asset path.
     * @param extension the extension.
     * @param external  true if the path is external path.
     */
    @FromAnyThread
    private void showPreview(@NotNull String path, @NotNull String extension, boolean external) {
        if (FileExtensions.JME_MATERIAL.equals(extension)) {
            EDITOR_THREAD_EXECUTOR.addToExecute(() -> showMaterial(path));
        } else if (isModelFile(path)) {
            EDITOR_THREAD_EXECUTOR.addToExecute(() -> showObject(path, external));
        } else {
            EDITOR_THREAD_EXECUTOR.addToExecute(this::clear);
        }
    }

    /**
     * Show a file.
     *
     * @param assetPath the asset path.
     * @param fitWidth  the target width of preview.
     * @param fitHeight the target height of preview.
     */
    @FromAnyThread
    public void show(@NotNull String assetPath, int fitWidth, int fitHeight) {
        imageView.setFitHeight(fitHeight);
        imageView.setFitWidth(fitWidth);
        showPreview(assetPath, getExtension(assetPath), false);
    }

    /**
     * Show a j3o object.
     *
     * @param path     the path to object.
     * @param external true if the object is external object.
     */
    @JmeThread
    private void showObject(@NotNull String path, boolean external) {
        prepareProcessor();

        var assetManager = EditorUtils.getAssetManager();

        Spatial model;

        FolderAssetLocator.setIgnore(external);
        try {

            model = assetManager.loadModel(path);

            if (external && EDITOR_CONFIG.getBoolean(PREF_TANGENT_GENERATION, PREF_DEFAULT_TANGENT_GENERATION)) {
                TangentGenerator.useMikktspaceGenerator(model);
            }

        } finally {
            FolderAssetLocator.setIgnore(false);
        }

        tryToLoad(model);

        var rootNode = EditorUtils.getPreviewNode();
        rootNode.detachChild(modelNode);
    }

    /**
     * Try to load and show the model.
     *
     * @param model the model.
     */
    @JmeThread
    private void tryToLoad(@NotNull Spatial model) {
        try {

            var renderManager = EditorUtils.getRenderManager();
            renderManager.preloadScene(model);

            modelNode.attachChild(model);

        } catch (RendererException | AssetNotFoundException | UnsupportedOperationException e) {
            EditorUtils.handleException(LOGGER, this, e);
        }
    }

    /**
     * Prepare the processor to render the a preview object.
     */
    @JmeThread
    private void prepareProcessor() {

        if (processor != null) {
            processor.setEnabled(true);
        }

        frame = 0;

        var camera = EditorUtils.getPreviewCamera();
        camera.setLocation(CAMERA_LOCATION);
        camera.setRotation(CAMERA_ROTATION);

        modelNode.detachAllChildren();
    }

    /**
     * Show a j3m material.
     *
     * @param path the path to material.
     */
    @JmeThread
    private void showMaterial(@NotNull String path) {
        prepareProcessor();

        var assetManager = EditorUtils.getAssetManager();
        var material = assetManager.loadMaterial(path);

        testBox.setMaterial(material);
        tryToLoad(testBox);

        var rootNode = EditorUtils.getPreviewNode();
        rootNode.detachChild(modelNode);
    }

    /**
     * Clear a preview.
     */
    @FromAnyThread
    public void clear() {
        EDITOR_THREAD_EXECUTOR.addToExecute(this::clearImpl);
    }

    @JmeThread
    private void clearImpl() {

        var jmeApplication = JmeApplication.getInstance();
        var rootNode = jmeApplication.getPreviewNode();
        rootNode.detachChild(modelNode);

        if (processor != null) {
            processor.setEnabled(false);
        }
    }

    /**
     * Gets the image view with a preview.
     *
     * @return the image view with a preview.
     */
    @FxThread
    public @NotNull ImageView getImageView() {
        return imageView;
    }

    /**
     * Prepare a transfer processor to transfer preview result to a image view.
     *
     * @return the transfer processor.
     */
    @JmeThread
    private @NotNull FrameTransferSceneProcessor prepareScene() {

        var jmeApplication = JmeApplication.getInstance();
        var assetManager = jmeApplication.getAssetManager();
        var sky = SkyFactory.createSky(assetManager, "graphics/textures/sky/studio.hdr",
                        SkyFactory.EnvMapType.EquirectMap);

        var light = new DirectionalLight();
        light.setDirection(LIGHT_DIRECTION);

        var cameraNode = new Node("Camera node");
        var rootNode = jmeApplication.getPreviewNode();
        rootNode.addControl(this);
        rootNode.attachChild(sky);
        rootNode.addLight(light);
        rootNode.attachChild(cameraNode);
        rootNode.attachChild(modelNode);

        var camera = jmeApplication.getPreviewCamera();
        var editorCamera = new EditorCamera(camera, cameraNode);
        editorCamera.setMaxDistance(10000);
        editorCamera.setMinDistance(0.01F);
        editorCamera.setZoomSensitivity(0.2F);

        //TODO added supporting moving the camera

        processor = bind(jmeApplication, imageView, imageView, jmeApplication.getPreviewViewPort(), false);
        processor.setTransferMode(ON_CHANGES);
        processor.setEnabled(false);

        return processor;
    }
}
