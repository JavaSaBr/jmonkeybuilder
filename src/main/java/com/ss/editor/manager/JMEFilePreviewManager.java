package com.ss.editor.manager;

import static com.jme3x.jfx.injfx.JmeToJFXIntegrator.bind;
import static com.jme3x.jfx.injfx.processor.FrameTransferSceneProcessor.TransferMode.ON_CHANGES;
import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.util.FileUtils.getExtension;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.RendererException;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;
import com.jme3.util.SkyFactory;
import com.jme3x.jfx.injfx.processor.FrameTransferSceneProcessor;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.JFXApplication;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JMEThread;
import com.ss.editor.asset.locator.FolderAssetLocator;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.executor.impl.JMEThreadExecutor;
import com.ss.editor.model.tool.TangentGenerator;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The class to manage previews of JME files.
 *
 * @author JavaSaBr
 */
public class JMEFilePreviewManager extends AbstractControl {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(JMEFilePreviewManager.class);

    @NotNull
    private static final Vector3f LIGHT_DIRECTION = new Vector3f(0.007654993F, 0.39636374F, 0.9180617F).negate();

    @NotNull
    private static final Vector3f CAMERA_LOCATION = new Vector3f(13.660254F, 5.176381F, 13.660254F);

    @NotNull
    private static final Quaternion CAMERA_ROTATION = new Quaternion(0.9159756F, 0.04995022F, -0.37940952F,
            0.12059049F);

    @NotNull
    private static final Array<String> JME_FORMATS = ArrayFactory.newArray(String.class);

    @NotNull
    private static final Array<String> MODELS_FORMATS = ArrayFactory.newArray(String.class);

    @NotNull
    private static final Array<String> AUDIO_FORMATS = ArrayFactory.newArray(String.class);

    @NotNull
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

    @NotNull
    private static final JMEThreadExecutor EDITOR_THREAD_EXECUTOR = JMEThreadExecutor.getInstance();

    @Nullable
    private static volatile JMEFilePreviewManager instance;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @FromAnyThread
    public static @NotNull JMEFilePreviewManager getInstance() {
        if (instance == null) {
            synchronized (JMEFilePreviewManager.class) {
                if (instance == null) {
                    instance = new JMEFilePreviewManager();
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
    public static boolean isModelFile(@Nullable final Path file) {
        if (file == null) return false;
        final String extension = getExtension(file);
        return MODELS_FORMATS.contains(extension);
    }

    /**
     * Check the file by the asset path.
     *
     * @param assetPath the asset path.
     * @return true is the file is a file of a model.
     */
    @FromAnyThread
    public static boolean isModelFile(@Nullable final String assetPath) {
        if (StringUtils.isEmpty(assetPath)) return false;
        final String extension = getExtension(assetPath);
        return MODELS_FORMATS.contains(extension);
    }

    /**
     * Check the file.
     *
     * @param file the file
     * @return true is the file is a JME file.
     */
    @FromAnyThread
    public static boolean isJmeFile(@Nullable final Path file) {
        if (file == null) return false;
        final String extension = getExtension(file);
        return JME_FORMATS.contains(extension);
    }

    /**
     * Check the file by the asset path.
     *
     * @param assetPath the asset path.
     * @return true is the file is a JME file.
     */
    @FromAnyThread
    public static boolean isJmeFile(@Nullable final String assetPath) {
        if (StringUtils.isEmpty(assetPath)) return false;
        final String extension = getExtension(assetPath);
        return JME_FORMATS.contains(extension);
    }

    /**
     * Check the file.
     *
     * @param file the file
     * @return true is the file is an audio file.
     */
    @FromAnyThread
    public static boolean isAudioFile(@Nullable final Path file) {
        if (file == null) return false;
        final String extension = getExtension(file);
        return AUDIO_FORMATS.contains(extension);
    }

    /**
     * Check the asset path.
     *
     * @param assetPath the asset path
     * @return true is the asset path is an audio file.
     */
    @FromAnyThread
    public static boolean isAudioFile(@Nullable final String assetPath) {
        if (assetPath == null) return false;
        final String extension = getExtension(assetPath);
        return AUDIO_FORMATS.contains(extension);
    }

    @NotNull
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

    private JMEFilePreviewManager() {
        this.imageView = new ImageView();
        this.testBox = new Geometry("Box", new Box(2, 2, 2));
        this.modelNode = new Node("Model Node");

        final ExecutorManager executorManager = ExecutorManager.getInstance();
        executorManager.addFXTask(() -> {

            final JFXApplication application = JFXApplication.getInstance();
            final EditorFXScene scene = application.getScene();
            final StackPane container = scene.getHideLayer();

            FXUtils.addToPane(imageView, container);

            TangentGenerator.useMikktspaceGenerator(testBox);

            final JMEThreadExecutor executor = JMEThreadExecutor.getInstance();
            executor.addToExecute(this::prepareScene);
        });
    }

    @Override
    @JMEThread
    protected void controlUpdate(final float tpf) {

        if (frame == 2) {
            final Editor editor = Editor.getInstance();
            editor.updatePreviewLightProbe(probeHandler);
        }

        frame++;
    }

    @JMEThread
    private void notifyProbeComplete() {
        final Editor editor = Editor.getInstance();
        final Node rootNode = editor.getPreviewNode();
        rootNode.attachChild(modelNode);
    }

    @Override
    @JMEThread
    protected void controlRender(@NotNull final RenderManager renderManager, @NotNull final ViewPort viewPort) {
    }

    /**
     * Show the file.
     *
     * @param file      the file.
     * @param fitWidth  the target width of preview.
     * @param fitHeight the target height of preview.
     */
    @FromAnyThread
    public void show(@NotNull final Path file, final int fitWidth, final int fitHeight) {
        imageView.setFitHeight(fitHeight);
        imageView.setFitWidth(fitWidth);

        final Path assetFile = notNull(getAssetFile(file), "File can't be null.");
        final String path = toAssetPath(assetFile);

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
    public void showExternal(@NotNull final Path file, final int fitWidth, final int fitHeight) {
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
    private void showPreview(@NotNull final String path, @NotNull final String extension, final boolean external) {
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
    public void show(@NotNull final String assetPath, final int fitWidth, final int fitHeight) {
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
    @JMEThread
    private void showObject(@NotNull final String path, final boolean external) {
        prepareProcessor();

        final Editor editor = Editor.getInstance();
        final AssetManager assetManager = editor.getAssetManager();
        final Spatial model;

        FolderAssetLocator.setIgnore(external);
        try {
            model = assetManager.loadModel(path);

            if (external && EDITOR_CONFIG.isAutoTangentGenerating()) {
                TangentGenerator.useMikktspaceGenerator(model);
            }

        } finally {
            FolderAssetLocator.setIgnore(false);
        }

        tryToLoad(model);

        final Node rootNode = editor.getPreviewNode();
        rootNode.detachChild(modelNode);
    }

    /**
     * Try to load and show the model.
     *
     * @param model the model.
     */
    @JMEThread
    private void tryToLoad(@NotNull final Spatial model) {
        try {

            final Editor editor = Editor.getInstance();
            final RenderManager renderManager = editor.getRenderManager();
            renderManager.preloadScene(model);

            modelNode.attachChild(model);

        } catch (final RendererException | AssetNotFoundException | UnsupportedOperationException e) {
            EditorUtil.handleException(LOGGER, this, e);
        }
    }

    /**
     * Prepare the processor to render the a preview object.
     */
    @JMEThread
    private void prepareProcessor() {

        if (processor != null) {
            processor.setEnabled(true);
        }

        frame = 0;

        final Editor editor = Editor.getInstance();
        final Camera camera = editor.getPreviewCamera();
        camera.setLocation(CAMERA_LOCATION);
        camera.setRotation(CAMERA_ROTATION);

        modelNode.detachAllChildren();
    }

    /**
     * Show a j3m material.
     *
     * @param path the path to material.
     */
    @JMEThread
    private void showMaterial(@NotNull final String path) {
        prepareProcessor();

        final Editor editor = Editor.getInstance();
        final AssetManager assetManager = editor.getAssetManager();
        final Material material = assetManager.loadMaterial(path);

        testBox.setMaterial(material);
        tryToLoad(testBox);

        final Node rootNode = editor.getPreviewNode();
        rootNode.detachChild(modelNode);
    }

    /**
     * Clear a preview.
     */
    @FromAnyThread
    public void clear() {
        EDITOR_THREAD_EXECUTOR.addToExecute(this::clearImpl);
    }

    @JMEThread
    private void clearImpl() {

        final Editor editor = Editor.getInstance();
        final Node rootNode = editor.getPreviewNode();
        rootNode.detachChild(modelNode);

        if (processor != null) {
            processor.setEnabled(false);
        }
    }

    /**
     * Gets image view.
     *
     * @return the image view with a preview.
     */
    @FXThread
    public @NotNull ImageView getImageView() {
        return imageView;
    }

    /**
     * Prepare a transfer processor to transfer preview result to a image view.
     *
     * @return the transfer processor.
     */
    @JMEThread
    private @NotNull FrameTransferSceneProcessor prepareScene() {

        final Editor editor = Editor.getInstance();
        final AssetManager assetManager = editor.getAssetManager();
        final Spatial sky = SkyFactory.createSky(assetManager, "graphics/textures/sky/studio.hdr",
                        SkyFactory.EnvMapType.EquirectMap);

        final DirectionalLight light = new DirectionalLight();
        light.setDirection(LIGHT_DIRECTION);

        final Node rootNode = editor.getPreviewNode();
        rootNode.addControl(this);
        rootNode.attachChild(sky);
        rootNode.addLight(light);
        rootNode.attachChild(modelNode);

        processor = bind(editor, imageView, imageView, editor.getPreviewViewPort(), false);
        processor.setTransferMode(ON_CHANGES);
        processor.setEnabled(false);

        return processor;
    }
}
