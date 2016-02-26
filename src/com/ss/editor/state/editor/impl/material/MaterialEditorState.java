package com.ss.editor.state.editor.impl.material;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.SkyFactory;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.state.editor.impl.AbstractEditorState;


/**
 * Реализация 3D части редактирования материала.
 *
 * @author Ronn
 */
public class MaterialEditorState extends AbstractEditorState {

    public static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    public static enum ModelType {
        SPHERE,
        BOX,
        QUAD,
    }

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

    public MaterialEditorState() {
        this.testBox = new Geometry("Box", new Box(2, 2, 2));
        this.testSphere = new Geometry("Sphere", new Sphere(30, 30, 2));
        this.testQuad = new Geometry("Quad", new Quad(4, 4));
        this.lightEnabled = true;

        final Node stateNode = getStateNode();
        stateNode.attachChild(modelNode);

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Spatial sky = SkyFactory.createSky(assetManager, "graphics/textures/sky/path.hdr", SkyFactory.EnvMapType.EquirectMap);

        stateNode.attachChild(sky);
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

    @Override
    public void initialize(final AppStateManager stateManager, final Application application) {
        super.initialize(stateManager, application);

        final ModelType currentModelType = getCurrentModelType();

        if(currentModelType != null) {
            changeModeImpl(currentModelType);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();

        final Node modelNode = getModelNode();
        modelNode.detachAllChildren();
    }

    @Override
    protected Node getNodeForChaseCamera() {
        this.modelNode = new Node("ModelNode");
        return modelNode;
    }

    @Override
    protected boolean needChaseCamera() {
        return true;
    }

    @Override
    protected boolean needLightForChaseCamera() {
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
     * @param lightEnabled активирован ли свет камеры.
     */
    private void setLightEnabled(boolean lightEnabled) {
        this.lightEnabled = lightEnabled;
    }

    /**
     * @return активирован ли свет камеры.
     */
    private boolean isLightEnabled() {
        return lightEnabled;
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

        if(enabled == isLightEnabled()) {
            return;
        }

        final DirectionalLight light = getLightForChaseCamera();
        final Node stateNode = getStateNode();

        if(enabled) {
            stateNode.addLight(light);
        } else{
            stateNode.removeLight(light);
        }

        setLightEnabled(enabled);
    }
}
