package com.ss.editor.state.editor.impl.material;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.RendererException;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.SkyFactory;
import com.ss.editor.EditorThread;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.model.tool.TangentGenerator;
import com.ss.editor.state.editor.impl.AdvancedAbstractEditorAppState;
import com.ss.editor.ui.component.editor.impl.material.MaterialFileEditor;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.function.BooleanFloatConsumer;
import com.ss.rlib.geom.util.AngleUtils;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import javafx.scene.input.KeyCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation the 3D part of the {@link MaterialFileEditor}.
 *
 * @author JavaSaBr
 */
public class MaterialEditorAppState extends AdvancedAbstractEditorAppState<MaterialFileEditor> {

    private static final Vector3f QUAD_OFFSET = new Vector3f(0, -2, 2);
    private static final Vector3f LIGHT_DIRECTION = new Vector3f(0.007654993F, 0.39636374F, 0.9180617F).negate();

    private static final float H_ROTATION = AngleUtils.degreeToRadians(75);
    private static final float V_ROTATION = AngleUtils.degreeToRadians(25);

    private static final String KEY_C = "SSEditor.materialEditorState.C";
    private static final String KEY_S = "SSEditor.materialEditorState.S";
    private static final String KEY_P = "SSEditor.materialEditorState.P";
    private static final String KEY_L = "SSEditor.materialEditorState.L";

    static {
        TRIGGERS.put(KEY_C, new KeyTrigger(KeyInput.KEY_C));
        TRIGGERS.put(KEY_S, new KeyTrigger(KeyInput.KEY_S));
        TRIGGERS.put(KEY_P, new KeyTrigger(KeyInput.KEY_P));
        TRIGGERS.put(KEY_L, new KeyTrigger(KeyInput.KEY_L));
    }

    private final JobProgressAdapter<LightProbe> probeHandler = new JobProgressAdapter<LightProbe>() {

        @Override
        public void done(final LightProbe result) {
            if (!isInitialized()) return;
            attachModelNode();
        }
    };

    /**
     * The test box.
     */
    @NotNull
    private final Geometry testBox;

    /**
     * The test sphere.
     */
    @NotNull
    private final Geometry testSphere;

    /**
     * The test quad.
     */
    @NotNull
    private final Geometry testQuad;

    /**
     * The model node.
     */
    @Nullable
    private Node modelNode;

    /**
     * The current model mode.
     */
    @Nullable
    private ModelType currentModelType;

    /**
     * The flag of enabling light.
     */
    private boolean lightEnabled;

    /**
     * The count of frames.
     */
    private int frame;

    /**
     * Instantiates a new Material editor app state.
     *
     * @param fileEditor the file editor
     */
    public MaterialEditorAppState(@NotNull final MaterialFileEditor fileEditor) {
        super(fileEditor);
        this.testBox = new Geometry("Box", new Box(2, 2, 2));
        this.testSphere = new Geometry("Sphere", new Sphere(30, 30, 2));
        this.testQuad = new Geometry("Quad", new Quad(4, 4));
        this.testQuad.setLocalTranslation(QUAD_OFFSET);
        this.lightEnabled = true;

        TangentGenerator.useMikktspaceGenerator(testBox);
        TangentGenerator.useMikktspaceGenerator(testSphere);
        TangentGenerator.useMikktspaceGenerator(testQuad);

        final AssetManager assetManager = EDITOR.getAssetManager();

        final Spatial sky = SkyFactory.createSky(assetManager, "graphics/textures/sky/studio.hdr",
                SkyFactory.EnvMapType.EquirectMap);

        final Node stateNode = getStateNode();
        stateNode.attachChild(sky);

        final DirectionalLight light = notNull(getLightForCamera());
        light.setDirection(LIGHT_DIRECTION);

        final EditorCamera editorCamera = notNull(getEditorCamera());
        editorCamera.setDefaultHorizontalRotation(H_ROTATION);
        editorCamera.setDefaultVerticalRotation(V_ROTATION);
    }

    @Override
    protected void registerActionHandlers(@NotNull final ObjectDictionary<String, BooleanFloatConsumer> actionHandlers) {
        super.registerActionHandlers(actionHandlers);

        final MaterialFileEditor fileEditor = getFileEditor();

        actionHandlers.put(KEY_S, (isPressed, tpf) -> fileEditor.handleKeyAction(KeyCode.S, isPressed, isControlDown(), isButtonMiddleDown()));
        actionHandlers.put(KEY_C, (isPressed, tpf) -> fileEditor.handleKeyAction(KeyCode.C, isPressed, isControlDown(), isButtonMiddleDown()));
        actionHandlers.put(KEY_P, (isPressed, tpf) -> fileEditor.handleKeyAction(KeyCode.P, isPressed, isControlDown(), isButtonMiddleDown()));
        actionHandlers.put(KEY_L, (isPressed, tpf) -> fileEditor.handleKeyAction(KeyCode.L, isPressed, isControlDown(), isButtonMiddleDown()));
    }

    @Override
    protected void registerActionListener(@NotNull final InputManager inputManager) {
        super.registerActionListener(inputManager);
        inputManager.addListener(actionListener, KEY_S, KEY_C, KEY_P, KEY_L);
    }

    /**
     * Attach model node to state node.
     */
    private void attachModelNode() {
        final Node stateNode = getStateNode();
        stateNode.attachChild(modelNode);
    }

    /**
     * @return the test box.
     */
    @NotNull
    private Geometry getTestBox() {
        return testBox;
    }

    /**
     * @return the test quad.
     */
    @NotNull
    private Geometry getTestQuad() {
        return testQuad;
    }

    /**
     * @return the test sphere.
     */
    @NotNull
    private Geometry getTestSphere() {
        return testSphere;
    }

    /**
     * Update the {@link Material}.
     *
     * @param material the material
     */
    public void updateMaterial(@NotNull final Material material) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateMaterialImpl(material));
    }

    /**
     * Update the {@link Material} in the {@link EditorThread}.
     */
    private void updateMaterialImpl(@NotNull final Material material) {

        final Geometry testBox = getTestBox();
        testBox.setMaterial(material);

        final Geometry testQuad = getTestQuad();
        testQuad.setMaterial(material);

        final Geometry testSphere = getTestSphere();
        testSphere.setMaterial(material);

        final RenderManager renderManager = EDITOR.getRenderManager();
        try {
            renderManager.preloadScene(testBox);
        } catch (final RendererException | AssetNotFoundException | UnsupportedOperationException e) {
            EditorUtil.handleException(LOGGER, this, e);
            testBox.setMaterial(EDITOR.getDefaultMaterial());
            testQuad.setMaterial(EDITOR.getDefaultMaterial());
            testSphere.setMaterial(EDITOR.getDefaultMaterial());
        }
    }

    /**
     * @return the model node.
     */
    @NotNull
    private Node getModelNode() {
        return notNull(modelNode);
    }

    /**
     * Change the {@link ModelType}.
     *
     * @param modelType the model type
     */
    public void changeMode(@NotNull final ModelType modelType) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> changeModeImpl(modelType));
    }

    /**
     * Change the {@link ModelType} in the {@link EditorThread}.
     */
    private void changeModeImpl(@NotNull final ModelType modelType) {

        final Node modelNode = getModelNode();
        modelNode.detachAllChildren();

        switch (modelType) {
            case BOX: {
                modelNode.attachChild(getTestBox());
                break;
            }
            case QUAD: {
                modelNode.attachChild(getTestQuad());
                break;
            }
            case SPHERE: {
                modelNode.attachChild(getTestSphere());
                break;
            }
        }

        setCurrentModelType(modelType);
    }

    /**
     * Change the {@link Bucket}.
     *
     * @param bucket the bucket
     */
    public void changeBucketType(@NotNull final Bucket bucket) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> changeBucketTypeImpl(bucket));
    }

    /**
     * Change the {@link Bucket} in the {@link EditorThread}.
     */
    private void changeBucketTypeImpl(@NotNull final Bucket bucket) {

        final Geometry testQuad = getTestQuad();
        testQuad.setQueueBucket(bucket);

        final Geometry testSphere = getTestSphere();
        testSphere.setQueueBucket(bucket);

        final Geometry testBox = getTestBox();
        testBox.setQueueBucket(bucket);
    }

    @Override
    public void initialize(@NotNull final AppStateManager stateManager, @NotNull final Application application) {
        super.initialize(stateManager, application);

        changeModeImpl(getCurrentModelType());

        frame = 0;
    }

    @Override
    public void cleanup() {
        super.cleanup();

        final Node modelNode = getModelNode();
        modelNode.detachAllChildren();

        final Node stateNode = getStateNode();
        stateNode.detachChild(modelNode);
    }

    @NotNull
    @Override
    protected Node getNodeForCamera() {
        if (modelNode == null) modelNode = new Node("ModelNode");
        return modelNode;
    }

    @Override
    protected boolean needMovableCamera() {
        return false;
    }

    @Override
    protected boolean needEditorCamera() {
        return true;
    }

    @Override
    protected boolean needLightForCamera() {
        return true;
    }

    /**
     * @return the current model mode.
     */
    @NotNull
    private ModelType getCurrentModelType() {
        return notNull(currentModelType);
    }

    /**
     * @param currentModelType the current model mode.
     */
    private void setCurrentModelType(@NotNull final ModelType currentModelType) {
        this.currentModelType = currentModelType;
    }

    /**
     * @return true if the light is enabled.
     */
    private boolean isLightEnabled() {
        return lightEnabled;
    }

    /**
     * @param lightEnabled true if the light is enabled.
     */
    private void setLightEnabled(final boolean lightEnabled) {
        this.lightEnabled = lightEnabled;
    }

    /**
     * Update the light in the scene.
     *
     * @param enabled the enabled
     */
    public void updateLightEnabled(final boolean enabled) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateLightEnabledImpl(enabled));
    }

    /**
     * Update the light in the scene in the {@link EditorThread}.
     */
    private void updateLightEnabledImpl(boolean enabled) {
        if (enabled == isLightEnabled()) return;

        final DirectionalLight light = getLightForCamera();
        final Node stateNode = getStateNode();

        if (enabled) {
            stateNode.addLight(light);
        } else {
            stateNode.removeLight(light);
        }

        setLightEnabled(enabled);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (frame == 2) EDITOR.updateProbe(probeHandler);

        final Geometry testQuad = getTestQuad();

        if (testQuad.getParent() != null) {

            final Quaternion localRotation = testQuad.getLocalRotation();
            final Camera camera = EDITOR.getCamera();

            localRotation.lookAt(camera.getLocation(), camera.getUp());
            testQuad.setLocalRotation(localRotation);
        }

        frame++;
    }

    @Override
    protected void undo() {
        final MaterialFileEditor fileEditor = getFileEditor();
        fileEditor.undo();
    }

    @Override
    protected void redo() {
        final MaterialFileEditor fileEditor = getFileEditor();
        fileEditor.redo();
    }

    @Override
    protected boolean needUpdateCameraLight() {
        return false;
    }

    @Override
    protected void notifyChangedCamera(@NotNull final Vector3f cameraLocation, final float hRotation,
                                       final float vRotation, final float targetDistance) {
        EXECUTOR_MANAGER.addFXTask(() -> getFileEditor().notifyChangedCamera(cameraLocation, hRotation, vRotation, targetDistance));
    }

    /**
     * The enum Model type.
     */
    public enum ModelType {
        /**
         * Sphere model type.
         */
        SPHERE,
        /**
         * Box model type.
         */
        BOX,
        /**
         * Quad model type.
         */
        QUAD;

        private static final ModelType[] VALUES = values();

        /**
         * Value of model type.
         *
         * @param index the index
         * @return the model type
         */
        public static ModelType valueOf(final int index) {
            return VALUES[index];
        }
    }
}
