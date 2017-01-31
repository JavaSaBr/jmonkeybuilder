package com.ss.editor.ui.control.model.tree.action.physics.shape;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.scene.Spatial;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.dialog.physics.shape.CreateBoxCollisionShapeDialog;
import com.ss.editor.ui.control.model.tree.dialog.physics.shape.CreateCylinderCollisionShapeDialog;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.scene.EditorFXScene;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a cylinder collision shape.
 *
 * @author JavaSaBr
 */
public class CreateCylinderCollisionShapeAction extends AbstractCreateShapeAction<PhysicsCollisionObject> {

    public CreateCylinderCollisionShapeAction(@NotNull final AbstractNodeTree<?> nodeTree,
                                              @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    protected void createShape(@NotNull final PhysicsCollisionObject object, @NotNull final Spatial parentElement,
                               @NotNull final AbstractNodeTree<?> nodeTree) {

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final CreateCylinderCollisionShapeDialog dialog = new CreateCylinderCollisionShapeDialog(nodeTree, object);
        dialog.show(scene.getWindow());
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.CYLINDER_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return "Cylinder shape";
    }
}
