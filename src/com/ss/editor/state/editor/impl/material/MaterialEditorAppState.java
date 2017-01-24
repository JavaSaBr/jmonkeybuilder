package com.ss.editor.state.editor.impl.material;

import static java.util.Objects.requireNonNull;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
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

import org.jetbrains.annotations.NotNull;

import rlib.geom.util.AngleUtils;

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
    private final Geometry testBox;

    /**
     * The test sphere.
     */
    private final Geometry testSphere;

    /**
     * The test quad.
     */
    private final Geometry testQuad;

    /**
     * The model node.
     */
    private Node modelNode;

    /**
     * The current model mode.
     */
    private ModelType currentModelType;

    /**
     * THe flag of enabling light.
     */
    private boolean lightEnabled;

    /**
     * The count of frames.
     */
    private int frame;

    public MaterialEditorAppState(final MaterialFileEditor fileEditor) {
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
        final Spatial sky = SkyFactory.createSky(assetManager, "graphics/textures/sky/studio.hdr", SkyFactory.EnvMapType.EquirectMap);

        final Node stateNode = getStateNode();
        stateNode.attachChild(sky);

        final DirectionalLight light = requireNonNull(getLightForCamera());
        light.setDirection(LIGHT_DIRECTION);

        final EditorCamera editorCamera = requireNonNull(getEditorCamera());
        editorCamera.setDefaultHorizontalRotation(H_ROTATION);
        editorCamera.setDefaultVerticalRotation(V_ROTATION);
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
    private Geometry getTestBox() {
        return testBox;
    }

    /**
     * @return the test quad.
     */
    private Geometry getTestQuad() {
        return testQuad;
    }

    /**
     * @return the test sphere.
     */
    private Geometry getTestSphere() {
        return testSphere;
    }

    /**
     * Update the {@link Material}.
     */
    public void updateMaterial(final Material material) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateMaterialImpl(material));
    }

    /**
     * Update the {@link Material} in the {@link EditorThread}.
     */
    private void updateMaterialImpl(final Material material) {

        final Geometry testBox = getTestBox();
        testBox.setMaterial(material);

        final Geometry testQuad = getTestQuad();
        testQuad.setMaterial(material);

        final Geometry testSphere = getTestSphere();
        testSphere.setMaterial(material);
    }

    /**
     * @return the model node.
     */
    private Node getModelNode() {
        return modelNode;
    }

    /**
     * Change the {@link ModelType}.
     */
    public void changeMode(final ModelType modelType) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> changeModeImpl(modelType));
    }

    /**
     * Change the {@link ModelType} in the {@link EditorThread}.
     */
    private void changeModeImpl(final ModelType modelType) {

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
     */
    public void changeBucketType(final Bucket bucket) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> changeBucketTypeImpl(bucket));
    }

    /**
     * Change the {@link Bucket} in the {@link EditorThread}.
     */
    private void changeBucketTypeImpl(final Bucket bucket) {

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

        final ModelType currentModelType = getCurrentModelType();
        if (currentModelType != null) changeModeImpl(currentModelType);

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
    private ModelType getCurrentModelType() {
        return currentModelType;
    }

    /**
     * @param currentModelType the current model mode.
     */
    private void setCurrentModelType(final ModelType currentModelType) {
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

    public enum ModelType {
        SPHERE,
        BOX,
        QUAD;

        private static final ModelType[] VALUES = values();

        public static ModelType valueOf(final int index) {
            return VALUES[index];
        }
    }
}
