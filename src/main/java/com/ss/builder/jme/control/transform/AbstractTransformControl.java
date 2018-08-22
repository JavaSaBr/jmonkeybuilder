package com.ss.builder.control.transform;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.collision.CollisionResult;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.transform.EditorTransformSupport.PickedAxis;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
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

    public AbstractTransformControl(@NotNull final EditorTransformSupport editorControl) {
        this.editorControl = editorControl;
        this.collisionPlane = notNull(editorControl.getCollisionPlane());
        this.parentNode = new Node("Parent");
        this.childNode = new Node("Child");
        this.parentNode.attachChild(childNode);
    }

    /**
     * Get the collision plane.
     *
     * @return the collision plane.
     */
    @JmeThread
    protected @NotNull Node getCollisionPlane() {
        return collisionPlane;
    }

    /**
     * Get the scene editor controller.
     *
     * @return the scene editor controller.
     */
    @JmeThread
    protected @NotNull EditorTransformSupport getEditorControl() {
        return editorControl;
    }

    /**
     * Get the parent node.
     *
     * @return the parent node.
     */
    @JmeThread
    protected @NotNull Node getParentNode() {
        return parentNode;
    }

    /**
     * Get the child node.
     *
     * @return the child node.
     */
    @JmeThread
    protected @NotNull Node getChildNode() {
        return childNode;
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

        detectPickedAxis(editorControl, collisionResult);

        // set the collision Plane location and rotation
        final Node collisionPlane = getCollisionPlane();
        collisionPlane.setLocalTranslation(transform.getTranslation());
        collisionPlane.setLocalRotation(Quaternion.IDENTITY);
    }

    @JmeThread
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

    /**
     * Get the name of node X.
     *
     * @return the name of node X.
     */
    @FromAnyThread
    protected @NotNull String getNodeX() {
        throw new RuntimeException();
    }

    /**
     * Get the name of node Y.
     *
     * @return the name of node Y.
     */
    @FromAnyThread
    protected @NotNull String getNodeY() {
        throw new RuntimeException();
    }

    /**
     * Get the name of node Z.
     *
     * @return the name of node Z.
     */
    @FromAnyThread
    protected @NotNull String getNodeZ() {
        throw new RuntimeException();
    }
}
