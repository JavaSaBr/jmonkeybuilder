package com.ss.editor.part3d.editor.impl.scene;

import static com.ss.editor.util.NodeUtils.findParent;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.app.state.AppState;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RendererException;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.AssetLinkNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.debug.WireSphere;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.painting.PaintingInput;
import com.ss.editor.control.transform.*;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.scene.ScenePresentable;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.scene.*;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.part3d.editor.impl.scene.handler.ApplyScaleToPhysicsControlsHandler;
import com.ss.editor.part3d.editor.impl.scene.handler.DisableControlsTransformationHandler;
import com.ss.editor.part3d.editor.impl.scene.handler.PhysicsControlTransformationHandler;
import com.ss.editor.part3d.editor.impl.scene.handler.ReactivatePhysicsControlsTransformationHandler;
import com.ss.editor.plugin.api.editor.part3d.Advanced3dEditorPart;
import com.ss.editor.ui.component.editor.impl.scene.AbstractSceneFileEditor;
import com.ss.editor.ui.control.property.operation.PropertyOperation;
import com.ss.editor.util.*;
import com.ss.rlib.common.function.BooleanFloatConsumer;
import com.ss.rlib.common.geom.util.AngleUtils;
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The base implementation of the {@link AppState} for the editor.
 *
 * @param <T> the type of file scene editor.
 * @param <M> the type of edited spatial.
 * @author JavaSaBr
 */
public abstract class AbstractSceneEditor3dPart<T extends AbstractSceneFileEditor & ModelChangeConsumer, M extends Spatial>
        extends Advanced3dEditorPart<T> implements EditorTransformSupport {

    /**
     * @see SelectionFinder
     */
    public static final String EP_SELECTION_FINDER = "SceneEditor3DPart#selectionFinder";

    /**
     * @see TransformationHandler
     */
    public static final String EP_PRE_TRANSFORM_HANDLER = "SceneEditor3DPart#preTransfromHandler";

    /**
     * @see TransformationHandler
     */
    public static final String EP_POST_TRANSFORM_HANDLER = "SceneEditor3DPart#postTransformHandler";

    public static final String KEY_LOADED_MODEL = "jMB.sceneEditor.loadedModel";
    public static final String KEY_IGNORE_RAY_CAST = "jMB.sceneEditor.ignoreRayCast";
    public static final String KEY_MODEL_NODE = "jMB.sceneEditor.modelNode";
    public static final String KEY_SHAPE_CENTER = "jMB.sceneEditor.shapeCenter";
    public static final String KEY_SHAPE_INIT_SCALE = "jMB.sceneEditor.initScale";

    private static final String KEY_S = "jMB.sceneEditor.S";
    private static final String KEY_G = "jMB.sceneEditor.G";
    private static final String KEY_R = "jMB.sceneEditor.R";
    private static final String KEY_DEL = "jMB.sceneEditor.Del";

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
    private static final ObjectDictionary<Light.Type, Node> LIGHT_MODEL_TABLE;

    private static final Node AUDIO_NODE_MODEL;

    static {

        var assetManager = EditorUtils.getAssetManager();

        AUDIO_NODE_MODEL = (Node) assetManager.loadModel("graphics/models/speaker/speaker.j3o");

        LIGHT_MODEL_TABLE = DictionaryFactory.newObjectDictionary();
        LIGHT_MODEL_TABLE.put(Light.Type.Point, (Node) assetManager.loadModel("graphics/models/light/point_light.j3o"));
        LIGHT_MODEL_TABLE.put(Light.Type.Directional, (Node) assetManager.loadModel("graphics/models/light/direction_light.j3o"));
        LIGHT_MODEL_TABLE.put(Light.Type.Spot, (Node) assetManager.loadModel("graphics/models/light/spot_light.j3o"));
    }

    @FunctionalInterface
    public interface SelectionFinder {

        @JmeThread
        @Nullable Spatial find(@NotNull Object object);
    }

    @FunctionalInterface
    public interface TransformationHandler {

        @JmeThread
        void handle(@NotNull Spatial object);
    }

    private static final ExtensionPoint<SelectionFinder> SELECTION_FINDERS =
            ExtensionPointManager.register(EP_SELECTION_FINDER);

    private static final ExtensionPoint<TransformationHandler> PRE_TRANSFORM_HANDLERS =
            ExtensionPointManager.register(EP_PRE_TRANSFORM_HANDLER);

    private static final ExtensionPoint<TransformationHandler> POST_TRANSFORM_HANDLERS =
            ExtensionPointManager.register(EP_POST_TRANSFORM_HANDLER);

    static {

        // default handlers
        var disableControlsHandler = new DisableControlsTransformationHandler();
        var applyScaleToPhysicsControlsHandler = new ApplyScaleToPhysicsControlsHandler();

        PRE_TRANSFORM_HANDLERS.register(disableControlsHandler::onPreTransform)
                .register(applyScaleToPhysicsControlsHandler::onPreTransform);

        POST_TRANSFORM_HANDLERS.register(disableControlsHandler::onPostTransform)
                .register(new ReactivatePhysicsControlsTransformationHandler())
                .register(new PhysicsControlTransformationHandler())
                .register(applyScaleToPhysicsControlsHandler::onPostTransform);
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
     * The map with cached presentable objects.
     */
    @NotNull
    private final ObjectDictionary<ScenePresentable, EditorPresentableNode> cachedPresentableObjects;

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
     * The array of scene presentable nodes.
     */
    @NotNull
    private final Array<EditorPresentableNode> presentableNodes;

    /**
     * The selection models of selected models.
     */
    @NotNull
    private final ObjectDictionary<Spatial, Geometry> selectionShape;

    /**
     * The array of selected models.
     */
    @NotNull
    protected final Array<Spatial> selected;

    /**
     * The node for the placement of controls.
     */
    @NotNull
    protected final Node toolNode;

    /**
     * The node for the placement of transform controls.
     */
    @NotNull
    private final Node transformToolNode;

    /**
     * The node for the placement of models.
     */
    @NotNull
    protected final Node modelNode;

    /**
     * The node for the placement of lights.
     */
    @NotNull
    protected final Node lightNode;

    /**
     * The node for the placement of audio nodes.
     */
    @NotNull
    protected final Node audioNode;

    /**
     * The node for the placement of presentable nodes.
     */
    @NotNull
    private final Node presentableNode;

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
     * The transformation mode.
     */
    @NotNull
    private TransformationMode transformMode;

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
    private float transformDeltaX;
    private float transformDeltaY;
    private float transformDeltaZ;

    /**
     * The plane for calculation transforms.
     */
    @Nullable
    private Node collisionPlane;

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
     * The flag of painting mode.
     */
    private boolean paintingMode;

    public AbstractSceneEditor3dPart(@NotNull T fileEditor) {
        super(fileEditor);
        this.cachedLights = DictionaryFactory.newObjectDictionary();
        this.cachedAudioNodes = DictionaryFactory.newObjectDictionary();
        this.cachedPresentableObjects = DictionaryFactory.newObjectDictionary();
        this.modelNode = new Node("TreeNode");
        this.modelNode.setUserData(KEY_MODEL_NODE, true);
        this.selected = ArrayFactory.newArray(Spatial.class);
        this.selectionShape = DictionaryFactory.newObjectDictionary();
        this.toolNode = new Node("ToolNode");
        this.transformToolNode = new Node("TransformToolNode");
        this.lightNodes = ArrayFactory.newArray(EditorLightNode.class);
        this.audioNodes = ArrayFactory.newArray(EditorAudioNode.class);
        this.presentableNodes = ArrayFactory.newArray(EditorPresentableNode.class);
        this.lightNode = new Node("Lights");
        this.audioNode = new Node("Audio nodes");
        this.presentableNode = new Node("Presentable nodes");
        this.cursorNode = new Node("Cursor node");
        this.markersNode = new Node("Markers node");

        var editorCamera = notNull(getEditorCamera());
        editorCamera.setDefaultHorizontalRotation(H_ROTATION);
        editorCamera.setDefaultVerticalRotation(V_ROTATION);

        modelNode.attachChild(lightNode);
        modelNode.attachChild(audioNode);
        modelNode.attachChild(presentableNode);

        createCollisionPlane();
        createToolElements();
        createManipulators();
        setShowSelection(true);
        setShowGrid(true);
        setTransformMode(TransformationMode.GLOBAL);
        setTransformType(TransformType.MOVE_TOOL);
        setTransformDeltaX(Float.NaN);
    }

    @Override
    @JmeThread
    protected void registerActionHandlers(@NotNull ObjectDictionary<String, BooleanFloatConsumer> actionHandlers) {
        super.registerActionHandlers(actionHandlers);

        actionHandlers.put(KEY_S, (isPressed, tpf) ->
                fileEditor.handleKeyAction(KeyCode.S, isPressed, isControlDown(), isShiftDown(), isButtonMiddleDown()));
        actionHandlers.put(KEY_G, (isPressed, tpf) ->
                fileEditor.handleKeyAction(KeyCode.G, isPressed, isControlDown(), isShiftDown(), isButtonMiddleDown()));
        actionHandlers.put(KEY_R, (isPressed, tpf) ->
                fileEditor.handleKeyAction(KeyCode.R, isPressed, isControlDown(), isShiftDown(), isButtonMiddleDown()));
        actionHandlers.put(KEY_DEL, (isPressed, tpf) ->
                fileEditor.handleKeyAction(KeyCode.DELETE, isPressed, isControlDown(), isShiftDown(), isButtonMiddleDown()));
    }

    @Override
    @JmeThread
    protected void registerActionListener(@NotNull InputManager inputManager) {
        super.registerActionListener(inputManager);
        inputManager.addListener(actionListener, KEY_S, KEY_G, KEY_R, KEY_DEL);
    }

    @Override
    @FromAnyThread
    protected boolean needEditorCamera() {
        return true;
    }

    /**
     * Create collision plane.
     */
    @FromAnyThread
    private void createCollisionPlane() {

        var assetManager = EditorUtils.getAssetManager();
        var material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        var renderState = material.getAdditionalRenderState();
        renderState.setFaceCullMode(RenderState.FaceCullMode.Off);
        renderState.setWireframe(true);

        var size = 20000F;

        var geometry = new Geometry("plane", new Quad(size, size));
        geometry.setMaterial(material);
        geometry.setLocalTranslation(-size / 2, -size / 2, 0);

        collisionPlane = new Node();
        collisionPlane.attachChild(geometry);
    }

    /**
     * Create tool elements.
     */
    @FromAnyThread
    private void createToolElements() {

        selectionMaterial = createColorMaterial(new ColorRGBA(1F, 170 / 255F, 64 / 255F, 1F));
        grid = createGrid();

        toolNode.attachChild(grid);
    }

    @FromAnyThread
    private @NotNull Node createGrid() {

        var gridNode = new Node("GridNode");

        var gridColor = new ColorRGBA(0.4f, 0.4f, 0.4f, 0.5f);
        var xColor = new ColorRGBA(1.0f, 0.1f, 0.1f, 0.5f);
        var zColor = new ColorRGBA(0.1f, 1.0f, 0.1f, 0.5f);

        var gridMaterial = createColorMaterial(gridColor);
        gridMaterial.getAdditionalRenderState()
                .setBlendMode(RenderState.BlendMode.Alpha);

        var xMaterial = createColorMaterial(xColor);
        xMaterial.getAdditionalRenderState()
                .setLineWidth(5);

        var zMaterial = createColorMaterial(zColor);
        zMaterial.getAdditionalRenderState()
                .setLineWidth(5);

        var gridSize = getGridSize();

        var grid = new Geometry("grid", new Grid(gridSize, gridSize, 1.0f));
        grid.setMaterial(gridMaterial);
        grid.setQueueBucket(RenderQueue.Bucket.Transparent);
        grid.setShadowMode(RenderQueue.ShadowMode.Off);
        grid.setCullHint(CullHint.Never);
        grid.setLocalTranslation(gridSize / 2 * -1, 0, gridSize / 2 * -1);

        var quad = new Quad(gridSize, gridSize);
        var gridCollision = new Geometry("collision", quad);
        gridCollision.setMaterial(createColorMaterial(gridColor));
        gridCollision.setQueueBucket(RenderQueue.Bucket.Transparent);
        gridCollision.setShadowMode(RenderQueue.ShadowMode.Off);
        gridCollision.setCullHint(CullHint.Always);
        gridCollision.setLocalTranslation(gridSize / 2 * -1, 0, gridSize / 2 * -1);
        gridCollision.setLocalRotation(new Quaternion().fromAngles(AngleUtils.degreeToRadians(90), 0, 0));

        gridNode.attachChild(grid);
        gridNode.attachChild(gridCollision);

        // Red line for X axis
        var xAxis = new Line(new Vector3f(-gridSize / 2, 0f, 0f), new Vector3f(gridSize / 2 - 1, 0f, 0f));

        var gxAxis = new Geometry("XAxis", xAxis);
        gxAxis.setModelBound(new BoundingBox());
        gxAxis.setShadowMode(RenderQueue.ShadowMode.Off);
        gxAxis.setCullHint(CullHint.Never);
        gxAxis.setMaterial(xMaterial);

        gridNode.attachChild(gxAxis);

        // Blue line for Z axis
        var zAxis = new Line(new Vector3f(0f, 0f, -gridSize / 2), new Vector3f(0f, 0f, gridSize / 2 - 1));

        var gzAxis = new Geometry("ZAxis", zAxis);
        gzAxis.setModelBound(new BoundingBox());
        gzAxis.setShadowMode(RenderQueue.ShadowMode.Off);
        gzAxis.setCullHint(CullHint.Never);
        gzAxis.setMaterial(zMaterial);

        gridNode.attachChild(gzAxis);

        return gridNode;
    }

    /**
     * Get the grid size.
     *
     * @return the grid size.
     */
    @FromAnyThread
    protected int getGridSize() {
        return 20;
    }

    /**
     * Create manipulators.
     */
    @FromAnyThread
    private void createManipulators() {

        var assetManager = EditorUtils.getAssetManager();

        var redMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        redMaterial.setColor("Color", ColorRGBA.Red);

        var blueMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        blueMaterial.setColor("Color", ColorRGBA.Blue);

        var greenMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        greenMaterial.setColor("Color", ColorRGBA.Green);

        moveTool = (Node) assetManager.loadModel("graphics/models/manipulators/manipulators_move.j3o");
        moveTool.getChild("move_x").setMaterial(redMaterial);
        moveTool.getChild("collision_move_x").setMaterial(redMaterial);
        moveTool.getChild("collision_move_x").setCullHint(CullHint.Always);
        moveTool.getChild("move_y").setMaterial(blueMaterial);
        moveTool.getChild("collision_move_y").setMaterial(blueMaterial);
        moveTool.getChild("collision_move_y").setCullHint(CullHint.Always);
        moveTool.getChild("move_z").setMaterial(greenMaterial);
        moveTool.getChild("collision_move_z").setMaterial(greenMaterial);
        moveTool.getChild("collision_move_z").setCullHint(CullHint.Always);
        moveTool.scale(0.2f);
        moveTool.addControl(new MoveToolControl(this));

        rotateTool = (Node) assetManager.loadModel("graphics/models/manipulators/manipulators_rotate.j3o");
        rotateTool.getChild("rot_x").setMaterial(redMaterial);
        rotateTool.getChild("collision_rot_x").setMaterial(redMaterial);
        rotateTool.getChild("collision_rot_x").setCullHint(CullHint.Always);
        rotateTool.getChild("rot_y").setMaterial(blueMaterial);
        rotateTool.getChild("collision_rot_y").setMaterial(blueMaterial);
        rotateTool.getChild("collision_rot_y").setCullHint(CullHint.Always);
        rotateTool.getChild("rot_z").setMaterial(greenMaterial);
        rotateTool.getChild("collision_rot_z").setMaterial(greenMaterial);
        rotateTool.getChild("collision_rot_z").setCullHint(CullHint.Always);
        rotateTool.scale(0.2f);
        rotateTool.addControl(new RotationToolControl(this));

        scaleTool = (Node) assetManager.loadModel("graphics/models/manipulators/manipulators_scale.j3o");
        scaleTool.getChild("scale_x").setMaterial(redMaterial);
        scaleTool.getChild("collision_scale_x").setMaterial(redMaterial);
        scaleTool.getChild("collision_scale_x").setCullHint(CullHint.Always);
        scaleTool.getChild("scale_y").setMaterial(blueMaterial);
        scaleTool.getChild("collision_scale_y").setMaterial(blueMaterial);
        scaleTool.getChild("collision_scale_y").setCullHint(CullHint.Always);
        scaleTool.getChild("scale_z").setMaterial(greenMaterial);
        scaleTool.getChild("collision_scale_z").setMaterial(greenMaterial);
        scaleTool.getChild("collision_scale_z").setCullHint(CullHint.Always);
        scaleTool.scale(0.2f);
        scaleTool.addControl(new ScaleToolControl(this));
    }

    /**
     * Create the material to present selected models.
     */
    @FromAnyThread
    private @NotNull Material createColorMaterial(@NotNull ColorRGBA color) {

        var material = new Material(EditorUtils.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setWireframe(true);
        material.setColor("Color", color);

        return material;
    }

    /**
     * Set the transform type.
     *
     * @param transformType the transformation type.
     */
    @FromAnyThread
    public void setTransformType(@Nullable TransformType transformType) {
        this.transformType = transformType;
    }

    /**
     * Set the transform mode.
     *
     * @param transformMode the transformation mode.
     */
    @FromAnyThread
    public void setTransformMode(@NotNull TransformationMode transformMode) {
        this.transformMode = transformMode;
    }

    /**
     * Get the transform type.
     *
     * @return the current transformation type.
     */
    @FromAnyThread
    public @Nullable TransformType getTransformType() {
        return transformType;
    }

    @Override
    @JmeThread
    public @NotNull TransformationMode getTransformationMode() {
        return transformMode;
    }

    @Override
    @JmeThread
    public @Nullable Transform getTransformCenter() {
        return transformCenter;
    }

    @Override
    @JmeThread
    public void setPickedAxis(@NotNull PickedAxis axis) {
        this.pickedAxis = axis;
    }

    @Override
    @JmeThread
    public @NotNull PickedAxis getPickedAxis() {
        return notNull(pickedAxis);
    }

    @Override
    @JmeThread
    public @Nullable Node getCollisionPlane() {
        if (collisionPlane == null) throw new RuntimeException("collisionPlane is null");
        return collisionPlane;
    }

    @Override
    @JmeThread
    public void setTransformDeltaX(float transformDeltaX) {
        this.transformDeltaX = transformDeltaX;
    }

    @Override
    @JmeThread
    public void setTransformDeltaY(float transformDeltaY) {
        this.transformDeltaY = transformDeltaY;
    }

    @Override
    @JmeThread
    public void setTransformDeltaZ(float transformDeltaZ) {
        this.transformDeltaZ = transformDeltaZ;
    }

    @Override
    @JmeThread
    public float getTransformDeltaX() {
        return transformDeltaX;
    }

    @Override
    @JmeThread
    public float getTransformDeltaY() {
        return transformDeltaY;
    }

    @Override
    @JmeThread
    public float getTransformDeltaZ() {
        return transformDeltaZ;
    }

    @Override
    @JmeThread
    public @Nullable Spatial getToTransform() {
        return toTransform;
    }

    /**
     * Get the grid.
     *
     * @return the grid.
     */
    @FromAnyThread
    private @NotNull Node getGrid() {
        return notNull(grid);
    }

    /**
     * Get the node to place move tool controls.
     *
     * @return the node to place move tool controls.
     */
    @FromAnyThread
    private @NotNull Node getMoveTool() {
        return notNull(moveTool);
    }

    /**
     * Get the node to place rotation tool controls.
     *
     * @return the node to place rotation tool controls.
     */
    @FromAnyThread
    private @NotNull Node getRotateTool() {
        return notNull(rotateTool);
    }

    /**
     * Get the node to place scale tool controls.
     *
     * @return the node to place scale tool controls.
     */
    @FromAnyThread
    private @NotNull Node getScaleTool() {
        return notNull(scaleTool);
    }

    /**
     * Set true of we have active transformation.
     *
     * @param activeTransform true of we have active transformation.
     */
    @FromAnyThread
    private void setActiveTransform(boolean activeTransform) {
        this.activeTransform = activeTransform;
    }

    /**
     * Return true of we have active transformation.
     *
     * @return true of we have active transformation.
     */
    @FromAnyThread
    private boolean isActiveTransform() {
        return activeTransform;
    }

    @Override
    protected void preCameraUpdate(float tpf) {
        super.preCameraUpdate(tpf);

        var selectionCenter = getTransformCenter();
        var transformType = getTransformType();

        // Transform Selected Objects!
        if (isActiveTransform() && selectionCenter != null) {
            if (transformType == TransformType.MOVE_TOOL) {
                var control = getMoveTool().getControl(TransformControl.class);
                transformToolNode.detachAllChildren();
                control.processTransform();
            } else if (transformType == TransformType.ROTATE_TOOL) {
                var control = getRotateTool().getControl(TransformControl.class);
                transformToolNode.detachAllChildren();
                control.processTransform();
            } else if (transformType == TransformType.SCALE_TOOL) {
                var control = getScaleTool().getControl(TransformControl.class);
                transformToolNode.detachAllChildren();
                control.processTransform();
            }
        }
    }

    @Override
    protected void postCameraUpdate(float tpf) {
        super.postCameraUpdate(tpf);

        lightNodes.forEach(EditorLightNode::updateModel);
        audioNodes.forEach(EditorAudioNode::updateModel);
        presentableNodes.forEach(EditorPresentableNode::updateModel);

        selected.forEach(this, (spatial, editor3dPart) -> {

            if (spatial instanceof EditorLightNode) {
                spatial = ((EditorLightNode) spatial).getModel();
            } else if (spatial instanceof EditorAudioNode) {
                spatial = ((EditorAudioNode) spatial).getModel();
            } else if (spatial instanceof EditorPresentableNode) {
                spatial = ((EditorPresentableNode) spatial).getModel();
            }

            if (spatial == null) {
                return;
            }

            editor3dPart.updateTransformNode(spatial.getWorldTransform());

            var selectionShape = editor3dPart.selectionShape;
            var shape = selectionShape.get(spatial);
            if (shape == null) {
                return;
            }

            var position = shape.getLocalTranslation();
            position.set(spatial.getWorldTranslation());

            var center = shape.<Vector3f>getUserData(KEY_SHAPE_CENTER);
            var initScale = shape.<Vector3f>getUserData(KEY_SHAPE_INIT_SCALE);

            if (center != null) {

                if (!initScale.equals(spatial.getLocalScale())) {

                    initScale.set(spatial.getLocalScale());

                    NodeUtils.updateWorldBound(spatial);

                    var bound = (BoundingBox) spatial.getWorldBound();
                    bound.getCenter().subtract(spatial.getWorldTranslation(), center);

                    var mesh = (WireBox) shape.getMesh();
                    mesh.updatePositions(bound.getXExtent(), bound.getYExtent(), bound.getZExtent());
                }

                position.addLocal(center);

            } else {
                shape.setLocalRotation(spatial.getWorldRotation());
                shape.setLocalScale(spatial.getWorldScale());
            }

            shape.setLocalTranslation(position);
        });

        transformToolNode.detachAllChildren();

        if (transformType == TransformType.MOVE_TOOL) {
            transformToolNode.attachChild(getMoveTool());
        } else if (transformType == TransformType.ROTATE_TOOL) {
            transformToolNode.attachChild(getRotateTool());
        } else if (transformType == TransformType.SCALE_TOOL) {
            transformToolNode.attachChild(getScaleTool());
        }

        // FIXME change when will support transform multi-nodes
        if (selected.size() != 1) {
            toolNode.detachChild(transformToolNode);
        } else if (!isPaintingMode()) {
            toolNode.attachChild(transformToolNode);
        }

        if (isPaintingMode()) {
            updatePaintingNodes();
            updatePainting(tpf);
        }
    }

    /**
     * Update editing nodes.
     */
    @JmeThread
    private void updatePaintingNodes() {

        if (!isPaintingMode()) {
            return;
        }

        var control = PaintingUtils.getPaintingControl(cursorNode);
        var paintedModel = PaintingUtils.getPaintedModel(control);

        if (paintedModel == null) {
            return;
        }

        var collisions = GeomUtils.getCollisionsFromCursor(paintedModel, getCamera());

        if (collisions.size() < 1) {
            return;
        }

        CollisionResult result = null;

        for (var collision : collisions) {

            var geometry = collision.getGeometry();
            var parent = NodeUtils.findParent(geometry, spatial ->
                    spatial.getUserData(KEY_IGNORE_RAY_CAST) == Boolean.TRUE);

            if (parent == null) {
                result = collision;
                break;
            }
        }

        if (result == null) {
            result = collisions.getClosestCollision();
        }

        var contactPoint = result.getContactPoint();
        var contactNormal = result.getContactNormal();

        var local = LocalObjects.get();
        var rotation = local.nextRotation();
        rotation.lookAt(contactNormal, Vector3f.UNIT_Y);

        cursorNode.setLocalRotation(rotation);
        cursorNode.setLocalTranslation(contactPoint);
    }

    /**
     * Update the transformation node.
     */
    @JmeThread
    private void updateTransformNode(@Nullable Transform transform) {

        if (transform == null) {
            return;
        }

        var transformationMode = getTransformationMode();
        var location = transform.getTranslation();
        var positionOnCamera = getPositionOnCamera(location);

        transformToolNode.setLocalTranslation(positionOnCamera);
        transformToolNode.setLocalScale(1.5F);
        transformToolNode.setLocalRotation(transformationMode.getToolRotation(transform, getCamera()));
    }

    @JmeThread
    private @NotNull Vector3f getPositionOnCamera(@NotNull Vector3f location) {

        var local = LocalObjects.get();
        var camera = EditorUtils.getGlobalCamera();

        var cameraLocation = camera.getLocation();
        var resultPosition = location.subtract(cameraLocation, local.nextVector())
                .normalizeLocal()
                .multLocal(camera.getFrustumNear() + 0.5f);

        return cameraLocation.add(resultPosition, local.nextVector());
    }

    /**
     * Get the material of selection.
     *
     * @return the material of selection.
     */
    @FromAnyThread
    private @Nullable Material getSelectionMaterial() {
        return selectionMaterial;
    }

    /**
     * Select the objects.
     *
     * @param objects the objects.
     */
    @FromAnyThread
    public void select(@NotNull Array<Spatial> objects) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> selectInJme(objects));
    }

    /**
     * Select the object.
     *
     * @param object the object.
     */
    @FromAnyThread
    public void select(@NotNull Spatial object) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> selectInJme(Array.of(object)));
    }

    /**
     * Select the objects in the jMe thread.
     *
     * @param objects the objects.
     */
    @JmeThread
    private void selectInJme(@NotNull Array<Spatial> objects) {

        if (objects.isEmpty()) {
            selected.forEach(this, (spatial, ed) -> ed.removeFromSelection(spatial));
            selected.clear();
        } else {

            for (var iterator = selected.iterator(); iterator.hasNext(); ) {

                var spatial = iterator.next();
                if (objects.contains(spatial)) {
                    continue;
                }

                removeFromSelection(spatial);
                iterator.fastRemove();
            }

            for (var spatial : objects) {
                if (!selected.contains(spatial)) {
                    addToSelection(spatial);
                }
            }
        }

        updateToTransform();
    }

    /**
     * Update transformation.
     */
    @FromAnyThread
    private void updateToTransform() {
        setToTransform(selected.first());
    }

    /**
     * Get the original transformation.
     *
     * @return the original transformation.
     */
    @FromAnyThread
    private @Nullable Transform getOriginalTransform() {
        return originalTransform;
    }

    /**
     * Get the original transformation.
     *
     * @param originalTransform the original transformation.
     */
    @FromAnyThread
    private void setOriginalTransform(@Nullable Transform originalTransform) {
        this.originalTransform = originalTransform;
    }

    /**
     * Update the transformation's center.
     */
    @JmeThread
    private void updateTransformCenter() {

        var toTransform = getToTransform();
        var transform = toTransform == null ? null : toTransform.getLocalTransform().clone();
        var originalTransform = transform == null ? null : transform.clone();

        setTransformCenter(transform);
        setOriginalTransform(originalTransform);
    }

    /**
     * Add the spatial to selection.
     */
    @JmeThread
    private void addToSelection(@NotNull Spatial spatial) {

        if (spatial instanceof VisibleOnlyWhenSelected) {
            spatial.setCullHint(CullHint.Dynamic);
        }

        selected.add(spatial);

        if (spatial instanceof NoSelection) {
            return;
        }

        Geometry shape;

        if (spatial instanceof ParticleEmitter) {
            shape = buildBoxSelection(spatial);
        } else if (spatial instanceof Geometry) {
            shape = buildGeometrySelection((Geometry) spatial);
        } else {
            shape = buildBoxSelection(spatial);
        }

        if (shape == null) {
            return;
        }

        if (isShowSelection()) {
            toolNode.attachChild(shape);
        }

        selectionShape.put(spatial, shape);
    }

    /**
     * Remove the spatial from the selection.
     */
    @JmeThread
    private void removeFromSelection(@NotNull Spatial spatial) {
        setTransformCenter(null);
        setToTransform(null);

        var shape = selectionShape.remove(spatial);

        if (shape != null) {
            shape.removeFromParent();
        }

        selected.fastRemove(spatial);

        if (spatial instanceof VisibleOnlyWhenSelected) {
            spatial.setCullHint(CullHint.Always);
        }
    }

    /**
     * Build the selection box for the spatial.
     */
    @JmeThread
    private Geometry buildBoxSelection(@NotNull Spatial spatial) {

        NodeUtils.updateWorldBound(spatial);

        var bound = spatial.getWorldBound();

        if (bound instanceof BoundingBox) {

            var boundingBox = (BoundingBox) bound;
            var center = boundingBox.getCenter().subtract(spatial.getWorldTranslation());
            var initScale = spatial.getLocalScale().clone();

            var geometry = WireBox.makeGeometry(boundingBox);
            geometry.setName("SelectionShape");
            geometry.setMaterial(getSelectionMaterial());
            geometry.setUserData(KEY_SHAPE_CENTER, center);
            geometry.setUserData(KEY_SHAPE_INIT_SCALE, initScale);

            var position = geometry.getLocalTranslation();
            position.addLocal(center);

            geometry.setLocalTranslation(position);

            return geometry;

        } else if (bound instanceof BoundingSphere) {

            var boundingSphere = (BoundingSphere) bound;

            var wire = new WireSphere();
            wire.fromBoundingSphere(boundingSphere);

            var geometry = new Geometry("SelectionShape", wire);
            geometry.setMaterial(getSelectionMaterial());
            geometry.setLocalTranslation(spatial.getWorldTranslation());

            return geometry;
        }

        var geometry = WireBox.makeGeometry(new BoundingBox(Vector3f.ZERO, 1, 1, 1));
        geometry.setName("SelectionShape");
        geometry.setMaterial(getSelectionMaterial());
        geometry.setLocalTranslation(spatial.getWorldTranslation());

        return geometry;
    }

    /**
     * Build selection grid for the geometry.
     */
    @JmeThread
    private Geometry buildGeometrySelection(@NotNull Geometry geom) {

        var mesh = geom.getMesh();
        if (mesh == null) {
            return null;
        }

        var geometry = new Geometry("SelectionShape", mesh);
        geometry.setMaterial(getSelectionMaterial());
        geometry.setLocalTransform(geom.getWorldTransform());

        return geometry;
    }

    /**
     * Set true if need to show a grid.
     *
     * @param showGrid true if need to show a grid.
     */
    @JmeThread
    private void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    /**
     * Set true if need to show a selection grid.
     *
     * @param showSelection true if need to show a selection grid.
     */
    @JmeThread
    private void setShowSelection(final boolean showSelection) {
        this.showSelection = showSelection;
    }

    /**
     * Return true if the grid is showed.
     *
     * @return true if the grid is showed.
     */
    @JmeThread
    private boolean isShowGrid() {
        return showGrid;
    }

    /**
     * Return true if a selection grid is showed.
     *
     * @return true if a selection grid is showed.
     */
    @JmeThread
    private boolean isShowSelection() {
        return showSelection;
    }

    @Override
    @JmeThread
    public void notifyTransformed(@NotNull Spatial spatial) {
        fileEditor.notifyTransformed(spatial);
    }

    /**
     * Update showing state of selection grid.
     *
     * @param showSelection true if need to show selection grid.
     */
    @FromAnyThread
    public void updateShowSelection(boolean showSelection) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> updateShowSelectionInJme(showSelection));
    }

    /**
     * Update showing state of selection grid in jME thread.
     *
     * @param showSelection true if need to show selection grid.
     */
    @JmeThread
    private void updateShowSelectionInJme(boolean showSelection) {

        if (isShowSelection() == showSelection) {
            return;
        }

        if (showSelection && !selectionShape.isEmpty()) {
            selectionShape.forEach(toolNode::attachChild);
        } else if (!showSelection && !selectionShape.isEmpty()) {
            selectionShape.forEach(toolNode::detachChild);
        }

        setShowSelection(showSelection);
    }

    @Override
    @JmeThread
    protected void onActionImpl(@NotNull String name, boolean isPressed, float tpf) {
        super.onActionImpl(name, isPressed, tpf);

        if (MOUSE_RIGHT_CLICK.equals(name)) {

            if (isPaintingMode()) {

                if (isPressed) {
                    startPainting(getPaintingInput(MouseButton.SECONDARY));
                } else {
                    finishPainting(getPaintingInput(MouseButton.SECONDARY));
                }

            } else if(!isPressed) {
                processSelect();
            }

        } else if (MOUSE_LEFT_CLICK.equals(name)) {

            if (isPaintingMode()) {

                if (isPressed) {
                    startPainting(getPaintingInput(MouseButton.PRIMARY));
                } else {
                    finishPainting(getPaintingInput(MouseButton.PRIMARY));
                }

            } else {
                if (isPressed) {
                    startTransform();
                } else {
                    endTransform();
                }
            }
        }
    }

    /**
     * Get the painting input.
     *
     * @param mouseButton the mouse button.
     * @return the painting input.
     */
    @FromAnyThread
    protected @NotNull PaintingInput getPaintingInput(@NotNull MouseButton mouseButton) {

        switch (mouseButton) {
            case SECONDARY: {

                if (isControlDown()) {
                    return PaintingInput.MOUSE_SECONDARY_WITH_CTRL;
                }

                return PaintingInput.MOUSE_SECONDARY;
            }
            case PRIMARY: {
                return PaintingInput.MOUSE_PRIMARY;
            }
        }

        return PaintingInput.MOUSE_PRIMARY;
    }

    /**
     * Handling a click in the area of the editor.
     */
    @JmeThread
    private void processSelect() {

        if (isPaintingMode()) {
            return;
        }

        var anyGeometry = GeomUtils.getGeometryFromCursor(modelNode, getCamera());
        var currentModel = notNull(getCurrentModel());

        Object toSelect = anyGeometry == null ? null : findToSelect(anyGeometry);

        if (toSelect == null && anyGeometry != null) {
            var modelGeometry = GeomUtils.getGeometryFromCursor(currentModel, getCamera());
            toSelect = modelGeometry == null ? null : findToSelect(modelGeometry);
        }

        var result = toSelect;

        ExecutorManager.getInstance()
                .addFxTask(() -> notifySelected(result));
    }

    /**
     * Find to select object.
     *
     * @param object the object
     * @return the object
     */
    @JmeThread
    protected @Nullable Object findToSelect(@NotNull Object object) {

        for (var finder : SELECTION_FINDERS.getExtensions()) {
            var spatial = finder.find(object);
            if (spatial != null && spatial.isVisible()) {
                return spatial;
            }
        }

        if (object instanceof Geometry) {

            var spatial = (Spatial) object;
            var parent = NodeUtils.findParent(spatial, 2);

            var lightNode = parent == null ? null : getLightNode(parent);

            if (lightNode != null) {
                return lightNode;
            }

            var audioNode = parent == null ? null : getAudioNode(parent);

            if (audioNode != null) {
                return audioNode;
            }

            parent = NodeUtils.findParent(spatial, AssetLinkNode.class::isInstance);

            if (parent != null) {
                return parent;
            }

            parent = NodeUtils.findParent(spatial,
                    p -> Boolean.TRUE.equals(p.getUserData(KEY_LOADED_MODEL)));

            if (parent != null) {
                return parent;
            }
        }

        if (object instanceof Spatial) {

            var spatial = (Spatial) object;

            if (!spatial.isVisible()) {
                return null;
            } else if (findParent(spatial, sp -> !sp.isVisible()) != null) {
                return null;
            } else if (findParent(spatial, sp -> sp == getCurrentModel()) == null) {
                return null;
            }
        }

        return object;
    }

    /**
     * Get a geometry on a scene for a position on a screen.
     *
     * @param screenX the x position on screen.
     * @param screenY the y position on screen.
     * @return the position on a scene.
     */
    @JmeThread
    public @Nullable Geometry getGeometryByScreenPos(float screenX, float screenY) {
        return GeomUtils.getGeometryFromScreenPos(notNull(getCurrentModel()), getCamera(), screenX, screenY);
    }

    @FxThread
    private void notifySelected(@Nullable Object object) {
        fileEditor.notifySelected(object);
    }

    /**
     * Get a position on a scene for a cursor position.
     *
     * @param screenX the x position on screen.
     * @param screenY the y position on screen.
     * @return the position on a scene.
     */
    @JmeThread
    public @NotNull Vector3f getScenePosByScreenPos(float screenX, float screenY) {

        var camera = getCamera();
        var currentModel = notNull(getCurrentModel());

        var modelPoint = GeomUtils.getContactPointFromScreenPos(currentModel, camera, screenX, screenY);
        var gridPoint = GeomUtils.getContactPointFromScreenPos(getGrid(), camera, screenX, screenY);

        if (modelPoint == null) {
            return gridPoint == null ? Vector3f.ZERO : gridPoint;
        } else if (gridPoint == null) {
            return modelPoint;
        }

        var distance = modelPoint.distance(camera.getLocation());

        if (gridPoint.distance(camera.getLocation()) < distance) {
            return gridPoint;
        } else {
            return modelPoint;
        }
    }

    /**
     * Get a normal on a scene for a cursor position.
     *
     * @param screenX the x position on screen.
     * @param screenY the y position on screen.
     * @return the normal on the current scene or null.
     */
    @JmeThread
    public @Nullable Vector3f getSceneNormalByScreenPos(float screenX, float screenY) {
        var camera = getCamera();
        var currentModel = notNull(getCurrentModel());
        return GeomUtils.getContactNormalFromScreenPos(currentModel, camera, screenX, screenY);
    }

    /**
     * Update the state of showing a scene's grid.
     *
     * @param showGrid true if need to show scene's grid.
     */
    @FromAnyThread
    public void updateShowGrid(boolean showGrid) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> updateShowGridInJme(showGrid));
    }

    /**
     * Update the state of showing a scene's grid in jME thread.
     *
     * @param showGrid true if need to show scene's grid.
     */
    @JmeThread
    private void updateShowGridInJme(boolean showGrid) {

        if (isShowGrid() == showGrid) {
            return;
        }

        var grid = getGrid();

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
    @JmeThread
    private void endTransform() {

        if (!isActiveTransform()) {
            return;
        }

        var originalTransform = getOriginalTransform();
        var toTransform = getToTransform();
        var currentModel = getCurrentModel();

        if (currentModel == null) {
            LOGGER.warning(this, "not found current model for finishing transform...");
            return;
        } else if (originalTransform == null || toTransform == null) {
            LOGGER.warning(this, "not found originalTransform or toTransform");
            return;
        }

        POST_TRANSFORM_HANDLERS.getExtensions()
                .forEach(handler -> handler.handle(toTransform));

        var oldValue = originalTransform.clone();
        var newValue = toTransform.getLocalTransform().clone();

        var operation = new PropertyOperation<ChangeConsumer, Spatial, Transform>(toTransform,
                "internal_transformation", newValue, oldValue);

        operation.setApplyHandler((spatial, transform) -> {

            var preHandlers = PRE_TRANSFORM_HANDLERS.getExtensions();
            var postHandlers = POST_TRANSFORM_HANDLERS.getExtensions();

            preHandlers.forEach(handler -> handler.handle(spatial));
            try {
                spatial.setLocalTransform(transform);
            } finally {
                postHandlers.forEach(handler -> handler.handle(spatial));
            }
        });

        setPickedAxis(PickedAxis.NONE);
        setActiveTransform(false);
        setTransformDeltaX(Float.NaN);
        updateTransformCenter();

        fileEditor.execute(operation);
    }

    /**
     * Start transformation.
     */
    @JmeThread
    private boolean startTransform() {

        updateTransformCenter();

        var camera = EditorUtils.getGlobalCamera();
        var inputManager = EditorUtils.getInputManager();
        var cursorPosition = inputManager.getCursorPosition();

        var collisionResults = new CollisionResults();

        var position = camera.getWorldCoordinates(cursorPosition, 0f);
        var direction = camera.getWorldCoordinates(cursorPosition, 1f)
                .subtractLocal(position)
                .normalizeLocal();

        var ray = new Ray();
        ray.setOrigin(position);
        ray.setDirection(direction);

        transformToolNode.collideWith(ray, collisionResults);

        if (collisionResults.size() < 1) {
            return false;
        }

        var toTransform = getToTransform();

        if (toTransform != null) {
            PRE_TRANSFORM_HANDLERS.getExtensions().
                    forEach(handler -> handler.handle(toTransform));
        }

        var collisionResult = collisionResults.getClosestCollision();
        var transformType = getTransformType();

        if (transformType == TransformType.MOVE_TOOL) {
            var moveTool = getMoveTool();
            var control = moveTool.getControl(TransformControl.class);
            control.setCollisionPlane(collisionResult);
        } else if (transformType == TransformType.ROTATE_TOOL) {
            var rotateTool = getRotateTool();
            var control = rotateTool.getControl(TransformControl.class);
            control.setCollisionPlane(collisionResult);
        } else if (transformType == TransformType.SCALE_TOOL) {
            var scaleTool = getScaleTool();
            var control = scaleTool.getControl(TransformControl.class);
            control.setCollisionPlane(collisionResult);
        }

        setActiveTransform(true);
        return true;
    }

    /**
     * Start painting.
     */
    @JmeThread
    private void startPainting(@NotNull PaintingInput input) {

        var control = PaintingUtils.getPaintingControl(cursorNode);
        var paintedModel = PaintingUtils.getPaintedModel(cursorNode);

        if (control == null || paintedModel == null || control.isStartedPainting()) {
            return;
        }

        control.startPainting(input, cursorNode.getLocalRotation(), cursorNode.getLocalTranslation());
    }

    /**
     * Finish painting.
     */
    @JmeThread
    private void finishPainting(@NotNull PaintingInput input) {

        var control = PaintingUtils.getPaintingControl(cursorNode);
        var paintedModel = PaintingUtils.getPaintedModel(control);

        if (control == null || paintedModel == null) {
            return;
        } else if (!control.isStartedPainting() || control.getCurrentInput() != input) {
            return;
        }

        control.finishPainting(cursorNode.getLocalRotation(), cursorNode.getLocalTranslation());
    }

    /**
     * Update painting.
     */
    @JmeThread
    private void updatePainting(float tpf) {

        var control = PaintingUtils.getPaintingControl(cursorNode);
        var model = PaintingUtils.getPaintedModel(control);

        if (control == null || model == null || !control.isStartedPainting()) {
            return;
        }

        control.updatePainting(cursorNode.getLocalRotation(), cursorNode.getLocalTranslation(), tpf);
    }

    /**
     * Notify about an attempt to change the property from jME thread.
     *
     * @param object the object.
     * @param name   the property name.
     */
    @JmeThread
    public void notifyPropertyPreChanged(@NotNull Object object, @NotNull String name) {
        if (object instanceof Spatial) {
            if (isTransformationProperty(name)) {
                PRE_TRANSFORM_HANDLERS.getExtensions()
                        .forEach(handler -> handler.handle((Spatial) object));
            }
        }
    }

    /**
     * Notify about property changes.
     *
     * @param object the object with changes.
     * @param name   the property name.
     */
    @JmeThread
    public void notifyPropertyChanged(@NotNull Object object, @NotNull String name) {

        if (object instanceof EditableProperty) {
            object = ((EditableProperty<?, ?>) object).getObject();
        }

        if (object instanceof AudioNode) {

            getAudioNodeOpt((AudioNode) object)
                    .ifPresent(EditorAudioNode::sync);

        } else if (object instanceof Light) {

            getLightNodeOpt((Light) object)
                    .ifPresent(EditorLightNode::sync);

        } else if (object instanceof ScenePresentable) {

            getPresentableNodeOpt((ScenePresentable) object).stream()
                    .peek(EditorPresentableNode::sync)
                    .forEach(EditorPresentableNode::updateGeometry);
        }

        if (object instanceof Spatial) {
            if (isTransformationProperty(name)) {
                var transformed = object;
                POST_TRANSFORM_HANDLERS.getExtensions()
                        .forEach(handler -> handler.handle((Spatial) transformed));
            }
        }
    }

    protected boolean isTransformationProperty(@NotNull String name) {
        return Messages.MODEL_PROPERTY_LOCATION.equals(name) ||
            Messages.MODEL_PROPERTY_SCALE.equals(name) ||
            Messages.MODEL_PROPERTY_ROTATION.equals(name) ||
            Messages.MODEL_PROPERTY_TRANSFORMATION.equals(name);
    }

    /**
     * Set the object to transform.
     *
     * @param toTransform the object to transform.
     */
    @JmeThread
    private void setToTransform(@Nullable Spatial toTransform) {
        this.toTransform = toTransform;
    }

    /**
     * Set the center of transformation.
     *
     * @param transformCenter the center of transformation.
     */
    @JmeThread
    private void setTransformCenter(@Nullable Transform transformCenter) {
        this.transformCenter = transformCenter;
    }

    /**
     * Show the model in this editor.
     *
     * @param model the model.
     */
    @FromAnyThread
    public void openModel(@NotNull M model) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> openModelInJme(model));
    }

    /**
     * Show the model in this editor in jME thread.
     *
     * @param model the model.
     */
    @JmeThread
    private void openModelInJme(@NotNull M model) {

        var currentModel = getCurrentModel();

        if (currentModel != null) {
            detachPrevModel(modelNode, currentModel);
        }

        NodeUtils.visitGeometry(model, geometry -> {

            var renderManager = EditorUtils.getRenderManager();
            try {
                renderManager.preloadScene(geometry);
            } catch (final RendererException | AssetNotFoundException | UnsupportedOperationException e) {
                EditorUtils.handleException(LOGGER, this,
                        new RuntimeException("Found invalid material in the geometry: [" + geometry.getName() + "]. " +
                                "The material will be removed from the geometry.", e));
                geometry.setMaterial(EditorUtils.getDefaultMaterial());
            }
        });

        PRE_TRANSFORM_HANDLERS.getExtensions()
                .forEach(handler -> handler.handle(model));

        attachModel(model, modelNode);

        POST_TRANSFORM_HANDLERS.getExtensions()
                .forEach(handler -> handler.handle(model));

        setCurrentModel(model);
    }

    @JmeThread
    protected void detachPrevModel(@NotNull Node modelNode, @Nullable M currentModel) {
        if (currentModel != null) {
            modelNode.detachChild(currentModel);
        }
    }

    @JmeThread
    protected void attachModel(@NotNull M model, @NotNull Node modelNode) {
        modelNode.attachChild(model);
    }

    /**
     * Set the current model.
     *
     * @param currentModel the current model.
     */
    @JmeThread
    private void setCurrentModel(@Nullable M currentModel) {
        this.currentModel = currentModel;
    }

    /**
     * Get the current model.
     *
     * @return the current model.
     */
    @FromAnyThread
    public @Nullable M getCurrentModel() {
        return currentModel;
    }

    /**
     * Add the light to this editor.
     *
     * @param light the light.
     */
    @FromAnyThread
    public void addLight(@NotNull Light light) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> addLightInJme(light));
    }

    /**
     * Add the light to this editor in jME thread.
     *
     * @param light the light.
     */
    @JmeThread
    private void addLightInJme(@NotNull Light light) {

        var node = LIGHT_MODEL_TABLE.get(light.getType());

        if (node == null) {
            return;
        }

        var camera = EditorUtils.getGlobalCamera();

        var lightModel = notNull(cachedLights.get(light, () -> {

            var model = (Node) node.clone();
            model.setLocalScale(0.03F);

            var result = new EditorLightNode(camera);
            result.setModel(model);

            var geometry = NodeUtils.findGeometry(model, "White");

            if (geometry == null) {
                LOGGER.warning(this, "not found geometry for the node " + geometry);
                return null;
            }

            var material = geometry.getMaterial();
            material.setColor("Color", light.getColor());

            return result;
        }));

        lightModel.setLight(light);
        lightModel.sync();

        lightNode.attachChild(lightModel);
        lightNode.attachChild(lightModel.getModel());

        lightNodes.add(lightModel);
    }

    /**
     * Move editor's camera to the location.
     *
     * @param location the location.
     */
    @FromAnyThread
    public void moveCameraTo(@NotNull Vector3f location) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> getNodeForCamera().setLocalTranslation(location));
    }

    /**
     * Look at the spatial.
     *
     * @param spatial the spatial.
     */
    @FromAnyThread
    public void cameraLookAt(@NotNull Spatial spatial) {

        var executorManager = ExecutorManager.getInstance();
        executorManager.addJmeTask(() -> {

            var editorCamera = notNull(getEditorCamera());
            var local = LocalObjects.get();

            float distance;

            var worldBound = spatial.getWorldBound();

            if (worldBound != null) {

                distance = worldBound.getVolume();

                if (worldBound instanceof BoundingBox) {
                    final BoundingBox boundingBox = (BoundingBox) worldBound;
                    distance = boundingBox.getXExtent();
                    distance = Math.max(distance, boundingBox.getYExtent());
                    distance = Math.max(distance, boundingBox.getZExtent());
                    distance *= 2F;
                } else if (worldBound instanceof BoundingSphere) {
                    distance = ((BoundingSphere) worldBound).getRadius() * 2F;
                }

            } else {

               distance = getCamera().getLocation()
                       .distance(spatial.getWorldTranslation());
            }

            editorCamera.setTargetDistance(distance);

            var position = local.nextVector()
                    .set(spatial.getWorldTranslation());

            getNodeForCamera().setLocalTranslation(position);
        });
    }

    /**
     * Remove the light from this editor.
     *
     * @param light the light.
     */
    @FromAnyThread
    public void removeLight(@NotNull Light light) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> removeLightInJme(light));
    }

    /**
     * Remove the light from this editor in jME thread.
     *
     * @param light the light.
     */
    @JmeThread
    private void removeLightInJme(@NotNull Light light) {

        var node = LIGHT_MODEL_TABLE.get(light.getType());

        if (node == null) {
            return;
        }

        var lightModel = cachedLights.get(light);
        if (lightModel == null) {
            return;
        }

        lightModel.setLight(null);

        lightNode.detachChild(lightModel);
        lightNode.detachChild(notNull(lightModel.getModel()));

        lightNodes.fastRemove(lightModel);
    }

    /**
     * Add the audio node to this editor.
     *
     * @param audioNode the audio node.
     */
    @FromAnyThread
    public void addAudioNode(@NotNull AudioNode audioNode) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> addAudioNodeInJme(audioNode));
    }

    /**
     * Add the audio node to this editor in jME thread.
     *
     * @param audio the audio node.
     */
    @JmeThread
    private void addAudioNodeInJme(@NotNull AudioNode audio) {

        var audioModel = notNull(cachedAudioNodes.get(audio, () -> {

            var model = (Node) AUDIO_NODE_MODEL.clone();
            model.setLocalScale(0.01F);

            var result = new EditorAudioNode(getCamera());
            result.setModel(model);

            return result;
        }));

        audioModel.setAudioNode(audio);
        audioModel.sync();

        audioNode.attachChild(audioModel);
        audioNode.attachChild(audioModel.getModel());

        audioNodes.add(audioModel);
    }

    /**
     * Remove the audio node from this editor.
     *
     * @param audio the audio node.
     */
    @FromAnyThread
    public void removeAudioNode(@NotNull AudioNode audio) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> removeAudioNodeInJme(audio));
    }

    /**
     * Remove the audio node from this editor in jME thread.
     *
     * @param audio the audio node.
     */
    @JmeThread
    private void removeAudioNodeInJme(@NotNull AudioNode audio) {

        var audioModel = cachedAudioNodes.get(audio);

        if (audioModel == null) {
            return;
        }

        audioModel.setAudioNode(null);

        audioNode.detachChild(audioModel);
        audioNode.detachChild(notNull(audioModel.getModel()));

        audioNodes.fastRemove(audioModel);
    }

    /**
     * Add the presentable object to this editor.
     *
     * @param presentable the presentable object.
     */
    @FromAnyThread
    public void addPresentable(@NotNull ScenePresentable presentable) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> addPresentableInJme(presentable));
    }

    /**
     * Add the presentable object to this editor in jME.
     *
     * @param presentable the presentable object.
     */
    @JmeThread
    private void addPresentableInJme(@NotNull ScenePresentable presentable) {

        var node = notNull(cachedPresentableObjects.get(presentable, EditorPresentableNode::new));

        node.setObject(presentable);
        node.updateGeometry();
        node.sync();

        var editedNode = node.getEditedNode();
        editedNode.setCullHint(CullHint.Always);

        presentableNode.attachChild(node);
        presentableNode.attachChild(node.getModel());

        node.setObject(presentable);

        presentableNodes.add(node);
    }

    /**
     * Remove the presentable object from this editor.
     *
     * @param presentable the presentable.
     */
    @FromAnyThread
    public void removePresentable(@NotNull ScenePresentable presentable) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> removePresentableInJme(presentable));
    }

    /**
     * Remove the presentable object from this editor in jME thread.
     *
     * @param presentable the presentable.
     */
    @JmeThread
    private void removePresentableInJme(@NotNull ScenePresentable presentable) {

        var node = cachedPresentableObjects.get(presentable);

        if (node == null) {
            return;
        }

        node.setObject(null);

        presentableNode.detachChild(node);
        presentableNode.detachChild(notNull(node.getModel()));

        presentableNodes.fastRemove(node);
    }

    /**
     * Get a light node for the light.
     *
     * @param light the light.
     * @return the light node or null.
     */
    @FromAnyThread
    public @Nullable EditorLightNode getLightNode(@NotNull Light light) {
        return lightNodes.findAny(light,
                (node, toCheck) -> node.getLight() == toCheck);
    }

    /**
     * Get an optional of a light node for the light.
     *
     * @param light the light.
     * @return the optional value.
     */
    @FromAnyThread
    public @NotNull Optional<EditorLightNode> getLightNodeOpt(@NotNull Light light) {
        return Optional.ofNullable(getLightNode(light));
    }

    /**
     * Get a light node for the model.
     *
     * @param model the model.
     * @return the light node or null.
     */
    @FromAnyThread
    public @Nullable EditorLightNode getLightNode(@NotNull Spatial model) {
        return lightNodes.findAny(model,
                (node, toCheck) -> node.getModel() == toCheck);
    }

    /**
     * Get an editor audio node of the audio node.
     *
     * @param audioNode the audio node.
     * @return the editor audio node or null.
     */
    @FromAnyThread
    public @Nullable EditorAudioNode getAudioNode(@NotNull AudioNode audioNode) {
        return audioNodes.findAny(audioNode,
                (node, toCheck) -> node.getAudioNode() == toCheck);
    }

    /**
     * Get an optional of an editor audio node for the audio node.
     *
     * @param audioNode the audio node.
     * @return the optional value.
     */
    @FromAnyThread
    public @NotNull Optional<EditorAudioNode> getAudioNodeOpt(@NotNull AudioNode audioNode) {
        return Optional.ofNullable(getAudioNode(audioNode));
    }

    /**
     * Get an editor audio node for the model.
     *
     * @param model the model.
     * @return the editor audio node or null.
     */
    @FromAnyThread
    public @Nullable EditorAudioNode getAudioNode(@NotNull Spatial model) {
        return audioNodes.findAny(model,
                (node, toCheck) -> node.getModel() == toCheck);
    }

    /**
     * Get an optional of an editor audio node for the model.
     *
     * @param model the model.
     * @return the optional value.
     */
    @FromAnyThread
    public @NotNull Optional<EditorAudioNode> getAudioNodeOpt(@NotNull Spatial model) {
        return Optional.ofNullable(getAudioNode(model));
    }

    /**
     * Get an editor presentable node for the presentable object.
     *
     * @param presentable the presentable object.
     * @return the editor presentable node or null.
     */
    @FromAnyThread
    public @Nullable EditorPresentableNode getPresentableNode(@NotNull ScenePresentable presentable) {
        return presentableNodes.findAny(presentable,
                (node, toCheck) -> node.getObject() == toCheck);
    }

    /**
     * Get an optional of an editor presentable node for the presentable object.
     *
     * @param presentable the presentable object.
     * @return the optional value.
     */
    @FromAnyThread
    public @NotNull Optional<EditorPresentableNode> getPresentableNodeOpt(@NotNull ScenePresentable presentable) {
        return Optional.ofNullable(getPresentableNode(presentable));
    }

    /**
     * Get an editor presentable node for the model.
     *
     * @param model the model.
     * @return the editor presentable node or null.
     */
    @FromAnyThread
    public @Nullable EditorPresentableNode getPresentableNode(@NotNull Spatial model) {
        return presentableNodes.findAny(model,
                (node, toCheck) -> node.getModel() == toCheck);
    }

    @Override
    @JmeThread
    protected void notifyChangedCameraSettings(
            @NotNull Vector3f cameraLocation,
            float hRotation,
            float vRotation,
            float targetDistance,
            float cameraFlySpeed
    ) {
        super.notifyChangedCameraSettings(cameraLocation, hRotation, vRotation, targetDistance, cameraFlySpeed);

        var executorManager = ExecutorManager.getInstance();
        executorManager.addFxTask(() ->
                fileEditor.notifyChangedCameraSettings(cameraLocation, hRotation, vRotation, targetDistance, cameraFlySpeed));
    }

    /**
     * Set true if painting mode is enabled.
     *
     * @param paintingMode true if painting mode is enabled.
     */
    @JmeThread
    private void setPaintingMode(boolean paintingMode) {
        this.paintingMode = paintingMode;
    }

    /**
     * Return true if painting mode is enabled.
     *
     * @return true if painting mode is enabled.
     */
    @JmeThread
    private boolean isPaintingMode() {
        return paintingMode;
    }

    /**
     * Change enabling of painting mode.
     *
     * @param paintingMode true if painting mode is enabled.
     */
    @FromAnyThread
    public void changePaintingMode(boolean paintingMode) {
        ExecutorManager.getInstance()
                .addJmeTask(() -> changePaintingModeInJme(paintingMode));
    }

    /**
     * Change enabling of painting mode in jME thread.
     *
     * @param paintingMode true if painting mode is enabled.
     */
    @JmeThread
    private void changePaintingModeInJme(boolean paintingMode) {
        setPaintingMode(paintingMode);

        if (isPaintingMode()) {
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
     * Get the cursor node.
     *
     * @return the cursor node.
     */
    @JmeThread
    public @NotNull Node getCursorNode() {
        return cursorNode;
    }

    /**
     * Get the markers node.
     *
     * @return the markers node.
     */
    @JmeThread
    public @NotNull Node getMarkersNode() {
        return markersNode;
    }

    @Override
    @JmeThread
    public @NotNull Camera getCamera() {
        return EditorUtils.getGlobalCamera();
    }
}
