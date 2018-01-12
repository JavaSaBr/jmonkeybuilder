package com.ss.editor.control.transform;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.input.InputManager;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.JmeApplication;
import com.ss.editor.control.transform.EditorTransformSupport.TransformationMode;
import com.ss.editor.util.LocalObjects;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the rotating control.
 *
 * @author JavaSaBr
 */
public class RotationToolControl extends AbstractTransformControl {

    @NotNull
    private static final String NODE_ROTATION_X = "rot_x";

    @NotNull
    private static final String NODE_ROTATION_Y = "rot_y";

    @NotNull
    private static final String NODE_ROTATION_Z = "rot_z";

    @NotNull
    private static final JmeApplication JME_APPLICATION = JmeApplication.getInstance();

    /**
     * Instantiates a new Rotation tool control.
     *
     * @param editorControl the editor control
     */
    public RotationToolControl(@NotNull final EditorTransformSupport editorControl) {
        super(editorControl);
    }

    @NotNull
    @Override
    protected String getNodeX() {
        return NODE_ROTATION_X;
    }

    @NotNull
    @Override
    protected String getNodeY() {
        return NODE_ROTATION_Y;
    }

    @NotNull
    @Override
    protected String getNodeZ() {
        return NODE_ROTATION_Z;
    }

    @Override
    public void processTransform() {

        final LocalObjects local = LocalObjects.get();
        final EditorTransformSupport editorControl = getEditorControl();
        final InputManager inputManager = JME_APPLICATION.getInputManager();
        final Camera camera = editorControl.getCamera();

        final Transform transform = notNull(editorControl.getTransformCenter());

        // cursor position and selected position vectors
        final Vector2f cursorPos = inputManager.getCursorPosition();
        final Vector3f transformOnScreen = camera.getScreenCoordinates(transform.getTranslation());
        final Vector2f selectedCoords = local.nextVector(transformOnScreen.getX(), transformOnScreen.getY());

        //set new deltaVector if it's not set
        if (Float.isNaN(editorControl.getTransformDeltaX())) {
            editorControl.setTransformDeltaX(selectedCoords.getX() - cursorPos.getX());
            editorControl.setTransformDeltaY(selectedCoords.getY() - cursorPos.getY());
        }

        // Picked vector
        final TransformationMode transformationMode = editorControl.getTransformationMode();
        final Vector3f pickedVector = transformationMode.getPickedVector(transform, editorControl.getPickedAxis(), camera);
        final Vector3f deltaVector = local.nextVector(editorControl.getTransformDeltaX(), editorControl.getTransformDeltaY(), 0F);

        // rotate according to angle
        final Vector2f cursorDirection = selectedCoords.subtractLocal(cursorPos).normalizeLocal();
        float angle = cursorDirection.angleBetween(local.nextVector(deltaVector.getX(), deltaVector.getY()));
        angle = FastMath.RAD_TO_DEG * angle * FastMath.DEG_TO_RAD;

        final Node parentNode = getParentNode();
        final Node childNode = getChildNode();

        transformationMode.prepareToRotate(parentNode, childNode, transform, camera);

        final Quaternion rotation = parentNode.getLocalRotation();
        final Vector3f axisToRotate = rotation.mult(pickedVector, local.nextVector());

        float angleCheck = axisToRotate.angleBetween(camera.getDirection(local.nextVector()));

        if (angleCheck > FastMath.HALF_PI) {
            angle = -angle;
        }

        final Quaternion difference = local.nextRotation().fromAngleAxis(angle, pickedVector);
        final Quaternion newRotation = rotation.mult(difference, local.nextRotation());

        parentNode.setLocalRotation(newRotation);

        final Spatial toTransform = notNull(editorControl.getToTransform());
        toTransform.setLocalRotation(childNode.getWorldRotation());

        editorControl.notifyTransformed(toTransform);
    }

    @Override
    protected void controlUpdate(final float tpf) {
    }

    @Override
    protected void controlRender(@NotNull final RenderManager renderManager, @NotNull final ViewPort viewPort) {
    }
}
