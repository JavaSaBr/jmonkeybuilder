package com.ss.builder.jme.control.transform;

import static com.ss.builder.util.GeomUtils.*;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.math.*;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.config.Config;
import com.ss.builder.jme.control.transform.EditorTransformSupport.PickedAxis;
import com.ss.builder.jme.control.transform.EditorTransformSupport.TransformationMode;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.util.LocalObjects;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the moving control.
 *
 * @author JavaSaBr
 */
public class MoveToolControl extends AbstractTransformControl {

    @NotNull
    private static final String NODE_MOVE_X = "move_x";

    @NotNull
    private static final String NODE_MOVE_Y = "move_y";

    @NotNull
    private static final String NODE_MOVE_Z = "move_z";

    public MoveToolControl(@NotNull final EditorTransformSupport editorControl) {
        super(editorControl);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getNodeX() {
        return NODE_MOVE_X;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getNodeY() {
        return NODE_MOVE_Y;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getNodeZ() {
        return NODE_MOVE_Z;
    }

    @Override
    @JmeThread
    public void setCollisionPlane(@NotNull final CollisionResult collisionResult) {

        final EditorTransformSupport editorControl = getEditorControl();
        final Transform transform = editorControl.getTransformCenter();

        if (transform == null) {
            LOGGER.warning(this, "not found transform center for the " + editorControl);
            return;
        }

        final LocalObjects local = LocalObjects.get();
        final Camera camera = editorControl.getCamera();
        final Vector3f direction = camera.getDirection(local.nextVector());

        detectPickedAxis(editorControl, collisionResult);

        final Node parentNode = getParentNode();
        final Node childNode = getChildNode();

        final PickedAxis pickedAxis = editorControl.getPickedAxis();
        final TransformationMode transformationMode = editorControl.getTransformationMode();
        transformationMode.prepareToMove(parentNode, childNode, transform, camera);

        final Quaternion rotation = parentNode.getLocalRotation();

        // select an angle between 0 and 90 degrees (from 0 to 1.57 in radians) (for collisionPlane)
        final Vector3f angleVectorX = rotation.mult(Vector3f.UNIT_X, local.nextVector());
        final Vector3f angleVectorY = rotation.mult(Vector3f.UNIT_Y, local.nextVector());
        final Vector3f angleVectorZ = rotation.mult(Vector3f.UNIT_Z, local.nextVector());

        float angleX = direction.angleBetween(angleVectorX);
        float angleY = direction.angleBetween(angleVectorY);
        float angleZ = direction.angleBetween(angleVectorZ);

        if (angleX > 1.57) {
            angleX = direction.angleBetween(angleVectorX.negateLocal());
        }

        if (angleY > 1.57) {
            angleY = direction.angleBetween(angleVectorY.negateLocal());
        }

        if (angleZ > 1.57) {
            angleZ = direction.angleBetween(angleVectorZ.negateLocal());
        }

        // select the less angle for collisionPlane
        float lessAngle = angleX;

        if (lessAngle > angleY) lessAngle = angleY;
        if (lessAngle > angleZ) lessAngle = angleZ;

        // set the collision Plane location and rotation
        final Node collisionPlane = getCollisionPlane();
        collisionPlane.setLocalTranslation(parentNode.getLocalTranslation());
        collisionPlane.setLocalRotation(rotation); //equals to angleZ

        final Quaternion planeRotation = collisionPlane.getLocalRotation();
        final Quaternion tempRotation = local.nextRotation();

        // rotate the plane for constraints
        if (lessAngle == angleX) {

            if (pickedAxis == PickedAxis.X && angleY > angleZ) {
                planeRotation.multLocal(tempRotation.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            } else if (pickedAxis == PickedAxis.X && angleY < angleZ) {
                planeRotation.multLocal(tempRotation.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
                planeRotation.multLocal(tempRotation.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            } else if (pickedAxis == PickedAxis.Y) {
                planeRotation.multLocal(tempRotation.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
            } else if (pickedAxis == PickedAxis.Z) {
                planeRotation.multLocal(tempRotation.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
                planeRotation.multLocal(tempRotation.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            }

        } else if (lessAngle == angleY) {

            if (pickedAxis == PickedAxis.X) {
                planeRotation.multLocal(tempRotation.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
                planeRotation.multLocal(tempRotation.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            } else if (pickedAxis == PickedAxis.Y && angleX < angleZ) {
                planeRotation.multLocal(tempRotation.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
            } else if (pickedAxis == PickedAxis.Z) {
                planeRotation.multLocal(tempRotation.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
            }

        } else if (lessAngle == angleZ) {

            if (pickedAxis == PickedAxis.X) {
                planeRotation.multLocal(tempRotation.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            } else if (pickedAxis == PickedAxis.Z && angleY < angleX) {
                planeRotation.multLocal(tempRotation.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
            } else if (pickedAxis == PickedAxis.Z && angleY > angleX) {
                planeRotation.multLocal(tempRotation.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Y));
                planeRotation.multLocal(tempRotation.fromAngleAxis(FastMath.HALF_PI, Vector3f.UNIT_Z));
            }
        }
    }

    @Override
    @JmeThread
    public void processTransform() {

        final EditorTransformSupport editorControl = getEditorControl();
        final LocalObjects local = LocalObjects.get();

        final Camera camera = editorControl.getCamera();
        final InputManager inputManager = EditorUtils.getInputManager();
        final Vector2f cursorPosition = inputManager.getCursorPosition();
        final CollisionResults results = local.nextCollisionResults();

        final Vector3f position = camera.getWorldCoordinates(cursorPosition, 0f, local.nextVector());
        final Vector3f direction = camera.getWorldCoordinates(cursorPosition, 1f, local.nextVector())
                .subtractLocal(position)
                .normalizeLocal();

        final Ray ray = local.nextRay();
        ray.setOrigin(position);
        ray.setDirection(direction);

        final Node collisionPlane = getCollisionPlane();
        collisionPlane.collideWith(ray, results);

        final CollisionResult result = results.getClosestCollision();
        final Transform transform = editorControl.getTransformCenter();

        // Complex trigonometry formula based on sin(angle)*distance
        if (result == null || transform == null) {
            return;
        }

        final Node parentNode = getParentNode();

        final Vector3f translation = parentNode.getLocalTranslation();
        final Vector3f contactPoint = result.getContactPoint(); // get a point of collisionPlane

        //set new deltaVector if it's not set
        if (Float.isNaN(editorControl.getTransformDeltaX())) {
            editorControl.setTransformDeltaX(translation.getX() - contactPoint.getX());
            editorControl.setTransformDeltaY(translation.getY() - contactPoint.getY());
            editorControl.setTransformDeltaZ(translation.getZ() - contactPoint.getZ());
        }

        // add delta of the picked place
        contactPoint.addLocal(editorControl.getTransformDeltaX(), editorControl.getTransformDeltaY(),
                editorControl.getTransformDeltaZ());

        final Vector3f difference = contactPoint.subtract(translation, local.nextVector());
        float distanceToContactPoint = translation.distance(contactPoint);

        // Picked vector
        final PickedAxis pickedAxis = editorControl.getPickedAxis();
        final TransformationMode transformationMode = editorControl.getTransformationMode();
        final Vector3f pickedVector = transformationMode.getPickedVector(transform, pickedAxis, camera);
        final Quaternion rotation = parentNode.getLocalRotation();

        // the main formula for constraint axis
        final Vector3f normalizedDifference = local.nextVector(difference).normalizeLocal();

        float angle = normalizedDifference.angleBetween(rotation.mult(pickedVector, local.nextVector())
                .normalizeLocal());

        float distanceVec2 = distanceToContactPoint * FastMath.sin(angle);

        // fix if angle>90 degrees
        Vector3f perpendicularVec = collisionPlane.getLocalRotation()
                .mult(Vector3f.UNIT_X, local.nextVector())
                .multLocal(distanceVec2);

        Vector3f checkVec = contactPoint.add(perpendicularVec, local.nextVector())
                .subtractLocal(contactPoint)
                .normalizeLocal();

        float angleCheck = checkVec.angleBetween(normalizedDifference);

        if (angleCheck < FastMath.HALF_PI) {
            perpendicularVec.negateLocal();
        }

        // find distance to move
        float distanceToMove = contactPoint.addLocal(perpendicularVec).distance(translation);

        // invert value if it's needed for negative movement
        if (angle > FastMath.HALF_PI) {
            distanceToMove = -distanceToMove;
        }

        translateObjects(pickedAxis, notNull(editorControl.getToTransform()), transform, distanceToMove);
    }

    /**
     * Process of moving objects.
     *
     * @param pickedAxis  the moving axis.
     * @param toTransform the moving object.
     * @param transform   the transform's center.
     * @param distance    the moving distance.
     */
    private void translateObjects(@NotNull final PickedAxis pickedAxis, @NotNull final Spatial toTransform,
                                  @NotNull final Transform transform, final float distance) {

        final Node parentNode = getParentNode();
        final Node childNode = getChildNode();

        final EditorTransformSupport editorControl = getEditorControl();
        final Quaternion rotation = parentNode.getLocalRotation();

        final LocalObjects local = LocalObjects.get();
        final Vector3f currentLocation = local.nextVector(parentNode.getLocalTranslation());

        if (Config.DEV_TRANSFORMS_DEBUG) {
            System.out.println("distance " + distance);
        }

        if (pickedAxis == PickedAxis.X) {
            currentLocation.addLocal(getLeft(rotation, local.nextVector()).multLocal(distance));
        } else if (pickedAxis == PickedAxis.Y) {
            currentLocation.addLocal(getUp(rotation, local.nextVector()).multLocal(distance));
        } else if (pickedAxis == PickedAxis.Z) {
            currentLocation.addLocal(getDirection(rotation, local.nextVector()).multLocal(distance));
        }

        parentNode.setLocalTranslation(currentLocation);

        toTransform.setLocalTranslation(childNode.getWorldTranslation());
        editorControl.notifyTransformed(toTransform);
    }

    @Override
    protected void controlUpdate(final float tpf) {
    }

    @Override
    protected void controlRender(@NotNull final RenderManager renderManager, @NotNull final ViewPort viewPort) {
    }
}
