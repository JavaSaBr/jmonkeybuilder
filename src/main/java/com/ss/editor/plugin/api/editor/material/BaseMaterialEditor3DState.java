package com.ss.editor.plugin.api.editor.material;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.RendererException;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.EditorThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JMEThread;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.model.tool.TangentGenerator;
import com.ss.editor.plugin.api.editor.part3d.AdvancedPBRWithStudioSky3DEditorState;
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
public class BaseMaterialEditor3DState<T extends BaseMaterialFileEditor> extends AdvancedPBRWithStudioSky3DEditorState<T> {

    @NotNull
    private static final Vector3f QUAD_OFFSET = new Vector3f(0, -2, 2);

    @NotNull
    private static final Vector3f LIGHT_DIRECTION = new Vector3f(0.007654993F, 0.39636374F, 0.9180617F).negate();

    private static final float H_ROTATION = AngleUtils.degreeToRadians(75);
    private static final float V_ROTATION = AngleUtils.degreeToRadians(25);

    @NotNull
    private static final String KEY_C = "SSEditor.materialEditorState.C";

    @NotNull
    private static final String KEY_S = "SSEditor.materialEditorState.S";

    @NotNull
    private static final String KEY_P = "SSEditor.materialEditorState.P";

    @NotNull
    private static final String KEY_L = "SSEditor.materialEditorState.L";

    static {
        TRIGGERS.put(KEY_C, new KeyTrigger(KeyInput.KEY_C));
        TRIGGERS.put(KEY_S, new KeyTrigger(KeyInput.KEY_S));
        TRIGGERS.put(KEY_P, new KeyTrigger(KeyInput.KEY_P));
        TRIGGERS.put(KEY_L, new KeyTrigger(KeyInput.KEY_L));
    }

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
     * The current model mode.
     */
    @Nullable
    private ModelType currentModelType;

    /**
     * The flag of enabling light.
     */
    private boolean lightEnabled;

    public BaseMaterialEditor3DState(@NotNull final T fileEditor) {
        super(fileEditor);
        this.testBox = new Geometry("Box", new Box(2, 2, 2));
        this.testSphere = new Geometry("Sphere", new Sphere(30, 30, 2));
        this.testQuad = new Geometry("Quad", new Quad(4, 4));
        this.testQuad.setLocalTranslation(QUAD_OFFSET);
        this.lightEnabled = MaterialFileEditor.DEFAULT_LIGHT_ENABLED;

        TangentGenerator.useMikktspaceGenerator(testBox);
        TangentGenerator.useMikktspaceGenerator(testSphere);
        TangentGenerator.useMikktspaceGenerator(testQuad);

        final DirectionalLight light = notNull(getLightForCamera());
        light.setDirection(LIGHT_DIRECTION);

        final EditorCamera editorCamera = notNull(getEditorCamera());
        editorCamera.setDefaultHorizontalRotation(H_ROTATION);
        editorCamera.setDefaultVerticalRotation(V_ROTATION);

        getModelNode().attachChild(getNodeForCamera());
    }

    @Override
    @JMEThread
    protected void registerActionHandlers(@NotNull final ObjectDictionary<String, BooleanFloatConsumer> actionHandlers) {
        super.registerActionHandlers(actionHandlers);

        final T fileEditor = getFileEditor();

        actionHandlers.put(KEY_S, (isPressed, tpf) -> fileEditor.handleKeyAction(KeyCode.S, isPressed, isControlDown(), isButtonMiddleDown()));
        actionHandlers.put(KEY_C, (isPressed, tpf) -> fileEditor.handleKeyAction(KeyCode.C, isPressed, isControlDown(), isButtonMiddleDown()));
        actionHandlers.put(KEY_P, (isPressed, tpf) -> fileEditor.handleKeyAction(KeyCode.P, isPressed, isControlDown(), isButtonMiddleDown()));
        actionHandlers.put(KEY_L, (isPressed, tpf) -> fileEditor.handleKeyAction(KeyCode.L, isPressed, isControlDown(), isButtonMiddleDown()));
    }

    @Override
    @JMEThread
    protected void registerActionListener(@NotNull final InputManager inputManager) {
        super.registerActionListener(inputManager);
        inputManager.addListener(actionListener, KEY_S, KEY_C, KEY_P, KEY_L);
    }

    /**
     * @return the test box.
     */
    @JMEThread
    protected @NotNull Geometry getTestBox() {
        return testBox;
    }

    /**
     * @return the test quad.
     */
    @JMEThread
    protected @NotNull Geometry getTestQuad() {
        return testQuad;
    }

    /**
     * @return the test sphere.
     */
    @JMEThread
    protected @NotNull Geometry getTestSphere() {
        return testSphere;
    }

    /**
     * Update the {@link Material}.
     *
     * @param material the material
     */
    @FromAnyThread
    public void updateMaterial(@NotNull final Material material) {
        EXECUTOR_MANAGER.addJMETask(() -> updateMaterialImpl(material));
    }

    /**
     * Update the {@link Material} in the {@link EditorThread}.
     *
     * @param material the new material.
     */
    @JMEThread
    protected void updateMaterialImpl(@NotNull final Material material) {

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
     * Change the {@link ModelType}.
     *
     * @param modelType the model type
     */
    @FromAnyThread
    public void changeMode(@NotNull final ModelType modelType) {
        EXECUTOR_MANAGER.addJMETask(() -> changeModeImpl(modelType));
    }

    /**
     * Change the {@link ModelType} in the {@link EditorThread}.
     *
     * @param modelType the new model type.
     */
    @JMEThread
    protected void changeModeImpl(@NotNull final ModelType modelType) {

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
    @FromAnyThread
    public void changeBucketType(@NotNull final Bucket bucket) {
        EXECUTOR_MANAGER.addJMETask(() -> changeBucketTypeImpl(bucket));
    }

    /**
     * Change the {@link Bucket} in the {@link EditorThread}.
     *
     * @param bucket the new bucket.
     */
    @JMEThread
    protected void changeBucketTypeImpl(@NotNull final Bucket bucket) {

        final Geometry testQuad = getTestQuad();
        testQuad.setQueueBucket(bucket);

        final Geometry testSphere = getTestSphere();
        testSphere.setQueueBucket(bucket);

        final Geometry testBox = getTestBox();
        testBox.setQueueBucket(bucket);
    }

    @Override
    @JMEThread
    public void initialize(@NotNull final AppStateManager stateManager, @NotNull final Application application) {
        super.initialize(stateManager, application);
        changeModeImpl(getCurrentModelType());
    }

    @Override
    @JMEThread
    protected boolean needMovableCamera() {
        return false;
    }

    @Override
    @JMEThread
    protected boolean needEditorCamera() {
        return true;
    }

    @Override
    @JMEThread
    protected boolean needLightForCamera() {
        return true;
    }

    /**
     * @return the current model mode.
     */
    @JMEThread
    protected @NotNull ModelType getCurrentModelType() {
        return notNull(currentModelType);
    }

    /**
     * @param currentModelType the current model mode.
     */
    @JMEThread
    protected void setCurrentModelType(@NotNull final ModelType currentModelType) {
        this.currentModelType = currentModelType;
    }

    /**
     * @return true if the light is enabled.
     */
    @JMEThread
    protected boolean isLightEnabled() {
        return lightEnabled;
    }

    /**
     * @param lightEnabled true if the light is enabled.
     */
    @JMEThread
    protected void setLightEnabled(final boolean lightEnabled) {
        this.lightEnabled = lightEnabled;
    }

    /**
     * Update the light in the scene.
     *
     * @param enabled the enabled
     */
    @FromAnyThread
    public void updateLightEnabled(final boolean enabled) {
        EXECUTOR_MANAGER.addJMETask(() -> updateLightEnabledImpl(enabled));
    }

    /**
     * Update the light in the scene in the {@link EditorThread}.
     *
     * @param enabled true if light should be enabled.
     */
    @JMEThread
    protected void updateLightEnabledImpl(final boolean enabled) {
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
    @JMEThread
    protected boolean needUpdateCameraLight() {
        return false;
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
