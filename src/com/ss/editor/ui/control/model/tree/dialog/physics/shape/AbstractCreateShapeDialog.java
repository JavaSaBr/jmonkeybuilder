package com.ss.editor.ui.control.model.tree.dialog.physics.shape;

import static java.util.Objects.requireNonNull;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.action.operation.ChangeCollisionShapeOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of a dialog to create a collision shape.
 *
 * @author JavaSaBr
 */
public abstract class AbstractCreateShapeDialog extends AbstractSimpleEditorDialog {

    protected static final Insets CONTAINER_OFFSET = new Insets(6, CANCEL_BUTTON_OFFSET.getRight(), 20, 0);

    /**
     * The node tree component.
     */
    @NotNull
    private final AbstractNodeTree<?> nodeTree;

    /**
     * The collision object.
     */
    @NotNull
    private final PhysicsCollisionObject collisionObject;

    public AbstractCreateShapeDialog(@NotNull final AbstractNodeTree<?> nodeTree,
                                     @NotNull final PhysicsCollisionObject collisionObject) {
        this.nodeTree = nodeTree;
        this.collisionObject = collisionObject;
    }

    /**
     * @return the node tree component.
     */
    @NotNull
    protected AbstractNodeTree<?> getNodeTree() {
        return nodeTree;
    }

    /**
     * @return the collision object.
     */
    @NotNull
    public PhysicsCollisionObject getCollisionObject() {
        return collisionObject;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);
        root.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    protected void processOk() {
        super.processOk();

        final AbstractNodeTree<?> nodeTree = getNodeTree();
        final PhysicsCollisionObject object = getCollisionObject();

        final CollisionShape currentShape = object.getCollisionShape();
        final CollisionShape shape = createShape();

        final ChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new ChangeCollisionShapeOperation(shape, currentShape, object));
    }

    /**
     * Create a new collision shape.
     *
     * @return the new collision shape.
     */
    @NotNull
    protected abstract CollisionShape createShape();
}
