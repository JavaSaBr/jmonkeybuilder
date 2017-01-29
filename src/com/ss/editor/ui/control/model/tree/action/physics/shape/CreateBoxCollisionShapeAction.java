package com.ss.editor.ui.control.model.tree.action.physics.shape;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.scene.Spatial;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
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

    }

    @NotNull
    @Override
    protected String getName() {
        return "Box shape";
    }
}
