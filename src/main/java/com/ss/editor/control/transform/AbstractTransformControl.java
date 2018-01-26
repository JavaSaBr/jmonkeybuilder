package com.ss.editor.control.transform;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.collision.CollisionResult;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.ss.editor.control.transform.EditorTransformSupport.PickedAxis;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import org.jetbrains.annotations.NotNull;

/**
 * The base implementation of transform control.
 *
 * @author JavaSaBr
 */
public abstract class AbstractTransformControl extends AbstractControl implements TransformControl {

    /**
     * The logger.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(MoveToolControl.class);

    /**
     * The scene editor controller.
     */
    @NotNull
    private final EditorTransformSupport editorControl;

    /**
     * The collision plane.
     */
    @NotNull
    private final Node collisionPlane;

    /**
     * The parent node.
     */
    @NotNull
    private final Node parentNode;

    /**
     * The child node.
     */
    @NotNull
    private final Node childNode;

    /**
     * Instantiates a new Rotation tool control.
     *
     * @param editorControl the editor control
     */
    public AbstractTransformControl(@NotNull final EditorTransformSupport editorControl) {
        this.editorControl = editorControl;
        this.collisionPlane = notNull(editorControl.getCollisionPlane());
        this.parentNode = new Node("Parent");
        this.childNode = new Node("Child");
        this.parentNode.attachChild(childNode);
    }

    /**
     * @return the collision plane.
     */
    @NotNull
    protected Node getCollisionPlane() {
        return collisionPlane;
    }

    /**
     * @return the scene editor controller.
     */
    @NotNull
    protected EditorTransformSupport getEditorControl() {
        return editorControl;
    }

    /**
     * @return the parent node.
     */
    @NotNull
    protected Node getParentNode() {
        return parentNode;
    }

    /**
     * @return the child node.
     */
    @NotNull
    protected Node getChildNode() {
        return childNode;
    }

    @Override
    public void setCollisionPlane(@NotNull final CollisionResult collisionResult) {

        final EditorTransformSupport editorControl = getEditorControl();
        final Transform transform = editorControl.getTransformCenter();

        if (transform == null) {
            LOGGER.warning(this, "not found transform center for the " + editorControl);
            return;
        }

        detectPickedAxis(editorControl, collisionResult);

        // set the collision Plane location and rotation
        final Node collisionPlane = getCollisionPlane();
        collisionPlane.setLocalTranslation(transform.getTranslation());
        collisionPlane.setLocalRotation(Quaternion.IDENTITY);
    }

    protected void detectPickedAxis(@NotNull final EditorTransformSupport editorControl,
                                    @NotNull final CollisionResult collisionResult) {

        final Geometry geometry = collisionResult.getGeometry();
        final String geometryName = geometry.getName();

        if (geometryName.contains(getNodeX())) {
            editorControl.setPickedAxis(PickedAxis.X);
        } else if (geometryName.contains(getNodeY())) {
            editorControl.setPickedAxis(PickedAxis.Y);
        } else if (geometryName.contains(getNodeZ())) {
            editorControl.setPickedAxis(PickedAxis.Z);
        }
    }

    @NotNull
    protected String getNodeX() {
        throw new RuntimeException();
    }

    @NotNull
    protected String getNodeY() {
        throw new RuntimeException();
    }

    @NotNull
    protected String getNodeZ() {
        throw new RuntimeException();
    }
}
