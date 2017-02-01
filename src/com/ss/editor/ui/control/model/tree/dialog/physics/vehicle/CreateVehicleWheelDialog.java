package com.ss.editor.ui.control.model.tree.dialog.physics.vehicle;

import static java.util.Objects.requireNonNull;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.model.tree.action.operation.ChangeCollisionShapeOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of a dialog to create a vehicle wheel.
 *
 * @author JavaSaBr
 */
public abstract class CreateVehicleWheelDialog extends AbstractSimpleEditorDialog {

    private static final Insets CONTAINER_OFFSET = new Insets(6, CANCEL_BUTTON_OFFSET.getRight(), 20, 0);


    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);
        VBox.setMargin(root, CONTAINER_OFFSET);
    }

    @Override
    protected boolean isGridStructure() {
        return true;
    }
}
