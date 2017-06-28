package com.ss.editor.ui.control.model.tree.action.physics.shape;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.ChangeCollisionShapeOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.dialog.factory.ObjectFactoryDialog;
import com.ss.editor.ui.dialog.factory.PropertyDefinition;
import com.ss.editor.ui.scene.EditorFXScene;
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

    /**
     * Instantiates a new Abstract create shape action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    AbstractCreateShapeAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.GEOMETRY_16;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final Array<PropertyDefinition> definitions = getPropertyDefinitions();
        if (definitions.isEmpty()) return;

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final ObjectFactoryDialog dialog = new ObjectFactoryDialog(definitions, this::handleResult);
        dialog.setTitle(getDialogTitle());
        dialog.show(scene.getWindow());
    }

    /**
     * Gets a dialog title.
     *
     * @return the dialog title.
     */
    @NotNull
    protected abstract String getDialogTitle();

    /**
     * Handle the result from the dialog.
     *
     * @param vars the table with variables.
     */
    private void handleResult(@NotNull final VarTable vars) {

        final ModelNode<?> modelNode = getNode();
        final PhysicsCollisionObject element = (PhysicsCollisionObject) modelNode.getElement();
        final CollisionShape shape = createShape(vars);
        final CollisionShape currentShape = element.getCollisionShape();

        final AbstractNodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new ChangeCollisionShapeOperation(shape, currentShape, element));
    }

    /**
     * Gets a list of property definitions to create a shape.
     *
     * @return the list of definitions.
     */
    @NotNull
    protected abstract Array<PropertyDefinition> getPropertyDefinitions();

    /**
     * Create a collision shape.
     *
     * @param vars the table with variables.
     * @return the collision shape
     */
    @NotNull
    protected abstract CollisionShape createShape(@NotNull final VarTable vars);
}
