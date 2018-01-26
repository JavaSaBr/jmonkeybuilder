package com.ss.editor.ui.control.tree.action.impl.physics.shape;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.plugin.api.dialog.GenericFactoryDialog;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.action.impl.operation.ChangeCollisionShapeOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a new shape.
 *
 * @author JavaSaBr
 */
public abstract class AbstractCreateShapeAction extends AbstractNodeAction<ModelChangeConsumer> {

    AbstractCreateShapeAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.GEOMETRY_16;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        final Array<PropertyDefinition> definitions = getPropertyDefinitions();
        if (definitions.isEmpty()) return;

        final GenericFactoryDialog dialog = new GenericFactoryDialog(definitions, this::handleResult);
        dialog.setTitle(getDialogTitle());
        dialog.show();
    }

    /**
     * Gets a dialog title.
     *
     * @return the dialog title.
     */
    @FxThread
    protected abstract @NotNull String getDialogTitle();

    /**
     * Handle the result from the dialog.
     *
     * @param vars the table with variables.
     */
    @FxThread
    private void handleResult(@NotNull final VarTable vars) {

        final TreeNode<?> treeNode = getNode();
        final PhysicsCollisionObject element = (PhysicsCollisionObject) treeNode.getElement();
        final CollisionShape shape = createShape(vars);
        final CollisionShape currentShape = element.getCollisionShape();

        final NodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new ChangeCollisionShapeOperation(shape, currentShape, element));
    }

    /**
     * Gets a list of property definitions to create a shape.
     *
     * @return the list of definitions.
     */
    @FxThread
    protected abstract @NotNull Array<PropertyDefinition> getPropertyDefinitions();

    /**
     * Create a collision shape.
     *
     * @param vars the table with variables.
     * @return the collision shape
     */
    @FxThread
    protected abstract @NotNull CollisionShape createShape(@NotNull final VarTable vars);
}
