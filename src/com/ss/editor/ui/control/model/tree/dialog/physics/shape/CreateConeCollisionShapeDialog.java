package com.ss.editor.ui.control.model.tree.dialog.physics.shape;

import static java.util.Objects.requireNonNull;
import static javafx.collections.FXCollections.observableArrayList;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
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
 * The dialog to create cone collision shape.
 *
 * @author JavaSaBr
 */
public class CreateConeCollisionShapeDialog extends CreateCapsuleCollisionShapeDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(400, 184);

    @NotNull
    private static final ObservableList<String> AXIS = observableArrayList("x", "y", "z");

    /**
     * The axis box.
     */
    @Nullable
    private ComboBox<String> axisBox;

    /**
     * Instantiates a new Create cone collision shape dialog.
     *
     * @param nodeTree        the node tree
     * @param collisionObject the collision object
     */
    public CreateConeCollisionShapeDialog(@NotNull final AbstractNodeTree<?> nodeTree,
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

        root.add(axisLabel, 0, 2);
        root.add(axisBox, 1, 2);

        FXUtils.addClassTo(axisLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(axisBox, CSSClasses.SPECIAL_FONT_14);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.CREATE_CONE_COLLISION_SHAPE_DIALOG_TITLE;
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

        final FloatTextField heightField = getHeightField();
        final FloatTextField radiusField = getRadiusField();

        final ComboBox<String> axisBox = getAxisBox();
        final SingleSelectionModel<String> selectionModel = axisBox.getSelectionModel();
        final int selectedIndex = selectionModel.getSelectedIndex();

        return new ConeCollisionShape(radiusField.getValue(), heightField.getValue(), selectedIndex);
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
