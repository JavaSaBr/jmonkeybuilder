package com.ss.editor.ui.control.model.tree.action.physics.shape;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.dialog.physics.shape.CreateBoxCollisionShapeDialog;
import com.ss.editor.ui.control.model.tree.dialog.physics.shape.CreateConeCollisionShapeDialog;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.scene.EditorFXScene;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a cone collision shape.
 *
 * @author JavaSaBr
 */
public class CreateConeCollisionShapeAction extends AbstractCreateShapeAction<PhysicsCollisionObject> {

    public CreateConeCollisionShapeAction(@NotNull final AbstractNodeTree<?> nodeTree,
                                          @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    protected void createShape(@NotNull final PhysicsCollisionObject object, @NotNull final Spatial parentElement,
                               @NotNull final AbstractNodeTree<?> nodeTree) {

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final CreateConeCollisionShapeDialog dialog = new CreateConeCollisionShapeDialog(nodeTree, object);
        dialog.show(scene.getWindow());
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.CONE_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CONE_COLLISION_SHAPE;
    }
}
