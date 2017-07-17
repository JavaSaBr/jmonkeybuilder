package com.ss.editor.state.editor.impl.scene;

import static com.ss.editor.state.editor.impl.model.ModelEditorUtils.findToSelect;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.util.Objects.requireNonNull;
import com.jme3.app.state.AppState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.RendererException;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.debug.WireSphere;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import com.ss.editor.annotation.JMEThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.control.editing.EditingControl;
import com.ss.editor.control.editing.EditingInput;
import com.ss.editor.control.transform.*;
import com.ss.editor.model.EditorCamera;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.scene.EditorAudioNode;
import com.ss.editor.scene.EditorLightNode;
import com.ss.editor.state.editor.impl.AdvancedAbstractEditorAppState;
import com.ss.editor.ui.component.editor.impl.scene.AbstractSceneFileEditor;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyOperation;
import com.ss.editor.util.EditingUtils;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.GeomUtils;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.function.BooleanFloatConsumer;
import com.ss.rlib.geom.util.AngleUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.array.ArrayIterator;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of the {@link AppState} for the editor.
 *
 * @param <T> the type parameter
 * @param <M> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractSceneEditorAppState<T extends AbstractSceneFileEditor & ModelChangeConsumer, M extends Spatial>
        extends AdvancedAbstractEditorAppState<T> implements SceneEditorControl {

    private static final String KEY_S = "SSEditor.sceneEditorState.S";
    private static final String KEY_G = "SSEditor.sceneEditorState.G";
    private static final String KEY_R = "SSEditor.sceneEditorState.R";
    private static final String KEY_DEL = "SSEditor.sceneEditorState.Del";

    private static final float H_ROTATION = AngleUtils.degreeToRadians(45);
    private static final float V_ROTATION = AngleUtils.degreeToRadians(15);

    static {
        TRIGGERS.put(KEY_S, new KeyTrigger(KeyInput.KEY_S));
        TRIGGERS.put(KEY_G, new KeyTrigger(KeyInput.KEY_G));
        TRIGGERS.put(KEY_R, new KeyTrigger(KeyInput.KEY_R));
        TRIGGERS.put(KEY_DEL, new KeyTrigger(KeyInput.KEY_DELETE));
    }

    /**
     * The table with models to present lights on a scene.
     */
    @NotNull
    private static final ObjectDictionary<Light.Type, Node> LIGHT_MODEL_TABLE;

    private static final Node AUDIO_NODE_MODEL;

    static {

        final AssetManager assetManager = EDITOR.getAssetManager();

        AUDIO_NODE_MODEL = (Node) assetManager.loadModel("graphics/models/speaker/speaker.j3o");

        LIGHT_MODEL_TABLE = DictionaryFactory.newObjectDictionary();
        LIGHT_MODEL_TABLE.put(Light.Type.Point, (Node) assetManager.loadModel("graphics/models/light/point_light.j3o"));
        LIGHT_MODEL_TABLE.put(Light.Type.Directional, (Node) assetManager.loadModel("graphics/models/light/direction_light.j3o"));
        LIGHT_MODEL_TABLE.put(Light.Type.Spot, (Node) assetManager.loadModel("graphics/models/light/spot_light.j3o"));
    }

    /**
     * The map with cached light nodes.
     */
    @NotNull
    private final ObjectDictionary<Light, EditorLightNode> cachedLights;

    /**
     * The map with cached audio nodes.
     */
    @NotNull
    private final ObjectDictionary<AudioNode, EditorAudioNode> cachedAudioNodes;

    /**
     * The array of light nodes.
     */
    @NotNull
    private final Array<EditorLightNode> lightNodes;

    /**
     * The array of audio nodes.
     */
    @NotNull
    private final Array<EditorAudioNode> audioNodes;

    /**
     * The selection models of selected models.
     */
    @NotNull
    private final ObjectDictionary<Spatial, Spatial> selectionShape;

    /**
     * The array of selected models.
     */
    @NotNull
    protected final Array<Spatial> selected;

    /**
     * The node for the placement of controls.
     */
    @NotNull
    private final Node toolNode;

    /**
     * The node for the placement of transform controls.
     */
    @NotNull
    private final Node transformToolNode;

    /**
     * The node for the placement of models.
     */
    @NotNull
    private final Node modelNode;

    /**
     * The node for the placement of lights.
     */
    @NotNull
    private final Node lightNode;

    /**
     * The node for the placement of audio nodes.
     */
    @NotNull
    private final Node audioNode;

    /**
     * The cursor node.
     */
    @NotNull
    private final Node cursorNode;

    /**
     * The markers node.
     */
    @NotNull
    private final Node markersNode;

    /**
     * The nodes for the placement of model controls.
     */
    @Nullable
    private Node moveTool, rotateTool, scaleTool;

    /**
     * Center of transformation.
     */
    @Nullable
    private Transform transformCenter;

    /**
     * The original transformation.
     */
    @Nullable
    private Transform originalTransform;

    /**
     * Object to transform.
     */
    @Nullable
    private Spatial toTransform;

    /**
     * Current display model.
     */
    @Nullable
    private M currentModel;

    /**
     * Material for selection.
     */
    @Nullable
    private Material selectionMaterial;

    /**
     * The current type of transformation.
     */
    @Nullable
    private volatile TransformType transformType;

    /**
     * The current direction of transformation.
     */
    @Nullable
    private PickedAxis pickedAxis;

    /**
     * The difference between the previous point of transformation and new.
     */
    @Nullable
    private Vector3f deltaVector;

    /**
     * The plane for calculation transforms.
     */
    @Nullable
    private Node collisionPlane;

    /**
     * The node on which the camera is looking.
     */
    @Nullable
    private Node cameraNode;

    /**
     * Grid of the scene.
     */
    @Nullable
    private Node grid;

    /**
     * The flag of visibility grid.
     */
    private boolean showGrid;

    /**
     * The flag of visibility selection.
     */
    private boolean showSelection;

    /**
     * The flag of existing active transformation.
     */
    private boolean activeTransform;

    /**
     * The flag of existing active editing.
     */
    private boolean activeEditing;

    /**
     * The flag of editing mode.
     */
    private boolean editingMode;

    /**
     * Instantiates a new Abstract scene editor app state.
     *
     * @param fileEditor the file editor
     */
    public AbstractSceneEditorAppState(@NotNull final T fileEditor) {
        super(fileEditor);
        this.cachedLights = DictionaryFactory.newObjectDictionary();
        this.cachedAudioNodes = DictionaryFactory.newObjectDictionary();
        this.modelNode = new Node("ModelNode");
        this.modelNode.setUserData(SceneEditorControl.class.getName(), true);
        this.selected = ArrayFactory.newArray(Spatial.class);
        this.selectionShape = DictionaryFactory.newObjectDictionary();
        this.toolNode = new Node("ToolNode");
        this.transformToolNode = new Node("TransformToolNode");
        this.lightNodes = ArrayFactory.newArray(EditorLightNode.class);
        this.audioNodes = ArrayFactory.newArray(EditorAudioNode.class);
        this.lightNode = new Node("Lights");
        this.audioNode = new Node("Audio nodes");
        this.cursorNode = new Node("Cursor node");
        this.markersNode = new Node("Markers node");

        final EditorCamera editorCamera = requireNonNull(getEditorCamera());
        editorCamera.setDefaultHorizontalRotation(H_ROTATION);
        editorCamera.setDefaultVerticalRotation(V_ROTATION);

        final Node stateNode = getStateNode();
        stateNode.attachChild(getCameraNode());

        modelNode.attachChild(lightNode);
        modelNode.attachChild(audioNode);

        createCollisionPlane();
        createToolElements();
        createManipulators();
        setShowSelection(true);
        setShowGrid(true);
        setTransformType(TransformType.MOVE_TOOL);
    }

    @Override
    protected void registerActionHandlers(@NotNull final ObjectDictionary<String, BooleanFloatConsumer> actionHandlers) {
        super.registerActionHandlers(actionHandlers);

        final T fileEditor = getFileEditor();

        actionHandlers.put(KEY_S, (isPressed, tpf) -> fileEditor.handleKeyAction(KeyCode.S, isPressed, isControlDown(), isButtonMiddleDown()));
        actionHandlers.put(KEY_G, (isPressed, tpf) -> fileEditor.handleKeyAction(KeyCode.G, isPressed, isControlDown(), isButtonMiddleDown()));
        actionHandlers.put(KEY_R, (isPressed, tpf) -> fileEditor.handleKeyAction(KeyCode.R, isPressed, isControlDown(), isButtonMiddleDown()));
        actionHandlers.put(KEY_DEL, (isPressed, tpf) -> fileEditor.handleKeyAction(KeyCode.DELETE, isPressed, isControlDown(), isButtonMiddleDown()));
    }

    @Override
    protected void registerActionListener(@NotNull final InputManager inputManager) {
        super.registerActionListener(inputManager);
        inputManager.addListener(actionListener, KEY_S, KEY_G, KEY_R, KEY_DEL);
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
        return notNull(cameraNode);
    }

    @Override
    protected boolean needEditorCamera() {
        return true;
    }

    /**
     * Gets light node.
     *
     * @return the node for the placement of lights.
     */
    @NotNull
    protected Node getLightNode() {
        return lightNode;
    }

    /**
     * Gets audio node.
     *
     * @return the node for the placement of audio nodes.
     */
    @NotNull
    protected Node getAudioNode() {
        return audioNode;
    }

    @Override
    public void notifyTransformed(@NotNull final Spatial spatial) {
        getFileEditor().notifyTransformed(spatial);
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
     * Create tool elements.
     */
    private void createToolElements() {

        selectionMaterial = createColorMaterial(new ColorRGBA(1F, 170 / 255F, 64 / 255F, 1F));
        grid = createGrid();

        final Node toolNode = getToolNode();
        toolNode.attachChild(grid);
    }

    @NotNull
    private Node createGrid() {

        final Node gridNode = new Node("GridNode");

        final ColorRGBA gridColor = new ColorRGBA(0.4f, 0.4f, 0.4f, 0.5f);
        final ColorRGBA xColor = new ColorRGBA(1.0f, 0.1f, 0.1f, 0.5f);
        final ColorRGBA zColor = new ColorRGBA(0.1f, 1.0f, 0.1f, 0.5f);

        final Material gridMaterial = createColorMaterial(gridColor);
        gridMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        final Material xMaterial = createColorMaterial(xColor);
        xMaterial.getAdditionalRenderState().setLineWidth(5);

        final Material zMaterial = createColorMaterial(zColor);
        zMaterial.getAdditionalRenderState().setLineWidth(5);

        final int gridSize = getGridSize();

        final Geometry grid = new Geometry("grid", new Grid(gridSize, gridSize, 1.0f));
        grid.setMaterial(gridMaterial);
        grid.setQueueBucket(RenderQueue.Bucket.Transparent);
        grid.setShadowMode(RenderQueue.ShadowMode.Off);
        grid.setCullHint(Spatial.CullHint.Never);
        grid.setLocalTranslation(gridSize / 2 * -1, 0, gridSize / 2 * -1);

        gridNode.attachChild(grid);

        // Red line for X axis
        final Line xAxis = new Line(new Vector3f(-gridSize / 2, 0f, 0f), new Vector3f(gridSize / 2 - 1, 0f, 0f));

        final Geometry gxAxis = new Geometry("XAxis", xAxis);
        gxAxis.setModelBound(new BoundingBox());
        gxAxis.setShadowMode(RenderQueue.ShadowMode.Off);
        gxAxis.setCullHint(Spatial.CullHint.Never);
        gxAxis.setMaterial(xMaterial);

        gridNode.attachChild(gxAxis);

        // Blue line for Z axis
        final Line zAxis = new Line(new Vector3f(0f, 0f, -gridSize / 2), new Vector3f(0f, 0f, gridSize / 2 - 1));

        final Geometry gzAxis = new Geometry("ZAxis", zAxis);
        gzAxis.setModelBound(new BoundingBox());
        gzAxis.setShadowMode(RenderQueue.ShadowMode.Off);
        gzAxis.setCullHint(Spatial.CullHint.Never);
        gzAxis.setMaterial(zMaterial);

        gridNode.attachChild(gzAxis);

        return gridNode;
    }

    /**
     * Gets grid size.
     *
     * @return the grid size.
     */
    protected int getGridSize() {
        return 20;
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
     * Create the material for presentation of selected models.
     */
    private Material createColorMaterial(@NotNull final ColorRGBA color) {
        final Material material = new Material(EDITOR.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setWireframe(true);
        material.setColor("Color", color);
        return material;
    }

    /**
     * Sets transform type.
     *
     * @param transformType the current type of the transform.
     */
    @FromAnyThread
    public void setTransformType(@Nullable final TransformType transformType) {
        this.transformType = transformType;
    }

    /**
     * @return the node for the placement of transform controls.
     */
    @NotNull
    private Node getTransformToolNode() {
        return transformToolNode;
    }

    /**
     * Gets transform type.
     *
     * @return the current type of transformation.
     */
    @Nullable
    @FromAnyThread
    public TransformType getTransformType() {
        return transformType;
    }

    @Override
    @Nullable
    public Transform getTransformCenter() {
        return transformCenter;
    }

    @Override
    public void setPickedAxis(@NotNull final PickedAxis axis) {
        this.pickedAxis = axis;
    }

    @NotNull
    @Override
    public PickedAxis getPickedAxis() {
        return notNull(pickedAxis);
    }

    @Nullable
    @Override
    public Node getCollisionPlane() {
        if (collisionPlane == null) throw new RuntimeException("collisionPlane is null");
        return collisionPlane;
    }

    @Override
    public void setDeltaVector(@Nullable final Vector3f deltaVector) {
        this.deltaVector = deltaVector;
    }

    @Nullable
    @Override
    public Vector3f getDeltaVector() {
        return deltaVector;
    }

    @Override
    @Nullable
    public Spatial getToTransform() {
        return toTransform;
    }

    /**
     * Gets tool node.
     *
     * @return the node for the placement of controls.
     */
    @NotNull
    protected Node getToolNode() {
        return toolNode;
    }

    /**
     * @return grid of the scene.
     */
    @NotNull
    private Node getGrid() {
        return notNull(grid);
    }

    /**
     * @return the nodes for the placement of model controls.
     */
    @NotNull
    private Node getMoveTool() {
        return notNull(moveTool);
    }

    /**
     * @return the nodes for the placement of model controls.
     */
    @NotNull
    private Node getRotateTool() {
        return notNull(rotateTool);
    }

    /**
     * @return the nodes for the placement of model controls.
     */
    @NotNull
    private Node getScaleTool() {
        return notNull(scaleTool);
    }

    /**
     * @param activeTransform true of we have active transformation.
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
     * @return true if we have active editing.
     */
    private boolean isActiveEditing() {
        return activeEditing;
    }

    /**
     * @param activeEditing true of we have active editing.
     */
    private void setActiveEditing(final boolean activeEditing) {
        this.activeEditing = activeEditing;
    }

    @Override
    public void update(final float tpf) {
        super.update(tpf);

        final Node transformToolNode = getTransformToolNode();
        final Transform selectionCenter = getTransformCenter();
        final TransformType transformType = getTransformType();

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
        if (editorCamera != null) editorCamera.update(tpf);

        final Array<EditorLightNode> lightNodes = getLightNodes();
        lightNodes.forEach(EditorLightNode::updateModel);

        final Array<EditorAudioNode> audioNodes = getAudioNodes();
        audioNodes.forEach(EditorAudioNode::updateModel);

        final Array<Spatial> selected = getSelected();
        selected.forEach(this, (spatial, state) -> {

            final ObjectDictionary<Spatial, Spatial> selectionShape = state.getSelectionShape();
            final Spatial shape = selectionShape.get(spatial);
            if (shape == null) return;

            if (spatial instanceof EditorLightNode) {
                spatial = ((EditorLightNode) spatial).getModel();
            } else if (spatial instanceof EditorAudioNode) {
                spatial = ((EditorAudioNode) spatial).getModel();
            }

            requireNonNull(spatial);

            state.updateTransformNode(spatial.getWorldTransform());
            shape.setLocalTranslation(spatial.getWorldTranslation());
            shape.setLocalRotation(spatial.getWorldRotation());
            shape.setLocalScale(spatial.getWorldScale());
        });

        transformToolNode.detachAllChildren();

        if (transformType == TransformType.MOVE_TOOL) {
            transformToolNode.attachChild(getMoveTool());
        } else if (transformType == TransformType.ROTATE_TOOL) {
            transformToolNode.attachChild(getRotateTool());
        } else if (transformType == TransformType.SCALE_TOOL) {
            transformToolNode.attachChild(getScaleTool());
        }

        final Node toolNode = getToolNode();

        if (selected.isEmpty()) {
            toolNode.detachChild(transformToolNode);
        } else if (!isEditingMode()) {
            toolNode.attachChild(transformToolNode);
        }

        if (isEditingMode()) {
            updateEditingNodes();
            updateEditing();
        }
    }

    /**
     * Update editing nodes.
     */
    private void updateEditingNodes() {
        if (!isEditingMode()) return;

        final Node cursorNode = getCursorNode();
        final EditingControl control = EditingUtils.getEditingControl(cursorNode);
        final Spatial editedModel = EditingUtils.getEditedModel(control);
        if (editedModel == null) return;

        final Vector3f contactPoint = GeomUtils.getContactPointFromCursor(editedModel);

        if (contactPoint != null) {
            cursorNode.setLocalTranslation(contactPoint);
        }
    }

    /**
     * Update the transformation node.
     */
    @JMEThread
    private void updateTransformNode(@Nullable final Transform transform) {
        if (transform == null) return;

        final Vector3f location = transform.getTranslation();
        final Vector3f positionOnCamera = getPositionOnCamera(location);

        final Node transformToolNode = getTransformToolNode();
        transformToolNode.setLocalTranslation(positionOnCamera);
        transformToolNode.setLocalRotation(transform.getRotation());
    }

    @NotNull
    @JMEThread
    private Vector3f getPositionOnCamera(@NotNull final Vector3f location) {
        final Camera camera = EDITOR.getCamera();
        final Vector3f resultPosition = location.subtract(camera.getLocation()).normalize().multLocal(camera.getFrustumNear() + 0.4f);
        return camera.getLocation().add(resultPosition);
    }

    /**
     * @return material of selection.
     */
    @Nullable
    private Material getSelectionMaterial() {
        return selectionMaterial;
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
     * @return the array of light nodes.
     */
    @NotNull
    private Array<EditorLightNode> getLightNodes() {
        return lightNodes;
    }

    /**
     * @return the array of audio nodes.
     */
    @NotNull
    private Array<EditorAudioNode> getAudioNodes() {
        return audioNodes;
    }

    /**
     * @return the map with cached light nodes.
     */
    @NotNull
    private ObjectDictionary<Light, EditorLightNode> getCachedLights() {
        return cachedLights;
    }

    /**
     * @return the map with cached audio nodes.
     */
    @NotNull
    private ObjectDictionary<AudioNode, EditorAudioNode> getCachedAudioNodes() {
        return cachedAudioNodes;
    }

    /**
     * Update selected models.
     *
     * @param spatials the spatials
     */
    @FromAnyThread
    public void updateSelection(@NotNull final Array<Spatial> spatials) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateSelectionImpl(spatials));
    }

    /**
     * The process of updating selected models.
     */
    @JMEThread
    private void updateSelectionImpl(@NotNull final Array<Spatial> spatials) {

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
    @JMEThread
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
    @JMEThread
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
    @JMEThread
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
    @JMEThread
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
    @JMEThread
    private Spatial buildGeometrySelection(@NotNull final Geometry geom) {

        final Mesh mesh = geom.getMesh();
        if (mesh == null) return null;

        final Geometry geometry = new Geometry("SelectionShape", mesh);
        geometry.setMaterial(getSelectionMaterial());
        geometry.setLocalTransform(geom.getWorldTransform());

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
     *
     * @param showSelection the show selection
     */
    @FromAnyThread
    public void updateShowSelection(final boolean showSelection) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateShowSelectionImpl(showSelection));
    }

    @Override
    protected void onActionImpl(@NotNull final String name, final boolean isPressed, final float tpf) {
        super.onActionImpl(name, isPressed, tpf);
        if (MOUSE_RIGHT_CLICK.equals(name)) {
            if(isEditingMode()) {
                if (isPressed) startEditing(getEditingInput(MouseButton.SECONDARY));
                else finishEditing(getEditingInput(MouseButton.SECONDARY));
            } else if(!isPressed) {
                processSelect();
            }
        } else if (MOUSE_LEFT_CLICK.equals(name)) {
            if(isEditingMode()) {
                if (isPressed) startEditing(getEditingInput(MouseButton.PRIMARY));
                else finishEditing(getEditingInput(MouseButton.PRIMARY));
            } else {
                if (isPressed) startTransform();
                else endTransform();
            }
        }
    }

    /**
     * Gets editing input.
     *
     * @param mouseButton the mouse button
     * @return the editing input
     */
    @NotNull
    protected EditingInput getEditingInput(final MouseButton mouseButton) {
        switch (mouseButton) {
            case SECONDARY: {

                if(isControlDown()) {
                    return EditingInput.MOUSE_SECONDARY_WITH_CTRL;
                }

                return EditingInput.MOUSE_SECONDARY;
            }
            case PRIMARY: {
                return EditingInput.MOUSE_PRIMARY;
            }
        }

        return EditingInput.MOUSE_PRIMARY;
    }

    /**
     * Handling a click in the area of the editor.
     */
    @JMEThread
    private void processSelect() {
        if (isEditingMode()) return;

        final Geometry geometry = GeomUtils.getGeometryFromCursor(getModelNode());

        if (geometry == null) {
            EXECUTOR_MANAGER.addFXTask(() -> notifySelected(null));
            return;
        }

        EXECUTOR_MANAGER.addFXTask(() -> notifySelected(findToSelect(this, geometry)));
    }

    /**
     * Get a position on a scene for a cursor position on a screen.
     *
     * @param worldX the x position on screen.
     * @param worldY the y position on screen.
     * @return the position on a scene.
     */
    @NotNull
    @JMEThread
    public Vector3f getScenePosByScreenPos(final float worldX, final float worldY) {

        final Camera camera = EDITOR.getCamera();

        final Vector2f cursor = new Vector2f(worldX, worldY);
        final Vector3f click3d = camera.getWorldCoordinates(cursor, 0f);
        final Vector3f dir = camera.getWorldCoordinates(cursor, 1f).subtractLocal(click3d).normalizeLocal();

        final Ray ray = new Ray();
        ray.setOrigin(click3d);
        ray.setDirection(dir);

        final CollisionResults results = new CollisionResults();

        final Node stateNode = getStateNode();
        stateNode.updateModelBound();
        stateNode.collideWith(ray, results);

        final CollisionResult closestCollision = results.getClosestCollision();
        if (closestCollision == null) return Vector3f.ZERO;

        return closestCollision.getContactPoint();
    }

    /**
     * Get a geometry on a scene for a position on a screen.
     *
     * @param worldX the x position on screen.
     * @param worldY the y position on screen.
     * @return the position on a scene.
     */
    @Nullable
    @JMEThread
    public Geometry getGeometryByScreenPos(final float worldX, final float worldY) {

        final Camera camera = EDITOR.getCamera();

        final Vector2f cursor = new Vector2f(worldX, worldY);
        final Vector3f click3d = camera.getWorldCoordinates(cursor, 0f);
        final Vector3f dir = camera.getWorldCoordinates(cursor, 1f).subtractLocal(click3d).normalizeLocal();

        final Ray ray = new Ray();
        ray.setOrigin(click3d);
        ray.setDirection(dir);

        final CollisionResults results = new CollisionResults();

        final M currentModel = requireNonNull(getCurrentModel());
        currentModel.updateModelBound();
        currentModel.collideWith(ray, results);

        final CollisionResult closestCollision = results.getClosestCollision();
        if (closestCollision == null) return null;

        return closestCollision.getGeometry();
    }

    @JMEThread
    private void notifySelected(@Nullable final Object object) {
        getFileEditor().notifySelected(object);
    }

    /**
     * The process of updating the showing selection.
     */
    @JMEThread
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
     * Gets model node.
     *
     * @return the node for the placement of models.
     */
    @NotNull
    protected Node getModelNode() {
        return modelNode;
    }

    /**
     * Update the showing grid.
     *
     * @param showGrid the show grid
     */
    @FromAnyThread
    public void updateShowGrid(final boolean showGrid) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> updateShowGridImpl(showGrid));
    }

    /**
     * The process of updating the showing grid.
     */
    @JMEThread
    private void updateShowGridImpl(final boolean showGrid) {
        if (isShowGrid() == showGrid) return;

        final Node toolNode = getToolNode();
        final Node grid = getGrid();

        if (showGrid) {
            toolNode.attachChild(grid);
        } else {
            toolNode.detachChild(grid);
        }

        setShowGrid(showGrid);
    }

    /**
     * Finish the transformation of the model.
     */
    @JMEThread
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

        final ModelPropertyOperation<Spatial, Transform> operation = new ModelPropertyOperation<>(toTransform, "transform", newValue, oldValue);
        operation.setApplyHandler(Spatial::setLocalTransform);

        final T fileEditor = getFileEditor();
        fileEditor.execute(operation);

        setPickedAxis(PickedAxis.NONE);
        setActiveTransform(false);
        setDeltaVector(null);
        updateTransformCenter();
    }

    /**
     * Start transformation.
     */
    @JMEThread
    private boolean startTransform() {
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
     * Start editing.
     */
    @JMEThread
    private void startEditing(@NotNull final EditingInput editingInput) {
        final Node cursorNode = getCursorNode();
        final EditingControl control = EditingUtils.getEditingControl(cursorNode);
        final Spatial editedModel = EditingUtils.getEditedModel(getCursorNode());
        if (control == null || editedModel == null || control.isStartedEditing()) return;
        control.startEditing(editingInput, cursorNode.getLocalTranslation());
    }

    /**
     * Finish editing.
     */
    @JMEThread
    private void finishEditing(@NotNull final EditingInput editingInput) {

        final Node cursorNode = getCursorNode();
        final EditingControl control = EditingUtils.getEditingControl(cursorNode);
        final Spatial editedModel = EditingUtils.getEditedModel(control);

        if (control == null || editedModel == null || !control.isStartedEditing() ||
                control.getCurrentInput() != editingInput) {
            return;
        }

        control.finishEditing(cursorNode.getLocalTranslation());
    }

    /**
     * Update editing.
     */
    @JMEThread
    private void updateEditing() {
        final Node cursorNode = getCursorNode();
        final EditingControl control = EditingUtils.getEditingControl(cursorNode);
        final Spatial editedModel = EditingUtils.getEditedModel(control);
        if (control == null || editedModel == null || !control.isStartedEditing()) return;
        control.updateEditing(cursorNode.getLocalTranslation());
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

    /**
     * Show the model in the scene.
     *
     * @param model the model
     */
    @FromAnyThread
    public void openModel(@NotNull final M model) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> openModelImpl(model));
    }

    /**
     * The process of showing the model in the scene.
     */
    @JMEThread
    private void openModelImpl(@NotNull final M model) {

        final Node modelNode = getModelNode();
        final M currentModel = getCurrentModel();
        if (currentModel != null) modelNode.detachChild(currentModel);

        NodeUtils.visitGeometry(model, geometry -> {

            final RenderManager renderManager = EDITOR.getRenderManager();
            try {
                renderManager.preloadScene(geometry);
            } catch (final RendererException | AssetNotFoundException | UnsupportedOperationException e) {

                EditorUtil.handleException(LOGGER, this,
                        new RuntimeException("Found invalid material in the geometry: [" + geometry.getName() + "]. " +
                                "The material will be removed from the geometry.", e));

                geometry.setMaterial(EDITOR.getDefaultMaterial());
            }
        });

        modelNode.attachChild(model);

        setCurrentModel(model);
    }

    /**
     * @param currentModel current display model.
     */
    private void setCurrentModel(@Nullable final M currentModel) {
        this.currentModel = currentModel;
    }

    /**
     * Gets current model.
     *
     * @return current display model.
     */
    @Nullable
    @FromAnyThread
    public M getCurrentModel() {
        return currentModel;
    }

    /**
     * Add a light.
     *
     * @param light the light.
     */
    @FromAnyThread
    public void addLight(@NotNull final Light light) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> addLightImpl(light));
    }

    /**
     * The process of adding a light.
     */
    @JMEThread
    private void addLightImpl(@NotNull final Light light) {

        final Node node = LIGHT_MODEL_TABLE.get(light.getType());
        if (node == null) return;

        final ObjectDictionary<Light, EditorLightNode> cachedLights = getCachedLights();

        final Camera camera = EDITOR.getCamera();
        final EditorLightNode lightModel = requireNonNull(cachedLights.get(light, () -> {

            final Node model = (Node) node.clone();
            model.setLocalScale(0.01F);

            final EditorLightNode result = new EditorLightNode();
            result.setModel(model);

            final Geometry geometry = NodeUtils.findGeometry(model, "White");

            if (geometry == null) {
                LOGGER.warning(this, "not found geometry for the node " + geometry);
                return null;
            }

            final Material material = geometry.getMaterial();
            material.setColor("Color", light.getColor());

            return result;
        }));

        if (light instanceof SpotLight) {

            final SpotLight spotLight = (SpotLight) light;

            final Quaternion rotation = new Quaternion();
            rotation.lookAt(spotLight.getDirection(), camera.getUp());

            lightModel.setLocalTranslation(spotLight.getPosition());
            lightModel.setLocalRotation(rotation);

        } else if (light instanceof PointLight) {
            lightModel.setLocalTranslation(((PointLight) light).getPosition());
        } else if (light instanceof DirectionalLight) {

            final DirectionalLight directionalLight = (DirectionalLight) light;
            final Quaternion rotation = new Quaternion();
            rotation.lookAt(directionalLight.getDirection(), camera.getUp());

            lightModel.setLocalRotation(rotation);
        }

        final Node lightNode = getLightNode();
        lightNode.attachChild(lightModel);
        lightNode.attachChild(lightModel.getModel());

        lightModel.setLight(light);

        getLightNodes().add(lightModel);
    }

    /**
     * Move a camera to a location.
     *
     * @param location the location.
     */
    @FromAnyThread
    public void moveCameraTo(@NotNull final Vector3f location) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> getNodeForCamera().setLocalTranslation(location));
    }

    /**
     * Remove a light.
     *
     * @param light the light.
     */
    @FromAnyThread
    public void removeLight(@NotNull final Light light) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> removeLightImpl(light));
    }

    /**
     * The process of removing a light.
     */
    @JMEThread
    private void removeLightImpl(@NotNull final Light light) {

        final Node node = LIGHT_MODEL_TABLE.get(light.getType());
        if (node == null) return;

        final ObjectDictionary<Light, EditorLightNode> cachedLights = getCachedLights();
        final EditorLightNode lightModel = cachedLights.get(light);
        if (lightModel == null) return;

        lightModel.setLight(null);

        final Node lightNode = getLightNode();
        lightNode.detachChild(lightModel);
        lightNode.detachChild(requireNonNull(lightModel.getModel()));

        getLightNodes().fastRemove(lightModel);
    }

    /**
     * Add an audio node.
     *
     * @param audioNode the audio node.
     */
    @FromAnyThread
    public void addAudioNode(@NotNull final AudioNode audioNode) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> addAudioNodeImpl(audioNode));
    }

    /**
     * The process of adding an audio node.
     */
    @JMEThread
    private void addAudioNodeImpl(@NotNull final AudioNode audio) {

        final ObjectDictionary<AudioNode, EditorAudioNode> cachedAudioNodes = getCachedAudioNodes();

        final Camera camera = EDITOR.getCamera();
        final EditorAudioNode audioModel = requireNonNull(cachedAudioNodes.get(audio, () -> {

            final Node model = (Node) AUDIO_NODE_MODEL.clone();
            model.setLocalScale(0.005F);

            final EditorAudioNode result = new EditorAudioNode();
            result.setModel(model);

            return result;
        }));

        final Quaternion rotation = new Quaternion();
        rotation.lookAt(audio.getDirection(), camera.getUp());

        final Node editedNode = audioModel.getEditedNode();
        editedNode.setLocalRotation(rotation);
        editedNode.setLocalTranslation(audio.getLocalTranslation());

        final Node audioNode = getAudioNode();
        audioNode.attachChild(audioModel);
        audioNode.attachChild(audioModel.getModel());

        audioModel.setAudioNode(audio);

        getAudioNodes().add(audioModel);
    }

    /**
     * Remove an audio node.
     *
     * @param audio the audio node.
     */
    @FromAnyThread
    public void removeAudioNode(@NotNull final AudioNode audio) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> removeAudioNodeImpl(audio));
    }

    /**
     * The process of removing an audio node.
     */
    @JMEThread
    private void removeAudioNodeImpl(@NotNull final AudioNode audio) {

        final ObjectDictionary<AudioNode, EditorAudioNode> cachedAudioNodes = getCachedAudioNodes();
        final EditorAudioNode audioModel = cachedAudioNodes.get(audio);
        if (audioModel == null) return;

        audioModel.setAudioNode(null);

        final Node audioNode = getAudioNode();
        audioNode.detachChild(audioModel);
        audioNode.detachChild(requireNonNull(audioModel.getModel()));

        getAudioNodes().fastRemove(audioModel);
    }

    /**
     * Get a light node for a light.
     *
     * @param light the light.
     * @return the light node or null.
     */
    @Nullable
    @FromAnyThread
    public EditorLightNode getLightNode(@NotNull final Light light) {
        return getLightNodes().search(light, (node, toCheck) -> node.getLight() == toCheck);
    }

    /**
     * Get a light node for a model.
     *
     * @param model the model.
     * @return the light node or null.
     */
    @Nullable
    @FromAnyThread
    public EditorLightNode getLightNode(@NotNull final Spatial model) {
        return getLightNodes().search(model, (node, toCheck) -> node.getModel() == toCheck);
    }

    /**
     * Get an editor audio node for an audio node.
     *
     * @param audioNode the audio node.
     * @return the editor audio node or null.
     */
    @Nullable
    @FromAnyThread
    public EditorAudioNode getAudioNode(@NotNull final AudioNode audioNode) {
        return getAudioNodes().search(audioNode, (node, toCheck) -> node.getAudioNode() == toCheck);
    }

    /**
     * Get an editor audio node for an model.
     *
     * @param model the model.
     * @return the editor audio node or null.
     */
    @Nullable
    @FromAnyThread
    public EditorAudioNode getAudioNode(@NotNull final Spatial model) {
        return getAudioNodes().search(model, (node, toCheck) -> node.getModel() == toCheck);
    }

    @Override
    @FromAnyThread
    protected void notifyChangedCamera(@NotNull final Vector3f cameraLocation, final float hRotation,
                                       final float vRotation, final float targetDistance) {
        EXECUTOR_MANAGER.addFXTask(() -> getFileEditor().notifyChangedCamera(cameraLocation, hRotation, vRotation, targetDistance));
    }

    /**
     * @param editingMode the flag of editing mode.
     */
    private void setEditingMode(final boolean editingMode) {
        this.editingMode = editingMode;
    }

    /**
     * @return true if editing mode is enabled.
     */
    private boolean isEditingMode() {
        return editingMode;
    }

    /**
     * Change enabling of editing mode.
     *
     * @param editingMode true if editing mode is enabled.
     */
    @FromAnyThread
    public void changeEditingMode(final boolean editingMode) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> changeEditingModeImpl(editingMode));
    }

    /**
     * Change enabling of editing mode.
     *
     * @param editingMode true if editing mode is enabled.
     */
    @JMEThread
    private void changeEditingModeImpl(final boolean editingMode) {
        setEditingMode(editingMode);

        final Node cursorNode = getCursorNode();
        final Node markersNode = getMarkersNode();
        final Node toolNode = getToolNode();
        final Node transformToolNode = getTransformToolNode();

        if (isEditingMode()) {
            toolNode.attachChild(cursorNode);
            toolNode.attachChild(markersNode);
            toolNode.detachChild(transformToolNode);
        } else {
            toolNode.detachChild(cursorNode);
            toolNode.detachChild(markersNode);
            toolNode.attachChild(transformToolNode);
        }
    }

    /**
     * Gets cursor node.
     *
     * @return the cursor node.
     */
    @NotNull
    @JMEThread
    public Node getCursorNode() {
        return cursorNode;
    }

    /**
     * Gets markers node.
     *
     * @return the markers node.
     */
    @NotNull
    @JMEThread
    public Node getMarkersNode() {
        return markersNode;
    }
}
