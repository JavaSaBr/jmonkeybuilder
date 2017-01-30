package com.ss.editor.ui.control.model.tree.dialog.physics.shape;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.action.physics.shape.AbstractCreateShapeAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

/**
 * Created by ronn on 30.01.17.
 */
public class CreateBoxCollisionShapeDialog extends AbstractCreateShapeDialog {

    public CreateBoxCollisionShapeDialog(@NotNull final AbstractNodeTree<ModelChangeConsumer> nodeTree,
                                         @NotNull final PhysicsCollisionObject collisionObject) {
        super(nodeTree, collisionObject);
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);
    }

    @Override
    protected void processOk() {
        super.processOk();
    }
}
