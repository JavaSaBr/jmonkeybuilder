package com.ss.editor.state.editor.impl.material;

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
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.SkyFactory;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.model.tool.TangentGenerator;
import com.ss.editor.state.editor.impl.AbstractEditorState;
import com.ss.editor.ui.component.editor.impl.material.MaterialFileEditor;

import rlib.geom.util.AngleUtils;

/**
 * Реализация 3D части редактирования материала.
 *
 * @author Ronn
 */
public class MaterialEditorState extends AbstractEditorState<MaterialFileEditor> {

    private static final Vector3f QUAD_OFFSET = new Vector3f(0, -2, 2);
    private static final Vector3f LIGHT_DIRECTION = new Vector3f(0.007654993F, 0.39636374F, 0.9180617F).negate();

    private static final float H_ROTATION = AngleUtils.degreeToRadians(75);
    private static final float V_ROTATION = AngleUtils.degreeToRadians(25);

    private final JobProgressAdapter<LightProbe> probeHandler = new JobProgressAdapter<LightProbe>() {

        @Override
        public void done(final LightProbe result) {

            if (!isInitialized()) {
                return;
            }

            attachModelNode();
        }
    };

    /**
     * Тестовый бокс.
     */
    private final Geometry testBox;

    /**
     * Тестовая сфера.
     */
    private final Geometry testSphere;

    /**
     * Тестовая плоскость.
     */
    private final Geometry testQuad;

    /**
     * Узел для размещения тестовой модели.
     */
    private Node modelNode;

    /**
     * Текущий режим.
     */
    private ModelType currentModelType;

    /**
     * Активирован ли свет камеры.
     */
    private boolean lightEnabled;

    /**
     * Кол-во кадров.
     */
    private int frame;

    public MaterialEditorState(final MaterialFileEditor fileEditor) {
        super(fileEditor);
        this.testBox = new Geometry("Box", new Box(2, 2, 2));
        this.testSphere = new Geometry("Sphere", new Sphere(30, 30, 2));
        this.testQuad = new Geometry("Quad", new Quad(4, 4));
        this.testQuad.setLocalTranslation(QUAD_OFFSET);
        this.lightEnabled = true;

        TangentGenerator.useStandardGenerator(testBox, false);
        TangentGenerator.useStandardGenerator(testSphere, false);
        TangentGenerator.useStandardGenerator(testQuad, false);

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Spatial sky = SkyFactory.createSky(assetManager, "graphics/textures/sky/studio.hdr", SkyFactory.EnvMapType.EquirectMap);

        final Node stateNode = getStateNode();
        stateNode.attachChild(sky);

        final DirectionalLight light = getLightForCamera();
        light.setDirection(LIGHT_DIRECTION);

        final EditorCamera editorCamera = getEditorCamera();
        editorCamera.setDefaultHorizontalRotation(H_ROTATION);
        editorCamera.setDefaultVerticalRotation(V_ROTATION);
    }

    /**
     * Активая узла с моделями.
     */
    private void attachModelNode() {
        final Node stateNode = getStateNode();
        stateNode.attachChild(modelNode);
    }

    /**
     * @return тестовый бокс.
     */
    private Geometry getTestBox() {
        return testBox;
    }

    /**
     * @return тестовая плоскость.
     */
    private Geometry getTestQuad() {
        return testQuad;
    }

    /**
     * @return тестовая сфера.
     */
    private Geometry getTestSphere() {
        return testSphere;
    }

    /**
     * Обновление материала.
     */
    public void updateMaterial(final Material material) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateMaterialImpl(material));
    }

    /**
     * Процесс обновления материала в потоке редаткора.
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
     * @return узел для размещения тестовой модели.
     */
    private Node getModelNode() {
        return modelNode;
    }

    /**
     * Смена режима отображения.
     */
    public void changeMode(final ModelType modelType) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> changeModeImpl(modelType));
    }

    /**
     * Процесс смены в потоке редактора.
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
     * Смена типа Bucket.
     */
    public void changeBucketType(final RenderQueue.Bucket bucket) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> changeBucketTypeImpl(bucket));
    }

    /**
     * Процесс смены типа Bucket.
     */
    private void changeBucketTypeImpl(final RenderQueue.Bucket bucket) {

        final Geometry testQuad = getTestQuad();
        testQuad.setQueueBucket(bucket);

        final Geometry testSphere = getTestSphere();
        testSphere.setQueueBucket(bucket);

        final Geometry testBox = getTestBox();
        testBox.setQueueBucket(bucket);
    }

    @Override
    public void initialize(final AppStateManager stateManager, final Application application) {
        super.initialize(stateManager, application);

        final ModelType currentModelType = getCurrentModelType();

        if (currentModelType != null) {
            changeModeImpl(currentModelType);
        }

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

    @Override
    protected Node getNodeForCamera() {

        if (modelNode == null) {
            modelNode = new Node("ModelNode");
        }

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
     * @return текущий режим.
     */
    private ModelType getCurrentModelType() {
        return currentModelType;
    }

    /**
     * @param currentModelType текущий режим.
     */
    private void setCurrentModelType(final ModelType currentModelType) {
        this.currentModelType = currentModelType;
    }

    /**
     * @return активирован ли свет камеры.
     */
    private boolean isLightEnabled() {
        return lightEnabled;
    }

    /**
     * @param lightEnabled активирован ли свет камеры.
     */
    private void setLightEnabled(boolean lightEnabled) {
        this.lightEnabled = lightEnabled;
    }

    /**
     * Обновление активированности света от камеры.
     */
    public void updateLightEnabled(final boolean enabled) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateLightEnabledImpl(enabled));
    }

    /**
     * Процесс обновление света от камеры.
     */
    private void updateLightEnabledImpl(boolean enabled) {

        if (enabled == isLightEnabled()) {
            return;
        }

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

        if (frame == 2) {
            EDITOR.updateProbe(probeHandler);
        }

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

    public enum ModelType {
        SPHERE,
        BOX,
        QUAD,
    }
}
