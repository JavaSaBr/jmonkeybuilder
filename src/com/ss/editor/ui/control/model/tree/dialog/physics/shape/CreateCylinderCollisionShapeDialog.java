package com.ss.editor.ui.control.model.tree.dialog.physics.shape;

import static java.util.Objects.requireNonNull;
import static javafx.collections.FXCollections.observableArrayList;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;

import java.awt.*;

/**
 * The dialog to create cylinder collision shape.
 *
 * @author JavaSaBr
 */
public class CreateCylinderCollisionShapeDialog extends CreateBoxCollisionShapeDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(400, 211);

    @NotNull
    private static final ObservableList<String> AXIS = observableArrayList("x", "y", "z");

    /**
     * The axis box.
     */
    @Nullable
    private ComboBox<String> axisBox;

    public CreateCylinderCollisionShapeDialog(@NotNull final AbstractNodeTree<?> nodeTree,
                                              @NotNull final PhysicsCollisionObject collisionObject) {
        super(nodeTree, collisionObject);
    }

    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label axisLabel = new Label(Messages.CONTROL_PROPERTY_AXIS + ":");
        axisLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        axisLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        axisBox = new ComboBox<>(AXIS);
        axisBox.setId(CSSIds.EDITOR_DIALOG_FIELD);
        axisBox.getSelectionModel().select(0);
        axisBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FXUtils.addClassTo(axisLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(axisBox, CSSClasses.SPECIAL_FONT_14);

        root.add(axisLabel, 0, 3);
        root.add(axisBox, 1, 3);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.CREATE_CYLINDER_COLLISION_SHAPE_DIALOG_TITLE;
    }

    /**
     * @return the axis box.
     */
    @NotNull
    private ComboBox<String> getAxisBox() {
        return requireNonNull(axisBox);
    }

    @NotNull
    @Override
    protected CollisionShape createShape() {

        final FloatTextField xField = getXField();
        final FloatTextField yField = getYField();
        final FloatTextField zField = getZField();

        final ComboBox<String> axisBox = getAxisBox();
        final SingleSelectionModel<String> selectionModel = axisBox.getSelectionModel();
        final int selectedIndex = selectionModel.getSelectedIndex();

        final Vector3f halfExtents = new Vector3f(xField.getValue(), yField.getValue(), zField.getValue());

        return new CylinderCollisionShape(halfExtents, selectedIndex);
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
