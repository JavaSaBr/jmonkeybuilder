package com.ss.editor.control.transform;

import static com.ss.editor.control.transform.TransformConstraint.constraintValue;
import static com.ss.editor.control.transform.TransformConstraint.getRotateConstraint;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.util.Objects.requireNonNull;

import com.jme3.collision.CollisionResult;
import com.jme3.input.InputManager;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.ss.editor.Editor;
import com.ss.editor.control.transform.SceneEditorControl.PickedAxis;

import org.jetbrains.annotations.NotNull;

import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;

/**
 * The implementation of the rotating control.
 *
 * @author JavaSaBr
 */
public class RotationToolControl extends AbstractControl implements TransformControl {

    /**
     * The constant LOGGER.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(MoveToolControl.class);

    private static final String NODE_ROTATION_X = "rot_x";
    private static final String NODE_ROTATION_Y = "rot_y";
    private static final String NODE_ROTATION_Z = "rot_z";

    @NotNull
    private static final Editor EDITOR = Editor.getInstance();

    /**
     * The scene editor controller.
     */
    @NotNull
    private final SceneEditorControl editorControl;

    /**
     * The collision plane.
     */
    @NotNull
    private final Node collisionPlane;

    /**
     * Instantiates a new Rotation tool control.
     *
     * @param editorControl the editor control
     */
    public RotationToolControl(@NotNull final SceneEditorControl editorControl) {
        this.editorControl = editorControl;
        this.collisionPlane = notNull(editorControl.getCollisionPlane());
    }

    /**
     * @return the collision plane.
     */
    @NotNull
    private Node getCollisionPlane() {
        return collisionPlane;
    }

    /**
     * @return the scene editor controller.
     */
    @NotNull
    private SceneEditorControl getEditorControl() {
        return editorControl;
    }

    @Override
    public void setCollisionPlane(@NotNull final CollisionResult colResult) {

        final Camera camera = EDITOR.getCamera();
        final SceneEditorControl editorControl = getEditorControl();
        final Transform selectedCenter = editorControl.getTransformCenter();

        if (selectedCenter == null) {
            LOGGER.warning(this, "not found transform center for the " + editorControl);
            return;
        }

        // Set PickedAxis
        final Geometry geometry = colResult.getGeometry();
        final String geometryName = geometry.getName();

        if (geometryName.contains(NODE_ROTATION_X)) {
            editorControl.setPickedAxis(PickedAxis.X);
        } else if (geometryName.contains(NODE_ROTATION_Y)) {
            editorControl.setPickedAxis(PickedAxis.Y);
        } else if (geometryName.contains(NODE_ROTATION_Z)) {
            editorControl.setPickedAxis(PickedAxis.Z);
        }

        final PickedAxis pickedAxis = editorControl.getPickedAxis();

        // set the collision Plane location and rotation
        final Node collisionPlane = getCollisionPlane();
        collisionPlane.setLocalTranslation(selectedCenter.getTranslation());

        final Quaternion rotation = collisionPlane.getLocalRotation();
        rotation.lookAt(camera.getDirection(), Vector3f.UNIT_Y); //equals to angleZ

        collisionPlane.setLocalRotation(rotation);
    }

    @Override
    public void processTransform() {

        final SceneEditorControl editorControl = getEditorControl();
        final InputManager inputManager = EDITOR.getInputManager();
        final Camera camera = EDITOR.getCamera();

        final Transform transformCenter = requireNonNull(editorControl.getTransformCenter());

        // cursor position and selected position vectors
        final Vector2f cursorPos = new Vector2f(inputManager.getCursorPosition());
        final Vector3f vectorScreenSelected = camera.getScreenCoordinates(transformCenter.getTranslation());
        final Vector2f selectedCoords = new Vector2f(vectorScreenSelected.getX(), vectorScreenSelected.getY());

        //set new deltaVector if it's not set
        if (editorControl.getDeltaVector() == null) {
            final Vector2f deltaVecPos = new Vector2f(cursorPos.getX(), cursorPos.getY());
            final Vector2f vecDelta = selectedCoords.subtract(deltaVecPos);
            editorControl.setDeltaVector(new Vector3f(vecDelta.getX(), vecDelta.getY(), 0));
        }


        // Picked vector
        PickedAxis pickedAxis = editorControl.getPickedAxis();
        Vector3f pickedVec = Vector3f.UNIT_X;

        if (pickedAxis == PickedAxis.Y) {
            pickedVec = Vector3f.UNIT_Y;
        } else if (pickedAxis == PickedAxis.Z) {
            pickedVec = Vector3f.UNIT_Z;
        }

        final Vector3f deltaVector = editorControl.getDeltaVector();

        // rotate according to angle
        final Vector2f vec1 = selectedCoords.subtract(cursorPos).normalizeLocal();
        float angle = vec1.angleBetween(new Vector2f(deltaVector.getX(), deltaVector.getY()));
        angle = constraintValue(FastMath.RAD_TO_DEG * angle, getRotateConstraint()) * FastMath.DEG_TO_RAD;

        final Quaternion transformRotation = transformCenter.getRotation();
        final Vector3f axisToRotate = transformRotation.mult(pickedVec);

        float angleCheck = axisToRotate.angleBetween(camera.getDirection());
        if (angleCheck > FastMath.HALF_PI) angle = -angle;

        final Quaternion newRotation = transformRotation.mult(transformRotation.clone().fromAngleAxis(angle, pickedVec));

        final Spatial toTransform = requireNonNull(editorControl.getToTransform());
        toTransform.setLocalRotation(newRotation);

        editorControl.notifyTransformed(toTransform);
    }

    @Override
    protected void controlUpdate(final float tpf) {
    }

    @Override
    protected void controlRender(@NotNull final RenderManager renderManager, @NotNull final ViewPort viewPort) {
    }
}
