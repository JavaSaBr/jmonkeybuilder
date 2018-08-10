package com.ss.editor.part3d.editor.impl.scene.control;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.transform.*;
import com.ss.editor.part3d.editor.EditableSceneEditor3dPart;
import com.ss.editor.part3d.editor.ExtendableEditor3dPart;
import com.ss.editor.part3d.editor.control.impl.BaseInputEditor3dPartControl;
import com.ss.editor.part3d.editor.event.Editor3dPartEvent;
import com.ss.editor.util.JmeUtils;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The control to implement painting support on the scene editor 3d part.
 *
 * @author JavaSaBr
 */
public class TransformationSupportEditor3dPartControl<T extends EditableSceneEditor3dPart & ExtendableEditor3dPart>
        extends BaseInputEditor3dPartControl<T> implements EditorTransformSupport {

    private static final ObjectDictionary<String, Trigger> TRIGGERS =
            ObjectDictionary.ofType(String.class, Trigger.class);

    private static final String MOUSE_LEFT_CLICK = "jMB.transformationSupportEditor.mouseLeftClick";

    private static final String[] MAPPINGS;

    static {

        TRIGGERS.put(MOUSE_LEFT_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        MAPPINGS = TRIGGERS.keyArray(String.class)
                .toArray(String.class);
    }

    /**
     * The node for the placement of transform controls.
     */
    @NotNull
    private final Node transformToolNode;

    /**
     * The difference between the previous point of transformation and new.
     */
    @NotNull
    private final Vector3f transformDelta;

    /**
     * The nodes with transformation control models.
     */
    @NotNull
    private final Node moveTool, rotateTool, scaleTool;

    /**
     * The plane for calculation transforms.
     */
    @NotNull
    private final Node collisionPlane;

    @NotNull
    private final ObjectDictionary<TransformType, Node> transformationTypeToNode;

    /**
     * The transformation mode.
     */
    @NotNull
    private TransformationMode transformMode;

    /**
     * The current type of transformation.
     */
    @NotNull
    private TransformType transformType;

    /**
     * The current direction of transformation.
     */
    @NotNull
    private PickedAxis pickedAxis;

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
     * The flag of existing active transformation.
     */
    private boolean activeTransform;

    private boolean initialized;

    public TransformationSupportEditor3dPartControl(@NotNull T editor3dPart) {
        super(editor3dPart);

        this.transformToolNode = new Node("TransformToolNode");
        this.transformMode = TransformationMode.GLOBAL;
        this.transformType = TransformType.MOVE_TOOL;
        this.pickedAxis = PickedAxis.NONE;
        this.transformDelta = new Vector3f(Float.NaN, Float.NaN, Float.NaN);
        this.collisionPlane = new Node();
        this.moveTool = new Node("Move tool");
        this.moveTool.addControl(new MoveToolControl(this));
        this.rotateTool = new Node("Rotate tool");
        this.rotateTool.addControl(new RotationToolControl(this));
        this.scaleTool = new Node("Scale tool");
        this.scaleTool.addControl(new ScaleToolControl(this));
        this.transformationTypeToNode = ObjectDictionary.of(
                TransformType.MOVE_TOOL, moveTool,
                TransformType.ROTATE_TOOL, rotateTool,
                TransformType.SCALE_TOOL, scaleTool
        );

        actionHandlers.put(MOUSE_LEFT_CLICK, (isPressed, tpf) -> {
            if (isPressed) {
                startTransform();
            } else {
                endTransform();
            }
        });
    }

    @JmeThread
    @Override
    public @Nullable Transform getTransformCenter() {
        return null;
    }

    @JmeThread
    @Override
    public void setPickedAxis(@NotNull PickedAxis axis) {

    }

    @JmeThread
    @Override
    public @NotNull PickedAxis getPickedAxis() {
        return null;
    }

    @JmeThread
    @Override
    public @NotNull EditorTransformSupport.TransformationMode getTransformationMode() {
        return null;
    }

    @Override
    @JmeThread
    public @NotNull Node getCollisionPlane() {
        return collisionPlane;
    }

    @JmeThread
    @Override
    public void setTransformDeltaX(float transformDeltaX) {

    }

    @JmeThread
    @Override
    public void setTransformDeltaY(float transformDeltaY) {

    }

    @JmeThread
    @Override
    public void setTransformDeltaZ(float transformDeltaZ) {

    }

    @JmeThread
    @Override
    public float getTransformDeltaX() {
        return 0;
    }

    @JmeThread
    @Override
    public float getTransformDeltaY() {
        return 0;
    }

    @JmeThread
    @Override
    public float getTransformDeltaZ() {
        return 0;
    }

    @JmeThread
    @Override
    public @Nullable Spatial getToTransform() {
        return null;
    }

    @JmeThread
    @Override
    public void notifyTransformed(@NotNull Spatial spatial) {

    }

    @Override
    @JmeThread
    public @NotNull Camera getCamera() {
        return editor3dPart.getCamera();
    }

    @Override
    @JmeThread
    public void register(@NotNull InputManager inputManager) {
        TRIGGERS.forEach(inputManager, JmeUtils::addMapping);
        inputManager.addListener(this, MAPPINGS);
    }

    /**
     * Create manipulators.
     *
     * @param assetManager the asset manager.
     */
    @JmeThread
    private void initializeManipulators(@NotNull AssetManager assetManager) {

        if (initialized) {
            return;
        }

        var transparentMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        var renderState = transparentMaterial.getAdditionalRenderState();
        renderState.setFaceCullMode(RenderState.FaceCullMode.Off);
        renderState.setWireframe(true);

        var size = 20000F;

        var collisionGeometry = new Geometry("plane", new Quad(size, size));
        collisionGeometry.setMaterial(transparentMaterial);
        collisionGeometry.setLocalTranslation(-size / 2, -size / 2, 0);

        var redMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        redMaterial.setColor("Color", ColorRGBA.Red);

        var blueMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        blueMaterial.setColor("Color", ColorRGBA.Blue);

        var greenMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        greenMaterial.setColor("Color", ColorRGBA.Green);

        var moveTool = (Node) assetManager.loadModel("graphics/models/manipulators/manipulators_move.j3o");
        moveTool.getChild("move_x").setMaterial(redMaterial);
        moveTool.getChild("collision_move_x").setMaterial(redMaterial);
        moveTool.getChild("collision_move_x").setCullHint(Spatial.CullHint.Always);
        moveTool.getChild("move_y").setMaterial(blueMaterial);
        moveTool.getChild("collision_move_y").setMaterial(blueMaterial);
        moveTool.getChild("collision_move_y").setCullHint(Spatial.CullHint.Always);
        moveTool.getChild("move_z").setMaterial(greenMaterial);
        moveTool.getChild("collision_move_z").setMaterial(greenMaterial);
        moveTool.getChild("collision_move_z").setCullHint(Spatial.CullHint.Always);
        moveTool.scale(0.2f);

        var rotateTool = (Node) assetManager.loadModel("graphics/models/manipulators/manipulators_rotate.j3o");
        rotateTool.getChild("rot_x").setMaterial(redMaterial);
        rotateTool.getChild("collision_rot_x").setMaterial(redMaterial);
        rotateTool.getChild("collision_rot_x").setCullHint(Spatial.CullHint.Always);
        rotateTool.getChild("rot_y").setMaterial(blueMaterial);
        rotateTool.getChild("collision_rot_y").setMaterial(blueMaterial);
        rotateTool.getChild("collision_rot_y").setCullHint(Spatial.CullHint.Always);
        rotateTool.getChild("rot_z").setMaterial(greenMaterial);
        rotateTool.getChild("collision_rot_z").setMaterial(greenMaterial);
        rotateTool.getChild("collision_rot_z").setCullHint(Spatial.CullHint.Always);
        rotateTool.scale(0.2f);

        var scaleTool = (Node) assetManager.loadModel("graphics/models/manipulators/manipulators_scale.j3o");
        scaleTool.getChild("scale_x").setMaterial(redMaterial);
        scaleTool.getChild("collision_scale_x").setMaterial(redMaterial);
        scaleTool.getChild("collision_scale_x").setCullHint(Spatial.CullHint.Always);
        scaleTool.getChild("scale_y").setMaterial(blueMaterial);
        scaleTool.getChild("collision_scale_y").setMaterial(blueMaterial);
        scaleTool.getChild("collision_scale_y").setCullHint(Spatial.CullHint.Always);
        scaleTool.getChild("scale_z").setMaterial(greenMaterial);
        scaleTool.getChild("collision_scale_z").setMaterial(greenMaterial);
        scaleTool.getChild("collision_scale_z").setCullHint(Spatial.CullHint.Always);
        scaleTool.scale(0.2f);

        this.moveTool.attachChild(moveTool);
        this.rotateTool.attachChild(rotateTool);
        this.scaleTool.attachChild(scaleTool);
        this.collisionPlane.attachChild(collisionGeometry);

        initialized = true;
    }

    @Override
    @JmeThread
    public void initialize(@NotNull Application application) {
        initializeManipulators(application.getAssetManager());
    }

    @Override
    @JmeThread
    public void preCameraUpdate(float tpf) {

        // Transform Selected Objects!
        /*if (!activeTransform || selectionCenter == null) {
            return;
        }*/

        transformToolNode.detachAllChildren();
        transformationTypeToNode.getOptional(transformType)
                .map(node -> node.getControl(TransformControl.class))
                .ifPresent(TransformControl::processTransform);
    }

    @Override
    @JmeThread
    public void postCameraUpdate(float tpf) {

        transformToolNode.detachAllChildren();
        transformationTypeToNode.getOptional(transformType)
            .ifPresent(transformToolNode::attachChild);

        // FIXME change when will support transform multi-nodes
       /* if (selected.size() != 1) {
            toolNode.detachChild(transformToolNode);
        } else if (!isPaintingMode()) {
            toolNode.attachChild(transformToolNode);
        }*/
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
        var positionOnCamera = JmeUtils.getPositionOnCamera(location, editor3dPart.getCamera());

        transformToolNode.setLocalTranslation(positionOnCamera);
        transformToolNode.setLocalScale(1.5F);
        transformToolNode.setLocalRotation(transformationMode.getToolRotation(transform, getCamera()));
    }

    /**
     * Update transformation.
     */
    @FromAnyThread
    private void updateToTransform() {
        //setToTransform(selected.first());
    }

    /**
     * Update the transformation's center.
     */
    @JmeThread
    private void updateTransformCenter() {

        var toTransform = getToTransform();
        var transform = toTransform == null ? null : toTransform.getLocalTransform().clone();
        var originalTransform = transform == null ? null : transform.clone();

        //setTransformCenter(transform);
        //setOriginalTransform(originalTransform);
    }

    /**
     * Finish the transformation of the model.
     */
    @JmeThread
    private void endTransform() {

        /*if (!isActiveTransform()) {
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

        fileEditor.execute(operation);*/
    }

    /**
     * Start transformation.
     */
    @JmeThread
    private boolean startTransform() {

        /*updateTransformCenter();

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
        return true;*/
        return false;
    }

    @Override
    protected void notifyImpl(@NotNull Editor3dPartEvent event) {
        super.notifyImpl(event);
    }
}
