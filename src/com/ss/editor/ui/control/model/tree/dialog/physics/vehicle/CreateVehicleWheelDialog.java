package com.ss.editor.ui.control.model.tree.dialog.physics.vehicle;

import static java.util.Objects.requireNonNull;
import com.jme3.bullet.control.VehicleControl;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.model.tree.action.operation.AddVehicleWheelOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.ui.control.input.FloatTextField;
import rlib.ui.util.FXUtils;

import java.awt.*;

/**
 * The implementation of a dialog to create a vehicle wheel.
 *
 * @author JavaSaBr
 */
public class CreateVehicleWheelDialog extends AbstractSimpleEditorDialog {

    private static final Point DIALOG_SIZE = new Point(400, 265);

    /**
     * The node tree.
     */
    @NotNull
    private final AbstractNodeTree<?> nodeTree;

    /**
     * The vehicle control.
     */
    @NotNull
    private final VehicleControl control;

    /**
     * The x value of location.
     */
    @Nullable
    private FloatTextField locationXField;

    /**
     * The y value of location.
     */
    @Nullable
    private FloatTextField locationYField;

    /**
     * The z value of location.
     */
    @Nullable
    private FloatTextField locationZField;

    /**
     * The x value of direction.
     */
    @Nullable
    private FloatTextField directionXField;

    /**
     * The y value of direction.
     */
    @Nullable
    private FloatTextField directionYField;

    /**
     * The z value of direction.
     */
    @Nullable
    private FloatTextField directionZField;

    /**
     * The x value of axle.
     */
    @Nullable
    private FloatTextField axleXField;

    /**
     * The y value of axle.
     */
    @Nullable
    private FloatTextField axleYField;

    /**
     * The z value of axle.
     */
    @Nullable
    private FloatTextField axleZField;

    /**
     * The suspension rest length.
     */
    @Nullable
    private FloatTextField restLengthField;

    /**
     * The wheel radius.
     */
    @Nullable
    private FloatTextField wheelRadiusField;

    /**
     * The flag.
     */
    @Nullable
    private CheckBox frontBox;

    public CreateVehicleWheelDialog(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final VehicleControl control) {
        this.nodeTree = nodeTree;
        this.control = control;
    }

    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label locationLabel = new Label(Messages.CONTROL_PROPERTY_LOCATION + ":");
        locationLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        locationLabel.prefWidthProperty().bind(root.widthProperty().multiply(0.7));

        locationXField = new FloatTextField();
        locationXField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        locationXField.prefWidthProperty().bind(root.widthProperty().multiply(0.3).multiply(0.33));
        locationXField.setValue(1);

        locationYField = new FloatTextField();
        locationYField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        locationYField.prefWidthProperty().bind(root.widthProperty().multiply(0.3).multiply(0.33));
        locationYField.setValue(1);

        locationZField = new FloatTextField();
        locationZField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        locationZField.prefWidthProperty().bind(root.widthProperty().multiply(0.3).multiply(0.33));
        locationZField.setValue(1);

        root.add(locationLabel, 0, 0);
        root.add(locationXField, 1, 0);
        root.add(locationYField, 2, 0);
        root.add(locationZField, 3, 0);

        final Label directionLabel = new Label(Messages.CONTROL_PROPERTY_DIRECTION + ":");
        directionLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        directionLabel.prefWidthProperty().bind(root.widthProperty().multiply(0.6));

        directionXField = new FloatTextField();
        directionXField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        directionXField.setValue(0);

        directionYField = new FloatTextField();
        directionYField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        directionYField.setValue(-1);

        directionZField = new FloatTextField();
        directionZField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        directionZField.setValue(0);

        root.add(directionLabel, 0, 1);
        root.add(directionXField, 1, 1);
        root.add(directionYField, 2, 1);
        root.add(directionZField, 3, 1);

        final Label axleLabel = new Label(Messages.CONTROL_PROPERTY_AXLE + ":");
        axleLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        axleLabel.prefWidthProperty().bind(root.widthProperty().multiply(0.6));

        axleXField = new FloatTextField();
        axleXField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        axleXField.setValue(-1);

        axleYField = new FloatTextField();
        axleYField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        axleYField.setValue(0);

        axleZField = new FloatTextField();
        axleZField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        axleZField.setValue(0);

        root.add(axleLabel, 0, 2);
        root.add(axleXField, 1, 2);
        root.add(axleYField, 2, 2);
        root.add(axleZField, 3, 2);

        final Label suspensionRestLengthLabel = new Label(Messages.CONTROL_PROPERTY_REST_LENGTH + ":");
        suspensionRestLengthLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        suspensionRestLengthLabel.prefWidthProperty().bind(root.widthProperty().multiply(0.6));

        restLengthField = new FloatTextField();
        restLengthField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        restLengthField.setValue(0.2F);

        root.add(suspensionRestLengthLabel, 0, 3);
        root.add(restLengthField, 1, 3, 3, 1);

        final Label wheelRadiusLabel = new Label(Messages.CONTROL_PROPERTY_RADIUS + ":");
        wheelRadiusLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        wheelRadiusLabel.prefWidthProperty().bind(root.widthProperty().multiply(0.6));

        wheelRadiusField = new FloatTextField();
        wheelRadiusField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        wheelRadiusField.setValue(1);

        root.add(wheelRadiusLabel, 0, 4);
        root.add(wheelRadiusField, 1, 4, 3, 1);

        final Label frontLabel = new Label(Messages.CONTROL_PROPERTY_FRONT + ":");
        frontLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        frontLabel.prefWidthProperty().bind(root.widthProperty().multiply(0.6));

        frontBox = new CheckBox();

        root.add(frontLabel, 0, 5);
        root.add(frontBox, 1, 5, 3, 1);

        FXUtils.addClassTo(locationLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(locationXField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(locationYField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(locationZField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(locationYField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(locationZField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(directionLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(directionXField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(directionYField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(directionZField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(axleLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(axleXField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(axleYField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(axleZField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(suspensionRestLengthLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(restLengthField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(wheelRadiusLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(wheelRadiusField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(frontLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(frontBox, CSSClasses.SPECIAL_FONT_14);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.ADD_VEHICLE_WHEEL_DIALOG_TITLE;
    }

    @Override
    protected boolean isGridStructure() {
        return true;
    }

    /**
     * @return the node tree.
     */
    @NotNull
    public AbstractNodeTree<?> getNodeTree() {
        return nodeTree;
    }

    /**
     * @return the x value of location.
     */
    @NotNull
    private FloatTextField getLocationXField() {
        return requireNonNull(locationXField);
    }

    /**
     * @return the y value of location.
     */
    @NotNull
    private FloatTextField getLocationYField() {
        return requireNonNull(locationYField);
    }

    /**
     * @return the z value of location.
     */
    @NotNull
    private FloatTextField getLocationZField() {
        return requireNonNull(locationZField);
    }

    /**
     * @return the x value of direction.
     */
    @NotNull
    private FloatTextField getDirectionXField() {
        return requireNonNull(directionXField);
    }

    /**
     * @return the y value of direction.
     */
    @NotNull
    private FloatTextField getDirectionYField() {
        return requireNonNull(directionYField);
    }

    /**
     * @return the z value of direction.
     */
    @NotNull
    private FloatTextField getDirectionZField() {
        return requireNonNull(directionZField);
    }

    /**
     * @return the x value of axle.
     */
    @NotNull
    private FloatTextField getAxleXField() {
        return requireNonNull(axleXField);
    }

    /**
     * @return the y value of axle.
     */
    @NotNull
    private FloatTextField getAxleYField() {
        return requireNonNull(axleYField);
    }

    /**
     * @return the z value of axle.
     */
    @NotNull
    private FloatTextField getAxleZField() {
        return requireNonNull(axleZField);
    }

    /**
     * @return the suspension rest length.
     */
    @NotNull
    private FloatTextField getRestLengthField() {
        return requireNonNull(restLengthField);
    }

    /**
     * @return the suspension rest length.
     */
    @NotNull
    private FloatTextField getWheelRadiusField() {
        return requireNonNull(wheelRadiusField);
    }

    /**
     * @return the suspension rest length.
     */
    @NotNull
    private CheckBox getFrontBox() {
        return requireNonNull(frontBox);
    }

    @Override
    protected void processOk() {
        super.processOk();

        final FloatTextField locationXField = getLocationXField();
        final FloatTextField locationYField = getLocationYField();
        final FloatTextField locationZField = getLocationZField();

        final Vector3f location = new Vector3f(locationXField.getValue(), locationYField.getValue(),
                locationZField.getValue());

        final FloatTextField directionXField = getDirectionXField();
        final FloatTextField directionYField = getDirectionYField();
        final FloatTextField directionZField = getDirectionZField();

        final Vector3f direction = new Vector3f(directionXField.getValue(), directionYField.getValue(),
                directionZField.getValue());

        final FloatTextField axleXField = getAxleXField();
        final FloatTextField axleYField = getAxleYField();
        final FloatTextField axleZField = getAxleZField();

        final Vector3f axle = new Vector3f(axleXField.getValue(), axleYField.getValue(), axleZField.getValue());

        final FloatTextField restLengthField = getRestLengthField();
        final FloatTextField wheelRadiusField = getWheelRadiusField();
        final CheckBox frontBox = getFrontBox();

        final AbstractNodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());

        changeConsumer.execute(new AddVehicleWheelOperation(control, location, direction, axle,
                restLengthField.getValue(), wheelRadiusField.getValue(), frontBox.isSelected()));
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
