package com.ss.editor.state.editor.impl.model;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.input.InputManager;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.debug.WireSphere;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.state.editor.impl.AbstractEditorState;
import com.ss.editor.ui.component.editor.impl.model.ModelFileEditor;

import rlib.geom.util.AngleUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реализация 3D части редактора модели.
 *
 * @author Ronn
 */
public class ModelEditorState extends AbstractEditorState<ModelFileEditor> {

    private static final float H_ROTATION = AngleUtils.degreeToRadians(45);
    private static final float V_ROTATION = AngleUtils.degreeToRadians(15);

    private final JobProgressAdapter<LightProbe> probeHandler = new JobProgressAdapter<LightProbe>() {

        @Override
        public void done(final LightProbe result) {

            if (!isInitialized()) {
                return;
            }

            notifyProbeComplete();
        }
    };

    /**
     * Набор кастомных фонов.
     */
    private final Array<Spatial> customSky;

    /**
     * Узел для размещения модели.
     */
    private final Node modelNode;

    /**
     * Узел для размещения вспомогательных графических элементов.
     */
    private final Node toolNode;

    /**
     * Узел для размещения кастомного фона.
     */
    private final Node customSkyNode;

    /**
     * Сетка сцены.
     */
    private Geometry grid;

    /**
     * Материал для выделения.
     */
    protected Material selectionMaterial;

    /**
     * Выбранная часть модели.
     */
    protected Spatial selected;

    /**
     * Модель выделения выбранной части.
     */
    protected Spatial selectionShape;

    /**
     * Отображать ли выбранный узел.
     */
    protected boolean showSelection;

    /**
     * Отображать ли сетку.
     */
    protected boolean showGrid;

    /**
     * Узел на который смотрит камера.
     */
    private Node cameraNode;

    /**
     * Текущая отображаемая модель.
     */
    private Spatial currentModel;

    /**
     * Текущее быстрое окружение.
     */
    private Spatial currentFastSky;

    /**
     * Активирован ли свет камеры.
     */
    private boolean lightEnabled;

    /**
     * Кол-во кадров.
     */
    private int frame;

    public ModelEditorState(final ModelFileEditor fileEditor) {
        super(fileEditor);
        this.modelNode = new Node("ModelNode");
        this.modelNode.setUserData(ModelEditorState.class.getName(), true);
        this.toolNode = new Node("ToolNode");
        this.customSkyNode = new Node("Custom Sky");
        this.customSky = ArrayFactory.newArray(Spatial.class);

        final Node stateNode = getStateNode();
        stateNode.attachChild(getCameraNode());
        stateNode.attachChild(getCustomSkyNode());

        setLightEnabled(true);
        createToolElements();

        final EditorCamera editorCamera = getEditorCamera();
        editorCamera.setDefaultHorizontalRotation(H_ROTATION);
        editorCamera.setDefaultVerticalRotation(V_ROTATION);

        setShowSelection(true);
        setShowGrid(true);
    }

    /**
     * @return узел для размещения кастомного фона.
     */
    private Node getCustomSkyNode() {
        return customSkyNode;
    }

    /**
     * @return Набор кастомных фонов.
     */
    private Array<Spatial> getCustomSky() {
        return customSky;
    }

    @Override
    protected void onActionImpl(final String name, final boolean isPressed, final float tpf) {
        super.onActionImpl(name, isPressed, tpf);

        if(MOUSE_RIGHT_CLICK.equals(name)) {
            processClick(isPressed);
        }
    }

    /**
     * Обработка клика мышкой по области редактора.
     */
    private void processClick(final boolean isPressed) {

        if (!isPressed) {
            return;
        }

        final Camera camera = EDITOR.getCamera();

        final InputManager inputManager = EDITOR.getInputManager();
        final Vector2f cursor = inputManager.getCursorPosition();
        final Vector3f click3d = camera.getWorldCoordinates(cursor, 0f);
        final Vector3f dir = camera.getWorldCoordinates(cursor, 1f).subtractLocal(click3d).normalizeLocal();

        final Ray ray = new Ray();
        ray.setOrigin(click3d);
        ray.setDirection(dir);

        final CollisionResults results = new CollisionResults();

        final Node modelNode = getModelNode();
        modelNode.collideWith(ray, results);

        final ModelFileEditor editor = getFileEditor();

        if (results.size() < 1) {
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifySelected(null));
            return;
        }

        final CollisionResult collision = results.getClosestCollision();

        if (collision == null) {
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifySelected(null));
            return;
        }

        EXECUTOR_MANAGER.addFXTask(() -> editor.notifySelected(collision.getGeometry()));
    }

    /**
     * Создание вспомогательных элементов.
     */
    private void createToolElements() {

        selectionMaterial = createColorMaterial(new ColorRGBA(1F, 170 / 255F, 64 / 255F, 1F));

        //grid
        grid = new Geometry("grid", new Grid(20, 20, 1.0f));
        grid.setMaterial(createColorMaterial(ColorRGBA.Gray));
        grid.setLocalTranslation(-10, 0, -10);

        final Node toolNode = getToolNode();
        toolNode.attachChild(grid);
    }

    /**
     * @return сетка.
     */
    private Geometry getGrid() {
        return grid;
    }

    /**
     * Создание материала для отображения граф. элемнтов.
     */
    private Material createColorMaterial(final ColorRGBA color) {
        final Material material = new Material(EDITOR.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setWireframe(true);
        material.setColor("Color", color);
        return material;
    }

    /**
     * @return узел для размещения вспомогательных графических элементов.
     */
    private Node getToolNode() {
        return toolNode;
    }

    @Override
    protected Node getNodeForCamera() {

        if(cameraNode == null) {
            cameraNode = new Node("CameraNode");
        }

        return cameraNode;
    }

    /**
     * @return узел на который смотрит камера.
     */
    private Node getCameraNode() {
        return cameraNode;
    }

    /**
     * Активая узла с моделями.
     */
    private void notifyProbeComplete() {

        final Node stateNode = getStateNode();
        stateNode.attachChild(getModelNode());
        stateNode.attachChild(getToolNode());

        final Node customSkyNode = getCustomSkyNode();
        customSkyNode.detachAllChildren();
    }

    /**
     * @param currentFastSky текущее быстрое окружение.
     */
    private void setCurrentFastSky(final Spatial currentFastSky) {
        this.currentFastSky = currentFastSky;
    }

    /**
     * @return текущее быстрое окружение.
     */
    private Spatial getCurrentFastSky() {
        return currentFastSky;
    }

    /**
     * @return узел для размещения модели.
     */
    private Node getModelNode() {
        return modelNode;
    }

    /**
     * Отобразить на сцене указанную модель.
     */
    public void openModel(final Spatial model) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> openModelImpl(model));
    }

    /**
     * Процесс отображения указанной модели.
     */
    private void openModelImpl(final Spatial model) {

        final Node modelNode = getModelNode();
        modelNode.attachChild(model);

        setCurrentModel(model);
    }

    /**
     * @param currentModel текущая отображаемая модель.
     */
    private void setCurrentModel(final Spatial currentModel) {
        this.currentModel = currentModel;
    }

    /**
     * @return текущая отображаемая модель.
     */
    private Spatial getCurrentModel() {
        return currentModel;
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

    @Override
    public void initialize(final AppStateManager stateManager, final Application application) {
        super.initialize(stateManager, application);

        frame = 0;
    }

    @Override
    public void cleanup() {
        super.cleanup();

        final Node stateNode = getStateNode();
        stateNode.detachChild(getModelNode());
        stateNode.detachChild(getToolNode());
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (frame == 2) {

            final Node customSkyNode = getCustomSkyNode();

            final Array<Spatial> customSky = getCustomSky();
            customSky.forEach(spatial -> customSkyNode.attachChild(spatial.clone(false)));

            EDITOR.updateProbe(probeHandler);
        }

        final Spatial selected = getSelected();
        final Spatial selectionShape = getSelectionShape();

        if (selected != null && selectionShape != null) {
            selectionShape.setLocalTranslation(selected.getWorldTranslation());
            selectionShape.setLocalRotation(selected.getWorldRotation());
            selectionShape.setLocalScale(selected.getWorldScale());
        }

        frame++;
    }

    @Override
    protected boolean needEditorCamera() {
        return true;
    }

    @Override
    protected boolean needUpdateCameraLight() {
        return true;
    }

    @Override
    protected boolean needLightForCamera() {
        return true;
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

    /**
     * Обработка смены быстрого окружения редактора.
     */
    public void changeFastSky(final Spatial fastSky) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> changeFastSkyImpl(fastSky));
    }

    /**
     * Процесс смены окружения редактора.
     */
    private void changeFastSkyImpl(final Spatial fastSky) {

        final Node stateNode = getStateNode();
        final Spatial currentFastSky = getCurrentFastSky();

        if (currentFastSky != null) {
            stateNode.detachChild(currentFastSky);
        }

        if (fastSky != null) {
            stateNode.attachChild(fastSky);
        }

        stateNode.detachChild(getModelNode());
        stateNode.detachChild(getToolNode());

        setCurrentFastSky(fastSky);

        frame = 0;
    }

    /**
     * @return выбранная часть модели.
     */
    private Spatial getSelected() {
        return selected;
    }

    /**
     * @param selected выбранная часть модели.
     */
    private void setSelected(Spatial selected) {
        this.selected = selected;
    }

    /**
     * @return модель выделения выбранной части.
     */
    private Spatial getSelectionShape() {
        return selectionShape;
    }

    /**
     * @param selectionShape модель выделения выбранной части.
     */
    private void setSelectionShape(Spatial selectionShape) {
        this.selectionShape = selectionShape;
    }

    /**
     * @return материал для выделения.
     */
    private Material getSelectionMaterial() {
        return selectionMaterial;
    }

    /**
     * Обновление выбранной части модели.
     */
    public void updateSelection(final Spatial spatial) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateSelectionImpl(spatial));
    }

    /**
     * Процесс обновления выбранной части модели.
     */
    private void updateSelectionImpl(final Spatial spatial) {

        if (getSelected() == spatial) {
            return;
        }

        final Node toolNode = getToolNode();
        final Spatial selectionShape = getSelectionShape();

        if (spatial == null && selectionShape != null) {
            toolNode.detachChild(selectionShape);
            setSelectionShape(null);
            setSelected(null);
            return;
        } else if (spatial == null) {
            setSelected(null);
            return;
        }

        if (selectionShape != null) {
            toolNode.detachChild(selectionShape);
        }

        Spatial shape = null;

        if (spatial instanceof ParticleEmitter) {
            shape = buildBoxSelection(spatial);
        } else if (spatial instanceof Geometry) {
            shape = buildGeometrySelection((Geometry) spatial);
        } else {
            shape = buildBoxSelection(spatial);
        }

        if (shape == null) {
            setSelectionShape(null);
            setSelected(null);
            return;
        }

        if (isShowSelection()) {
            toolNode.attachChild(shape);
        }

        setSelected(spatial);
        setSelectionShape(shape);
    }


    /**
     * Построение выделения для модели.
     *
     * @param spatial выделяемая модель.
     * @return выделение этой модели.
     */
    private Spatial buildBoxSelection(final Spatial spatial) {

        final BoundingVolume bound = spatial.getWorldBound();

        if (bound instanceof BoundingBox) {

            final BoundingBox boundingBox = (BoundingBox) bound;

            final Geometry geometry = WireBox.makeGeometry(boundingBox);
            geometry.setName("SelectionShape");
            geometry.setMaterial(getSelectionMaterial());
            geometry.setLocalTranslation(boundingBox.getCenter().subtract(spatial.getWorldTranslation()));

            return geometry;

        } else if (bound instanceof BoundingSphere) {

            final BoundingSphere boundingSphere = (BoundingSphere) bound;

            final WireSphere wire = new WireSphere();
            wire.fromBoundingSphere(boundingSphere);

            final Geometry geometry = new Geometry("SelectionShape", wire);
            geometry.setMaterial(getSelectionMaterial());
            geometry.setLocalTranslation(boundingSphere.getCenter().subtract(spatial.getWorldTranslation()));

            return geometry;
        }

        return null;
    }

    /**
     * Построение выделения указанной геометрии.
     *
     * @param spatial геометря которую надо выделить.
     * @return выделение этой геометрии.
     */
    private Spatial buildGeometrySelection(final Geometry spatial) {

        final Mesh mesh = spatial.getMesh();

        if (mesh == null) {
            return null;
        }

        final Geometry geometry = new Geometry("SelectionShape", mesh);
        geometry.setMaterial(getSelectionMaterial());
        geometry.setLocalTransform(spatial.getWorldTransform());

        return geometry;
    }

    /**
     * @param showGrid отображать ли сетку.
     */
    private void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    /**
     * @param showSelection отображать ли выбранный узел.
     */
    private void setShowSelection(boolean showSelection) {
        this.showSelection = showSelection;
    }

    /**
     * @return отображать ли сетку.
     */
    private boolean isShowGrid() {
        return showGrid;
    }

    /**
     * @return отображать ли выбранный узел.
     */
    private boolean isShowSelection() {
        return showSelection;
    }

    /**
     * Обновленине видимости выделения.
     */
    public void updateShowSelection(final boolean showSelection) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateShowSelectionImpl(showSelection));
    }

    /**
     * Процесс обновления видимости выделения.
     */
    private void updateShowSelectionImpl(boolean showSelection) {

        if (isShowSelection() == showSelection) {
            return;
        }

        final Spatial selectionShape = getSelectionShape();
        final Node toolNode = getToolNode();

        if (showSelection && selectionShape != null) {
            toolNode.attachChild(selectionShape);
        } else if (!showSelection && selectionShape != null) {
            toolNode.detachChild(selectionShape);
        }

        setShowSelection(showSelection);
    }

    /**
     * Обновление отображения сетки.
     */
    public void updateShowGrid(final boolean showGrid) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateShowGridImpl(showGrid));
    }

    /**
     * Процесс обновления отображения сетки.
     */
    private void updateShowGridImpl(final boolean showGrid) {

        if (isShowGrid() == showGrid) {
            return;
        }

        final Node toolNode = getToolNode();
        final Geometry grid = getGrid();

        if (showGrid) {
            toolNode.attachChild(grid);
        } else {
            toolNode.detachChild(grid);
        }

        setShowGrid(showGrid);
    }

    /**
     * Добавление кастомного фона.
     */
    public void addCustomSky(final Spatial sky) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> addCustomSkyImpl(sky));
    }

    /**
     * Процесс добавление кастомного фона.
     */
    private void addCustomSkyImpl(Spatial sky) {
        final Array<Spatial> customSky = getCustomSky();
        customSky.add(sky);
    }

    /**
     * Удаление кастомного фона.
     */
    public void removeCustomSky(final Spatial sky) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> removeCustomSkyImpl(sky));
    }

    /**
     * Процесс удаления кастомного фона.
     */
    private void removeCustomSkyImpl(Spatial sky) {
        final Array<Spatial> customSky = getCustomSky();
        customSky.slowRemove(sky);
    }

    /**
     * Обновить цвето-пробу.
     */
    public void updateLightProbe() {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Node stateNode = getStateNode();
            stateNode.detachChild(getModelNode());
            stateNode.detachChild(getToolNode());

            frame = 0;
        });
    }
}
