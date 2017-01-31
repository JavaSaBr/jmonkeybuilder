package com.ss.editor.ui.control.model.tree.dialog.physics.shape;

import static java.util.Objects.requireNonNull;
import static javafx.collections.FXCollections.observableArrayList;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.ui.control.input.FloatTextField;
import rlib.ui.util.FXUtils;

import java.awt.*;

/**
 * The dialog to create cone collision shape.
 *
 * @author JavaSaBr
 */
public class CreateConeCollisionShapeDialog extends AbstractCreateShapeDialog {

    private static final Point DIALOG_SIZE = new Point(400, 174);

    /**
     * The radius field.
     */
    @Nullable
    private FloatTextField radiusField;

    /**
     * The height field.
     */
    @Nullable
    private FloatTextField heightField;

    /**
     * The axis box.
     */
    @Nullable
    private ComboBox<String> axisBox;

    public CreateConeCollisionShapeDialog(@NotNull final AbstractNodeTree<?> nodeTree,
                                          @NotNull final PhysicsCollisionObject collisionObject) {
        super(nodeTree, collisionObject);
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        final Label radiusLabel = new Label("Radius:");
        radiusLabel.setId(CSSIds.EDITOR_DIALOG_SHORT_LABEL);

        radiusField = new FloatTextField();
        radiusField.prefWidthProperty().bind(widthProperty());
        radiusField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        radiusField.setMinMax(0, Integer.MAX_VALUE);
        radiusField.setValue(1);

        final Label heightLabel = new Label("Height:");
        heightLabel.setId(CSSIds.EDITOR_DIALOG_SHORT_LABEL);

        heightField = new FloatTextField();
        heightField.prefWidthProperty().bind(widthProperty());
        heightField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        heightField.setMinMax(0, Integer.MAX_VALUE);
        heightField.setValue(1);

        final Label axisLabel = new Label("Axis:");
        axisLabel.setId(CSSIds.EDITOR_DIALOG_SHORT_LABEL);

        axisBox = new ComboBox<>(observableArrayList("X", "Y", "Z"));
        axisBox.prefWidthProperty().bind(widthProperty());
        axisBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        axisBox.getSelectionModel().select(0);

        final GridPane container = new GridPane();
        container.add(radiusLabel, 0, 0);
        container.add(radiusField, 1, 0);
        container.add(heightLabel, 0, 1);
        container.add(heightField, 1, 1);
        container.add(axisLabel, 0, 2);
        container.add(axisBox, 1, 2);

        FXUtils.addClassTo(radiusLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(heightLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(axisLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(radiusField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(heightField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(axisBox, CSSClasses.SPECIAL_FONT_14);

        FXUtils.addToPane(container, root);

        VBox.setMargin(container, CONTAINER_OFFSET);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return super.getTitleText();
    }

    /**
     * @return the radius size.
     */
    @NotNull
    private FloatTextField getRadiusField() {
        return requireNonNull(radiusField);
    }

    /**
     * @return the height size.
     */
    @NotNull
    private FloatTextField getHeightField() {
        return requireNonNull(heightField);
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
