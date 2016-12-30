package com.ss.editor.state.editor.impl.model;

import static com.ss.editor.state.editor.impl.model.ModelEditorUtils.findToSelect;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.input.InputManager;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
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
import com.jme3.scene.shape.Quad;
import com.ss.editor.control.light.EditorLightControl;
import com.ss.editor.control.transform.MoveToolControl;
import com.ss.editor.control.transform.RotationToolControl;
import com.ss.editor.control.transform.ScaleToolControl;
import com.ss.editor.control.transform.SceneEditorControl;
import com.ss.editor.control.transform.TransformControl;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.state.editor.impl.AbstractEditorAppState;
import com.ss.editor.ui.component.editor.impl.model.ModelFileEditor;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyOperation;
import com.ss.editor.util.GeomUtils;
import com.ss.editor.util.NodeUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import rlib.geom.util.AngleUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.array.ArrayIterator;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

/**
 * The implementation of the {@link AbstractEditorAppState} for the {@link ModelFileEditor}.
 *
 * @author JavaSaBr
 */
public class ModelEditorAppState extends AbstractEditorAppState<ModelFileEditor> implements SceneEditorControl {

    public static final String USER_DATA_IS_LIGHT = ModelEditorAppState.class.getName() + ".isLight";

    private static final float H_ROTATION = AngleUtils.degreeToRadians(45);
    private static final float V_ROTATION = AngleUtils.degreeToRadians(15);

    private final JobProgressAdapter<LightProbe> probeHandler = new JobProgressAdapter<LightProbe>() {

        @Override
        public void done(final LightProbe result) {
            if (!isInitialized()) return;
            notifyProbeComplete();
        }
    };


    /**
     * The table with models for presentation of the lights.
     */
    protected static final ObjectDictionary<Light.Type, Spatial> LIGHT_MODEL_TABLE;

    static {

        final AssetManager assetManager = EDITOR.getAssetManager();

        LIGHT_MODEL_TABLE = DictionaryFactory.newObjectDictionary();
        LIGHT_MODEL_TABLE.put(Light.Type.Point, assetManager.loadModel("graphics/models/light/point.j3o"));
        LIGHT_MODEL_TABLE.put(Light.Type.Directional, assetManager.loadModel("graphics/models/light/sun.j3o"));
        LIGHT_MODEL_TABLE.put(Light.Type.Spot, assetManager.loadModel("graphics/models/light/spot_lamp.j3o"));
        LIGHT_MODEL_TABLE.put(Light.Type.Probe, assetManager.loadModel("graphics/models/light/point.j3o"));
    }

    /**
     * The selection models of selected models.
     */
    protected final ObjectDictionary<Spatial, Spatial> selectionShape;

    /**
     * The array controls for lights.
     */
    protected final Array<EditorLightControl> editorLightControls;

    /**
     * The array of custom skies.
     */
    private final Array<Spatial> customSky;

    /**
     * The array of selected models.
     */
    protected final Array<Spatial> selected;

    /**
     * The node for the placement of models.
     */
    private final Node modelNode;

    /**
     * The node for the placement of lights.
     */
    private final Node lightNode;

    /**
     * The node for the placement of controls.
     */
    private final Node toolNode;

    /**
     * The node for the placement of transform controls.
     */
    private final Node transformToolNode;

    /**
     * The node for the placement of custom sky.
     */
    private final Node customSkyNode;

    /**
     * The nodes for the placement of model controls.
     */
    private Node moveTool, rotateTool, scaleTool;

    /**
     * The plane for calculation transforms.
     */
    private Node collisionPlane;

    /**
     * The difference between the previous point of transformation and new.
     */
    private Vector3f deltaVector;

    /**
     * Center of transformation.
     */
    private Transform transformCenter;

    /**
     * The original transformation.
     */
    private Transform originalTransform;

    /**
     * Object to transform.
     */
    private Spatial toTransform;

    /**
     * Material for selection.
     */
    private Material selectionMaterial;

    /**
     * Grid of the scene.
     */
    private Geometry grid;

    /**
     * The node on which the camera is looking.
     */
    private Node cameraNode;

    /**
     * Current display model.
     */
    private Spatial currentModel;

    /**
     * The current fast sky.
     */
    private Spatial currentFastSky;

    /**
     * The current type of transformation.
     */
    private TransformType transformType;

    /**
     * The current direction of transformation.
     */
    private PickedAxis pickedAxis;

    /**
     * The flag of activity light of the camera.
     */
    private boolean lightEnabled;

    /**
     * The flag of visibility selection.
     */
    private boolean showSelection;

    /**
     * The flag of visibility grid.
     */
    private boolean showGrid;

    /**
     * The flag of existing active transformation.
     */
    private boolean activeTransform;

    /**
     * The frame rate.
     */
    private int frame;

    public ModelEditorAppState(final ModelFileEditor fileEditor) {
        super(fileEditor);
        this.modelNode = new Node("ModelNode");
        this.modelNode.setUserData(ModelEditorAppState.class.getName(), true);
        this.toolNode = new Node("ToolNode");
        this.transformToolNode = new Node("TransformToolNode");
        this.customSkyNode = new Node("Custom Sky");
        this.lightNode = new Node("Lights");
        this.customSky = ArrayFactory.newArray(Spatial.class);
        this.selected = ArrayFactory.newArray(Spatial.class);
        this.selectionShape = DictionaryFactory.newObjectDictionary();
        this.editorLightControls = ArrayFactory.newArray(EditorLightControl.class);

        final Node stateNode = getStateNode();
        stateNode.attachChild(getCameraNode());
        stateNode.attachChild(getCustomSkyNode());

        createCollisionPlane();
        createToolElements();
        createManipulators();

        final EditorCamera editorCamera = Objects.requireNonNull(getEditorCamera());
        editorCamera.setDefaultHorizontalRotation(H_ROTATION);
        editorCamera.setDefaultVerticalRotation(V_ROTATION);

        setLightEnabled(true);
        setShowSelection(true);
        setShowGrid(true);
        setTransformType(TransformType.MOVE_TOOL);
    }

    /**
     * @return the node for the placement of lights.
     */
    @NotNull
    protected Node getLightNode() {
        return lightNode;
    }

    /**
     * @return the array controls for lights.
     */
    @NotNull
    protected Array<EditorLightControl> getEditorLightControls() {
        return editorLightControls;
    }

    /**
     * @param activeTransform the flag of existing active transformation.
     */
    private void setActiveTransform(final boolean activeTransform) {
        this.activeTransform = activeTransform;
    }

    /**
     * @return true of we have active transformation.
     */
    private boolean isActiveTransform() {
        return activeTransform;
    }

    /**
     * @return the node for the placement of transform controls.
     */
    @NotNull
    private Node getTransformToolNode() {
        return transformToolNode;
    }

    /**
     * @return the node for the placement of custom sky.
     */
    @NotNull
    private Node getCustomSkyNode() {
        return customSkyNode;
    }

    /**
     * @return the array of custom skies.
     */
    @NotNull
    private Array<Spatial> getCustomSky() {
        return customSky;
    }

    @Override
    protected void onActionImpl(@NotNull final String name, final boolean isPressed, final float tpf) {
        super.onActionImpl(name, isPressed, tpf);
        if (MOUSE_RIGHT_CLICK.equals(name)) {
            processClick(isPressed);
        } else if (MOUSE_LEFT_CLICK.equals(name)) {
            if (isPressed) startTransform();
            else endTransform();
        }
    }

    /**
     * Handling a click in the area of the ditor.
     */
    private void processClick(final boolean isPressed) {
        if (!isPressed) return;

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
        modelNode.updateModelBound();
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

        EXECUTOR_MANAGER.addFXTask(() -> editor.notifySelected(findToSelect(collision.getGeometry())));
    }

    /**
     * Create tool elementns.
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
     * Create manipulators.
     */
    private void createManipulators() {

        final AssetManager assetManager = EDITOR.getAssetManager();

        final Material redMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        redMaterial.setColor("Color", ColorRGBA.Red);

        final Material blueMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        blueMaterial.setColor("Color", ColorRGBA.Blue);

        final Material greenMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        greenMaterial.setColor("Color", ColorRGBA.Green);

        moveTool = (Node) assetManager.loadModel("graphics/models/manipulators/manipulators_move.j3o");
        moveTool.getChild("move_x").setMaterial(redMaterial);
        moveTool.getChild("collision_move_x").setMaterial(redMaterial);
        moveTool.getChild("collision_move_x").setCullHint(Spatial.CullHint.Always);
        moveTool.getChild("move_y").setMaterial(blueMaterial);
        moveTool.getChild("collision_move_y").setMaterial(blueMaterial);
        moveTool.getChild("collision_move_y").setCullHint(Spatial.CullHint.Always);
        moveTool.getChild("move_z").setMaterial(greenMaterial);
        moveTool.getChild("collision_move_z").setMaterial(greenMaterial);
        moveTool.getChild("collision_move_z").setCullHint(Spatial.CullHint.Always);
        moveTool.scale(0.1f);
        moveTool.addControl(new MoveToolControl(this));

        rotateTool = (Node) assetManager.loadModel("graphics/models/manipulators/manipulators_rotate.j3o");
        rotateTool.getChild("rot_x").setMaterial(redMaterial);
        rotateTool.getChild("collision_rot_x").setMaterial(redMaterial);
        rotateTool.getChild("collision_rot_x").setCullHint(Spatial.CullHint.Always);
        rotateTool.getChild("rot_y").setMaterial(blueMaterial);
        rotateTool.getChild("collision_rot_y").setMaterial(blueMaterial);
        rotateTool.getChild("collision_rot_y").setCullHint(Spatial.CullHint.Always);
        rotateTool.getChild("rot_z").setMaterial(greenMaterial);
        rotateTool.getChild("collision_rot_z").setMaterial(greenMaterial);
        rotateTool.getChild("collision_rot_z").setCullHint(Spatial.CullHint.Always);
        rotateTool.scale(0.1f);
        rotateTool.addControl(new RotationToolControl(this));

        scaleTool = (Node) assetManager.loadModel("graphics/models/manipulators/manipulators_scale.j3o");
        scaleTool.getChild("scale_x").setMaterial(redMaterial);
        scaleTool.getChild("collision_scale_x").setMaterial(redMaterial);
        scaleTool.getChild("collision_scale_x").setCullHint(Spatial.CullHint.Always);
        scaleTool.getChild("scale_y").setMaterial(blueMaterial);
        scaleTool.getChild("collision_scale_y").setMaterial(blueMaterial);
        scaleTool.getChild("collision_scale_y").setCullHint(Spatial.CullHint.Always);
        scaleTool.getChild("scale_z").setMaterial(greenMaterial);
        scaleTool.getChild("collision_scale_z").setMaterial(greenMaterial);
        scaleTool.getChild("collision_scale_z").setCullHint(Spatial.CullHint.Always);
        scaleTool.scale(0.1f);
        scaleTool.addControl(new ScaleToolControl(this));
    }

    /**
     * Create collision plane.
     */
    private void createCollisionPlane() {

        final AssetManager assetManager = EDITOR.getAssetManager();

        final Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        final RenderState renderState = material.getAdditionalRenderState();
        renderState.setFaceCullMode(RenderState.FaceCullMode.Off);
        renderState.setWireframe(true);

        final float size = 20000;

        final Geometry geometry = new Geometry("plane", new Quad(size, size));
        geometry.setMaterial(material);
        geometry.setLocalTranslation(-size / 2, -size / 2, 0);

        collisionPlane = new Node();
        collisionPlane.attachChild(geometry);
    }

    /**
     * @param transformType the current type of the transform.
     */
    public void setTransformType(final TransformType transformType) {
        this.transformType = transformType;
    }

    @Override
    @Nullable
    public Transform getTransformCenter() {
        return transformCenter;
    }

    /**
     * @param pickedAxis the current direction of transformation.
     */
    public void setPickedAxis(@Nullable final PickedAxis pickedAxis) {
        this.pickedAxis = pickedAxis;
    }

    @Override
    @Nullable
    public PickedAxis getPickedAxis() {
        return pickedAxis;
    }

    @Override
    @NotNull
    public Node getCollisionPlane() {
        if (collisionPlane == null) throw new RuntimeException("collisionPlane is null");
        return collisionPlane;
    }

    @Override
    public void setDeltaVector(@Nullable final Vector3f deltaVector) {
        this.deltaVector = deltaVector;
    }

    @Override
    @Nullable
    public Vector3f getDeltaVector() {
        return deltaVector;
    }

    @Override
    @Nullable
    public Spatial getToTransform() {
        return toTransform;
    }

    @Override
    public void notifyTransformed(@NotNull final Spatial spatial) {
        final ModelFileEditor fileEditor = getFileEditor();
        fileEditor.notifyTransformed(spatial);
    }

    /**
     * @return the current type of transformation.
     */
    @Nullable
    private TransformType getTransformType() {
        return transformType;
    }

    /**
     * @return grid of the scene.
     */
    @NotNull
    private Geometry getGrid() {
        return grid;
    }

    /**
     * Create the material for presentation of selected models.
     */
    private Material createColorMaterial(final ColorRGBA color) {
        final Material material = new Material(EDITOR.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setWireframe(true);
        material.setColor("Color", color);
        return material;
    }

    /**
     * @return the node for the placement of controls.
     */
    @NotNull
    private Node getToolNode() {
        return toolNode;
    }

    @Override
    @NotNull
    protected Node getNodeForCamera() {
        if (cameraNode == null) cameraNode = new Node("CameraNode");
        return cameraNode;
    }

    /**
     * @return the node on which the camera is looking.
     */
    @NotNull
    private Node getCameraNode() {
        return cameraNode;
    }

    /**
     * Activate the node with models.
     */
    private void notifyProbeComplete() {

        final Node stateNode = getStateNode();
        stateNode.attachChild(getModelNode());
        stateNode.attachChild(getToolNode());
        stateNode.attachChild(getLightNode());

        final Node customSkyNode = getCustomSkyNode();
        customSkyNode.detachAllChildren();
    }

    /**
     * @param currentFastSky the current fast sky.
     */
    private void setCurrentFastSky(@Nullable final Spatial currentFastSky) {
        this.currentFastSky = currentFastSky;
    }

    /**
     * @return the current fast sky.
     */
    @Nullable
    private Spatial getCurrentFastSky() {
        return currentFastSky;
    }

    /**
     * @return the node for the placement of models.
     */
    @NotNull
    private Node getModelNode() {
        return modelNode;
    }

    /**
     * Show the model in the scene.
     */
    public void openModel(@NotNull final Spatial model) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> openModelImpl(model));
    }

    /**
     * The process of showing the model in the scene.
     */
    private void openModelImpl(@NotNull final Spatial model) {

        final Node modelNode = getModelNode();
        final Spatial currentModel = getCurrentModel();
        if (currentModel != null) modelNode.detachChild(currentModel);

        modelNode.attachChild(model);

        setCurrentModel(model);
    }

    /**
     * @param currentModel current display model.
     */
    private void setCurrentModel(@Nullable final Spatial currentModel) {
        this.currentModel = currentModel;
    }

    /**
     * @return current display model.
     */
    @Nullable
    public Spatial getCurrentModel() {
        return currentModel;
    }

    /**
     * @return true if the light of the camera is enabled.
     */
    private boolean isLightEnabled() {
        return lightEnabled;
    }

    /**
     * @param lightEnabled the flag of activity light of the camera.
     */
    private void setLightEnabled(final boolean lightEnabled) {
        this.lightEnabled = lightEnabled;
    }

    @Override
    public void initialize(@NotNull final AppStateManager stateManager, @NotNull final Application application) {
        super.initialize(stateManager, application);
        frame = 0;
    }

    @Override
    public void cleanup() {
        super.cleanup();

        final Node stateNode = getStateNode();
        stateNode.detachChild(getModelNode());
        stateNode.detachChild(getToolNode());
        stateNode.detachChild(getLightNode());
    }

    /**
     * @return the nodes for the placement of model controls.
     */
    @NotNull
    private Node getMoveTool() {
        return moveTool;
    }

    /**
     * @return the nodes for the placement of model controls.
     */
    @NotNull
    private Node getRotateTool() {
        return rotateTool;
    }

    /**
     * @return the nodes for the placement of model controls.
     */
    @NotNull
    private Node getScaleTool() {
        return scaleTool;
    }

    @Override
    public void update(final float tpf) {
        super.update(tpf);

        if (frame == 2) {

            final Node customSkyNode = getCustomSkyNode();

            final Array<Spatial> customSky = getCustomSky();
            customSky.forEach(spatial -> customSkyNode.attachChild(spatial.clone(false)));

            EDITOR.updateProbe(probeHandler);
        }

        final Node transformToolNode = getTransformToolNode();
        final Transform selectionCenter = getTransformCenter();

        // Transform Selected Objects!
        if (isActiveTransform() && selectionCenter != null) {
            if (transformType == TransformType.MOVE_TOOL) {
                final TransformControl control = getMoveTool().getControl(TransformControl.class);
                transformToolNode.detachAllChildren();
                control.processTransform();
            } else if (transformType == TransformType.ROTATE_TOOL) {
                final TransformControl control = getRotateTool().getControl(TransformControl.class);
                transformToolNode.detachAllChildren();
                control.processTransform();
            } else if (transformType == TransformType.SCALE_TOOL) {
                final TransformControl control = getScaleTool().getControl(TransformControl.class);
                transformToolNode.detachAllChildren();
                control.processTransform();
            }
        }

        final EditorCamera editorCamera = getEditorCamera();

        if (editorCamera != null) {
            editorCamera.update(tpf);
        }

        final ObjectDictionary<Spatial, Spatial> selectionShape = getSelectionShape();
        final Array<Spatial> selected = getSelected();
        selected.forEach(spatial -> {

            final Spatial shape = selectionShape.get(spatial);
            if (shape == null) return;

            shape.setLocalTranslation(spatial.getWorldTranslation());
            shape.setLocalRotation(spatial.getWorldRotation());
            shape.setLocalScale(spatial.getWorldScale());

            updateTransformNode(spatial.getLocalTransform());
        });

        final Node toolNode = getToolNode();
        transformToolNode.detachAllChildren();

        final TransformType transformType = getTransformType();

        if (transformType == TransformType.MOVE_TOOL) {
            transformToolNode.attachChild(getMoveTool());
        } else if (transformType == TransformType.ROTATE_TOOL) {
            transformToolNode.attachChild(getRotateTool());
        } else if (transformType == TransformType.SCALE_TOOL) {
            transformToolNode.attachChild(getScaleTool());
        }

        if (selected.isEmpty()) {
            toolNode.detachChild(transformToolNode);
        } else {
            toolNode.attachChild(transformToolNode);
        }

        frame++;
    }

    @Override
    protected void undo() {
        final ModelFileEditor fileEditor = getFileEditor();
        fileEditor.undo();
    }

    @Override
    protected void redo() {
        final ModelFileEditor fileEditor = getFileEditor();
        fileEditor.redo();
    }

    /**
     * Update the transformation node.
     */
    protected void updateTransformNode(final Transform transform) {
        if (transform == null) return;

        final Vector3f location = transform.getTranslation();
        final Vector3f positionOnCamera = getPositionOnCamera(location);

        final Node transformToolNode = getTransformToolNode();
        transformToolNode.setLocalTranslation(positionOnCamera);
        transformToolNode.setLocalRotation(transform.getRotation());
    }

    private Vector3f getPositionOnCamera(@NotNull final Vector3f location) {
        final Camera camera = EDITOR.getCamera();
        final Vector3f resultPosition = location.subtract(camera.getLocation()).normalize().multLocal(camera.getFrustumNear() + 0.4f);
        return camera.getLocation().add(resultPosition);
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
     * Update light.
     */
    public void updateLightEnabled(final boolean enabled) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateLightEnabledImpl(enabled));
    }

    /**
     * The process of updating the light.
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

    /**
     * Change the fast sky.
     */
    public void changeFastSky(final Spatial fastSky) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> changeFastSkyImpl(fastSky));
    }

    /**
     * The process of changing the fast sky.
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
        stateNode.detachChild(getLightNode());

        setCurrentFastSky(fastSky);

        frame = 0;
    }

    /**
     * @return the array of selected models.
     */
    @NotNull
    private Array<Spatial> getSelected() {
        return selected;
    }

    /**
     * @return the selection models of selected models.
     */
    @NotNull
    private ObjectDictionary<Spatial, Spatial> getSelectionShape() {
        return selectionShape;
    }

    /**
     * @return material for selection.
     */
    @Nullable
    private Material getSelectionMaterial() {
        return selectionMaterial;
    }

    /**
     * Update selected models.
     */
    public void updateSelection(final Array<Spatial> spatials) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateSelectionImpl(spatials));
    }

    /**
     * The process of updating selected models.
     */
    private void updateSelectionImpl(final Array<Spatial> spatials) {

        final Array<Spatial> selected = getSelected();

        for (final ArrayIterator<Spatial> iterator = selected.iterator(); iterator.hasNext(); ) {

            final Spatial spatial = iterator.next();
            if (spatials.contains(spatial)) continue;

            removeFromSelection(spatial);
            iterator.fastRemove();
        }

        for (final Spatial spatial : spatials) {
            if (!selected.contains(spatial)) {
                addToSelection(spatial);
            }
        }

        updateToTransform();
    }

    /**
     * Update transformation.
     */
    private void updateToTransform() {
        setToTransform(getSelected().first());
    }

    /**
     * @return the original transformation.
     */
    @Nullable
    private Transform getOriginalTransform() {
        return originalTransform;
    }

    /**
     * @param originalTransform the original transformation.
     */
    private void setOriginalTransform(@Nullable final Transform originalTransform) {
        this.originalTransform = originalTransform;
    }

    /**
     * Update the transformation's center.
     */
    private void updateTransformCenter() {

        final Spatial toTransform = getToTransform();
        final Transform transform = toTransform == null ? null : toTransform.getLocalTransform().clone();
        final Transform originalTransform = transform == null ? null : transform.clone();

        setTransformCenter(transform);
        setOriginalTransform(originalTransform);
    }

    /**
     * Add the spatial to selection.
     */
    private void addToSelection(@NotNull final Spatial spatial) {

        Spatial shape;

        if (spatial instanceof ParticleEmitter) {
            shape = buildBoxSelection(spatial);
        } else if (spatial instanceof Geometry) {
            shape = buildGeometrySelection((Geometry) spatial);
        } else {
            shape = buildBoxSelection(spatial);
        }

        if (shape == null) return;

        if (isShowSelection()) {
            final Node toolNode = getToolNode();
            toolNode.attachChild(shape);
        }

        final Array<Spatial> selected = getSelected();
        selected.add(spatial);

        final ObjectDictionary<Spatial, Spatial> selectionShape = getSelectionShape();
        selectionShape.put(spatial, shape);
    }

    /**
     * Remove the spatial from the selection.
     */
    private void removeFromSelection(@NotNull final Spatial spatial) {
        setTransformCenter(null);
        setToTransform(null);

        final ObjectDictionary<Spatial, Spatial> selectionShape = getSelectionShape();
        final Spatial shape = selectionShape.remove(spatial);

        if (shape != null) shape.removeFromParent();
    }

    /**
     * Build the selection box for the spatial.
     */
    private Spatial buildBoxSelection(@NotNull final Spatial spatial) {
        spatial.updateModelBound();

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

        final Geometry geometry = WireBox.makeGeometry(new BoundingBox(Vector3f.ZERO, 1, 1, 1));
        geometry.setName("SelectionShape");
        geometry.setMaterial(getSelectionMaterial());
        geometry.setLocalTranslation(spatial.getWorldTranslation());

        return geometry;
    }

    /**
     * Build selection grid for the geometry.
     */
    private Spatial buildGeometrySelection(@NotNull final Geometry spatial) {

        final Mesh mesh = spatial.getMesh();
        if (mesh == null) return null;

        final Geometry geometry = new Geometry("SelectionShape", mesh);
        geometry.setMaterial(getSelectionMaterial());
        geometry.setLocalTransform(spatial.getWorldTransform());

        return geometry;
    }

    /**
     * @param showGrid the flag of visibility grid.
     */
    private void setShowGrid(final boolean showGrid) {
        this.showGrid = showGrid;
    }

    /**
     * @param showSelection the flag of visibility selection.
     */
    private void setShowSelection(final boolean showSelection) {
        this.showSelection = showSelection;
    }

    /**
     * @return true if the grid is showed.
     */
    private boolean isShowGrid() {
        return showGrid;
    }

    /**
     * @return true if the selection is showed.
     */
    private boolean isShowSelection() {
        return showSelection;
    }

    /**
     * Update the showing selection.
     */
    public void updateShowSelection(final boolean showSelection) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateShowSelectionImpl(showSelection));
    }

    /**
     * The process of updating the showing selection.
     */
    private void updateShowSelectionImpl(final boolean showSelection) {
        if (isShowSelection() == showSelection) return;

        final ObjectDictionary<Spatial, Spatial> selectionShape = getSelectionShape();
        final Node toolNode = getToolNode();

        if (showSelection && !selectionShape.isEmpty()) {
            selectionShape.forEach(toolNode::attachChild);
        } else if (!showSelection && !selectionShape.isEmpty()) {
            selectionShape.forEach(toolNode::detachChild);
        }

        setShowSelection(showSelection);
    }

    /**
     * Update the showing grid.
     */
    public void updateShowGrid(final boolean showGrid) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateShowGridImpl(showGrid));
    }

    /**
     * The process of updating the showing grid.
     */
    private void updateShowGridImpl(final boolean showGrid) {
        if (isShowGrid() == showGrid) return;

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
     * Add the custom sky.
     */
    public void addCustomSky(@NotNull final Spatial sky) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> addCustomSkyImpl(sky));
    }

    /**
     * The process of adding the custom sky.
     */
    private void addCustomSkyImpl(@NotNull final Spatial sky) {
        final Array<Spatial> customSky = getCustomSky();
        customSky.add(sky);
    }

    /**
     * Add the light.
     */
    public void addLight(@NotNull final Light light) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> addLightImpl(light));
    }

    /**
     * The process of adding the light.
     */
    private void addLightImpl(final Light light) {

        final Spatial original = LIGHT_MODEL_TABLE.get(light.getType());
        if (original == null) return;

        final Spatial newModel = original.clone();
        newModel.setUserData(USER_DATA_IS_LIGHT, Boolean.TRUE);
        newModel.setLocalScale(0.02F);

        final Geometry geometry = NodeUtils.findGeometry(newModel);

        if (geometry == null) {
            LOGGER.warning(this, "not found geometry for the node " + newModel);
            return;
        }

        final Material material = geometry.getMaterial();
        material.setColor("Color", light.getColor());

        final EditorLightControl editorLightControl = new EditorLightControl(light);
        newModel.addControl(editorLightControl);

        final Array<EditorLightControl> lights = getEditorLightControls();
        lights.add(editorLightControl);

        final Node lightNode = getLightNode();
        lightNode.attachChild(newModel);
    }

    /**
     * Remove the custom sky.
     */
    public void removeCustomSky(@NotNull final Spatial sky) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> removeCustomSkyImpl(sky));
    }

    /**
     * The process of removing the custom sky.
     */
    private void removeCustomSkyImpl(@NotNull final Spatial sky) {
        final Array<Spatial> customSky = getCustomSky();
        customSky.slowRemove(sky);
    }

    /**
     * Remove the light.
     */
    public void removeLight(@NotNull final Light light) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> removeLightImpl(light));
    }

    /**
     * The process of removing the light.
     */
    private void removeLightImpl(@NotNull final Light light) {

        EditorLightControl control = null;

        final Array<EditorLightControl> lights = getEditorLightControls();

        for (final EditorLightControl lightControl : lights) {
            if (lightControl.getLight() == light) {
                control = lightControl;
                break;
            }
        }

        if (control == null) return;

        final Spatial spatial = control.getSpatial();
        spatial.removeFromParent();

        lights.fastRemove(control);
    }

    /**
     * Update the light probe.
     */
    public void updateLightProbe() {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Node stateNode = getStateNode();
            stateNode.detachChild(getModelNode());
            stateNode.detachChild(getToolNode());
            stateNode.detachChild(getLightNode());

            frame = 0;
        });
    }

    /**
     * Finish the transformation of the model.
     */
    private void endTransform() {
        if (!isActiveTransform()) return;

        final Transform originalTransform = getOriginalTransform();
        final Spatial toTransform = getToTransform();
        final Spatial currentModel = getCurrentModel();

        if (currentModel == null) {
            LOGGER.warning(this, "not found current model for finishing transform...");
            return;
        } else if (originalTransform == null || toTransform == null) {
            LOGGER.warning(this, "not found originalTransform or toTransform");
            return;
        }

        final Transform oldValue = originalTransform.clone();
        final Transform newValue = toTransform.getLocalTransform().clone();

        final int index = GeomUtils.getIndex(currentModel, toTransform);

        final ModelPropertyOperation<Spatial, Transform> operation = new ModelPropertyOperation<>(index, "transform", newValue, oldValue);
        operation.setApplyHandler(Spatial::setLocalTransform);

        final ModelFileEditor fileEditor = getFileEditor();
        fileEditor.execute(operation);

        setPickedAxis(PickedAxis.NONE);
        setActiveTransform(false);
        setDeltaVector(null);
        updateTransformCenter();
    }

    /**
     * Start transformation.
     */
    public boolean startTransform() {
        updateTransformCenter();

        final Camera camera = EDITOR.getCamera();
        final InputManager inputManager = EDITOR.getInputManager();
        final Vector2f cursorPosition = inputManager.getCursorPosition();

        final CollisionResults collisionResults = new CollisionResults();

        final Vector3f position = camera.getWorldCoordinates(cursorPosition, 0f);
        final Vector3f direction = camera.getWorldCoordinates(cursorPosition, 1f);
        direction.subtractLocal(position).normalizeLocal();

        final Ray ray = new Ray();
        ray.setOrigin(position);
        ray.setDirection(direction);

        final Node transformToolNode = getTransformToolNode();
        transformToolNode.collideWith(ray, collisionResults);

        if (collisionResults.size() < 1) return false;

        final CollisionResult collisionResult = collisionResults.getClosestCollision();
        final TransformType transformType = getTransformType();

        if (transformType == TransformType.MOVE_TOOL) {
            final Node moveTool = getMoveTool();
            final TransformControl control = moveTool.getControl(TransformControl.class);
            control.setCollisionPlane(collisionResult);
        } else if (transformType == TransformType.ROTATE_TOOL) {
            final Node rotateTool = getRotateTool();
            final TransformControl control = rotateTool.getControl(TransformControl.class);
            control.setCollisionPlane(collisionResult);
        } else if (transformType == TransformType.SCALE_TOOL) {
            final Node scaleTool = getScaleTool();
            final TransformControl control = scaleTool.getControl(TransformControl.class);
            control.setCollisionPlane(collisionResult);
        }

        setActiveTransform(true);
        return true;
    }

    /**
     * @param toTransform the object to transform.
     */
    private void setToTransform(@Nullable final Spatial toTransform) {
        this.toTransform = toTransform;
    }

    /**
     * @param transformCenter the center of transformation.
     */
    private void setTransformCenter(@Nullable final Transform transformCenter) {
        this.transformCenter = transformCenter;
    }

    @Override
    protected void notifyChangedCamera(@NotNull final Vector3f cameraLocation, final float hRotation,
                                       final float vRotation, final float targetDistance) {
        EXECUTOR_MANAGER.addFXTask(() -> getFileEditor().notifyChangedCamera(cameraLocation, hRotation, vRotation, targetDistance));
    }

    @Override
    public String toString() {
        return "ModelEditorAppState{" +
                "modelNode=" + modelNode +
                ", showGrid=" + showGrid +
                ", showSelection=" + showSelection +
                ", lightEnabled=" + lightEnabled +
                ", activeTransform=" + activeTransform +
                "} " + super.toString();
    }
}
