package com.ss.editor.manager;

import static com.jme3x.jfx.injfx.JmeToJFXIntegrator.bind;
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
import com.ss.editor.executor.impl.EditorThreadExecutor;
import com.ss.editor.model.tool.TangentGenerator;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.ui.util.FXUtils;
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
    private static final Array<String> AUDIO_FORMATS = ArrayFactory.newArray(String.class);

    static {
        JME_FORMATS.add(FileExtensions.JME_MATERIAL);
        JME_FORMATS.add(FileExtensions.JME_OBJECT);
        AUDIO_FORMATS.add(FileExtensions.AUDIO_OGG);
        AUDIO_FORMATS.add(FileExtensions.AUDIO_MP3);
        AUDIO_FORMATS.add(FileExtensions.AUDIO_WAV);
    }

    @NotNull
    private static final EditorThreadExecutor EDITOR_THREAD_EXECUTOR = EditorThreadExecutor.getInstance();

    @NotNull
    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    @NotNull
    private static final Editor EDITOR = Editor.getInstance();

    @Nullable
    private static volatile JMEFilePreviewManager instance;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static JMEFilePreviewManager getInstance() {
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
     * Is jme file boolean.
     *
     * @param file the file
     * @return true is the file is a JME file.
     */
    public static boolean isJmeFile(@Nullable final Path file) {
        if (file == null) return false;
        final String extension = getExtension(file);
        return JME_FORMATS.contains(extension);
    }

    /**
     * Is audio file boolean.
     *
     * @param file the file
     * @return true is the file is a JME file.
     */
    public static boolean isAudioFile(@Nullable final Path file) {
        if (file == null) return false;
        final String extension = getExtension(file);
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

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final StackPane container = scene.getHideLayer();
        FXUtils.addToPane(imageView, container);

        TangentGenerator.useMikktspaceGenerator(testBox);

        final EditorThreadExecutor executor = EditorThreadExecutor.getInstance();
        executor.addToExecute(this::prepareScene);
    }

    @Override
    protected void controlUpdate(final float tpf) {

        if (frame == 2) {
            EDITOR.updatePreviewProbe(probeHandler);
        }

        frame++;
    }

    @JMEThread
    private void notifyProbeComplete() {
        final Node rootNode = EDITOR.getPreviewNode();
        rootNode.attachChild(modelNode);
    }

    @Override
    protected void controlRender(@NotNull final RenderManager renderManager, @NotNull final ViewPort viewPort) {
    }

    /**
     * Show a file.
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
        final String extension = getExtension(assetFile);

        if (FileExtensions.JME_MATERIAL.equals(extension)) {
            EDITOR_THREAD_EXECUTOR.addToExecute(() -> showMaterial(path));
        } else if (FileExtensions.JME_OBJECT.equals(extension)) {
            EDITOR_THREAD_EXECUTOR.addToExecute(() -> showObject(path));
        } else {
            EDITOR_THREAD_EXECUTOR.addToExecute(this::clear);
        }
    }

    /**
     * Show a j3o object.
     *
     * @param path the path to object.
     */
    @JMEThread
    private void showObject(@NotNull final String path) {
        if (processor != null) processor.setEnabled(true);

        frame = 0;

        final Camera camera = EDITOR.getPreviewCamera();
        camera.setLocation(CAMERA_LOCATION);
        camera.setRotation(CAMERA_ROTATION);

        modelNode.detachAllChildren();

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Spatial model = assetManager.loadModel(path);
        try {

            final RenderManager renderManager = EDITOR.getRenderManager();
            renderManager.preloadScene(model);

            modelNode.attachChild(model);

        } catch (final RendererException | AssetNotFoundException | UnsupportedOperationException e) {
            EditorUtil.handleException(LOGGER, this, e);
        }

        final Node rootNode = EDITOR.getPreviewNode();
        rootNode.detachChild(modelNode);
    }

    /**
     * Show a j3m material.
     *
     * @param path the path to material.
     */
    @JMEThread
    private void showMaterial(@NotNull final String path) {
        if (processor != null) processor.setEnabled(true);

        frame = 0;

        final Camera camera = EDITOR.getPreviewCamera();
        camera.setLocation(CAMERA_LOCATION);
        camera.setRotation(CAMERA_ROTATION);

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Material material = assetManager.loadMaterial(path);

        modelNode.detachAllChildren();

        testBox.setMaterial(material);
        try {

            final RenderManager renderManager = EDITOR.getRenderManager();
            renderManager.preloadScene(testBox);

            modelNode.attachChild(testBox);

        } catch (final RendererException | AssetNotFoundException | UnsupportedOperationException e) {
            EditorUtil.handleException(LOGGER, this, e);
        }

        final Node rootNode = EDITOR.getPreviewNode();
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

        final Node rootNode = EDITOR.getPreviewNode();
        rootNode.detachChild(modelNode);

        if (processor != null) processor.setEnabled(false);
    }

    /**
     * Gets image view.
     *
     * @return the image view with a preview.
     */
    @NotNull
    @FXThread
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Prepare a transfer processor to transfer preview result to a image view.
     *
     * @return the transfer processor.
     */
    @NotNull
    private FrameTransferSceneProcessor prepareScene() {

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Spatial sky = SkyFactory.createSky(assetManager, "graphics/textures/sky/studio.hdr",
                        SkyFactory.EnvMapType.EquirectMap);

        final DirectionalLight light = new DirectionalLight();
        light.setDirection(LIGHT_DIRECTION);

        final Node rootNode = EDITOR.getPreviewNode();
        rootNode.addControl(this);
        rootNode.attachChild(sky);
        rootNode.addLight(light);
        rootNode.attachChild(modelNode);

        processor = bind(EDITOR, imageView, imageView, EDITOR.getPreviewViewPort(), false);
        processor.setEnabled(false);

        return processor;
    }
}
