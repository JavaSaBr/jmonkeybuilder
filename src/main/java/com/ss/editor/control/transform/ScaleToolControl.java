package com.ss.editor.control.transform;

import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.util.Objects.requireNonNull;

import com.jme3.collision.CollisionResult;
import com.jme3.input.InputManager;
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
 * The implementation of the scaling control.
 *
 * @author JavaSaBr
 */
public class ScaleToolControl extends AbstractControl implements TransformControl {

    /**
     * The constant LOGGER.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(MoveToolControl.class);

    private static final String NODE_SCALE_X = "scale_x";
    private static final String NODE_SCALE_Y = "scale_y";
    private static final String NODE_SCALE_Z = "scale_z";

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
     * Instantiates a new Scale tool control.
     *
     * @param editorControl the editor control
     */
    public ScaleToolControl(@NotNull final SceneEditorControl editorControl) {
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
        final Transform transformCenter = editorControl.getTransformCenter();

        if (transformCenter == null) {
            LOGGER.warning(this, "not found transform center for the " + editorControl);
            return;
        }

        // Set PickedAxis
        final Geometry geometry = colResult.getGeometry();
        final String geometryName = geometry.getName();

        if (geometryName.contains(NODE_SCALE_X)) {
            editorControl.setPickedAxis(PickedAxis.X);
        } else if (geometryName.contains(NODE_SCALE_Y)) {
            editorControl.setPickedAxis(PickedAxis.Y);
        } else if (geometryName.contains(NODE_SCALE_Z)) {
            editorControl.setPickedAxis(PickedAxis.Z);
        }

        // set the collision Plane location and rotation
        final Node collisionPlane = getCollisionPlane();
        collisionPlane.setLocalTranslation(transformCenter.getTranslation());

        final Quaternion rotation = collisionPlane.getLocalRotation();
        rotation.lookAt(camera.getDirection(), Vector3f.UNIT_Y); //equals to angleZ

        collisionPlane.setLocalRotation(rotation);
    }

    @Override
    public void processTransform() {

        final SceneEditorControl editorControl = getEditorControl();

        final Camera camera = EDITOR.getCamera();
        final InputManager inputManager = EDITOR.getInputManager();
        final Transform transformCenter = requireNonNull(editorControl.getTransformCenter());

        // cursor position and selected position vectors
        final Vector2f cursorPos = new Vector2f(inputManager.getCursorPosition());
        final Vector3f vectorScreenSelected = camera.getScreenCoordinates(transformCenter.getTranslation());
        final Vector2f selectedCoords = new Vector2f(vectorScreenSelected.getX(), vectorScreenSelected.getY());

        //set new deltaVector if it's not set (scale tool stores position of a cursor)
        if (editorControl.getDeltaVector() == null) {
            final Vector2f deltaVecPos = new Vector2f(cursorPos.getX(), cursorPos.getY());
            editorControl.setDeltaVector(new Vector3f(deltaVecPos.getX(), deltaVecPos.getY(), 0));
        }

        // Picked vector
        PickedAxis pickedAxis = editorControl.getPickedAxis();
        Vector3f pickedVec = Vector3f.UNIT_X;

        if (pickedAxis == PickedAxis.Y) {
            pickedVec = Vector3f.UNIT_Y;
        } else if (pickedAxis == PickedAxis.Z) {
            pickedVec = Vector3f.UNIT_Z;
        }

        // scale according to distance
        final Vector3f deltaVector = editorControl.getDeltaVector();
        final Vector2f delta2d = new Vector2f(deltaVector.getX(), deltaVector.getY());
        final Vector3f baseScale = transformCenter.getScale().clone(); // default scale

        // scale object
        float disCursor = cursorPos.distance(selectedCoords);
        float disDelta = delta2d.distance(selectedCoords);
        float scaleValue = cursorPos.distance(delta2d);
        scaleValue = TransformConstraint.constraintValue(scaleValue * 0.007f, TransformConstraint.getScaleConstraint());

        Vector3f scaleVector = null;

        if (disCursor > disDelta) {
            scaleVector = baseScale.add(pickedVec.mult(scaleValue));
        } else {
            scaleValue = Math.min(scaleValue, 0.999f); // remove negateve values
            scaleVector = baseScale.subtract(pickedVec.mult((scaleValue)));
        }

        final Spatial toTransform = requireNonNull(editorControl.getToTransform());
        toTransform.setLocalScale(scaleVector);

        editorControl.notifyTransformed(toTransform);
    }

    @Override
    protected void controlUpdate(final float tpf) {
    }

    @Override
    protected void controlRender(@NotNull final RenderManager renderManager, @NotNull final ViewPort viewPort) {
    }
}
