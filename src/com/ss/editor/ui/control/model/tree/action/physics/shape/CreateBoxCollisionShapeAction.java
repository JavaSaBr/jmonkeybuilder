package com.ss.editor.ui.control.model.tree.action.physics.shape;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.dialog.physics.shape.CreateBoxCollisionShapeDialog;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.scene.EditorFXScene;
import org.jetbrains.annotations.NotNull;

/**
 * The action to create a box collision shape.
 *
 * @author JavaSaBr
 */
public class CreateBoxCollisionShapeAction extends AbstractCreateShapeAction<PhysicsCollisionObject> {

    public CreateBoxCollisionShapeAction(@NotNull final AbstractNodeTree<?> nodeTree,
                                         @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    protected void createShape(@NotNull final PhysicsCollisionObject object, @NotNull final Spatial parentElement,
                               @NotNull final AbstractNodeTree<?> nodeTree) {

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final CreateBoxCollisionShapeDialog dialog = new CreateBoxCollisionShapeDialog(nodeTree, object);
        dialog.show(scene.getWindow());
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_BOX_COLLISION_SHAPE;
    }
}
