package com.ss.editor.ui.control.model.property;

import static java.lang.Float.parseFloat;
import static rlib.geom.util.AngleUtils.degreeToRadians;
import static rlib.geom.util.AngleUtils.radiansToDegree;

import com.jme3.math.Quaternion;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link ModelPropertyControl} for editing {@link Quaternion} values.
 *
 * @author JavaSaBr
 */
public class QuaternionModelPropertyControl extends ModelPropertyControl<Spatial, Quaternion> {

    /**
     * The field Y.
     */
    private TextField xField;

    /**
     * The field X.
     */
    private TextField yFiled;

    /**
     * The field Z.
     */
    private TextField zField;

    public QuaternionModelPropertyControl(final Quaternion element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        final Label xLabel = new Label("x:");
        xLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        xField = new TextField();
        xField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        xField.setOnScroll(this::processScroll);
        xField.setOnKeyReleased(this::updateRotation);
        xField.prefWidthProperty().bind(widthProperty().divide(3));

        final Label yLabel = new Label("y:");
        yLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        yFiled = new TextField();
        yFiled.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        yFiled.setOnScroll(this::processScroll);
        yFiled.setOnKeyReleased(this::updateRotation);
        yFiled.prefWidthProperty().bind(widthProperty().divide(3));

        final Label zLabel = new Label("z:");
        zLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        zField = new TextField();
        zField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR3F_FIELD);
        zField.setOnScroll(this::processScroll);
        zField.setOnKeyReleased(this::updateRotation);
        zField.prefWidthProperty().bind(widthProperty().divide(3));

        FXUtils.addToPane(xLabel, container);
        FXUtils.addToPane(xField, container);
        FXUtils.addToPane(yLabel, container);
        FXUtils.addToPane(yFiled, container);
        FXUtils.addToPane(zLabel, container);
        FXUtils.addToPane(zField, container);

        FXUtils.addClassTo(xLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(xField, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(yLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(yFiled, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(zLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(zField, CSSClasses.SPECIAL_FONT_13);
    }

    /**
     * The process of scrolling.
     */
    private void processScroll(final ScrollEvent event) {
        if (!event.isControlDown()) return;

        final TextField source = (TextField) event.getSource();
        final String text = source.getText();

        float value;
        try {
            value = parseFloat(text);
        } catch (final NumberFormatException e) {
            return;
        }

        long longValue = (long) (value * 1000);
        longValue += event.getDeltaY() * 50;

        final String result = String.valueOf(longValue / 1000F);
        source.setText(result);
        source.positionCaret(result.length());

        updateRotation(null);
    }

    /**
     * @return the field X.
     */
    private TextField getXField() {
        return xField;
    }

    /**
     * @return the field Y.
     */
    private TextField getYFiled() {
        return yFiled;
    }

    /**
     * @return the field Z.
     */
    private TextField getZField() {
        return zField;
    }

    @Override
    protected void reload() {

        final float[] angles = new float[3];

        final Quaternion element = getPropertyValue();
        Objects.requireNonNull(element, "The property value can't be null.");

        element.toAngles(angles);

        final TextField xField = getXField();
        xField.setText(String.valueOf(radiansToDegree(angles[0])));
        xField.positionCaret(xField.getText().length());

        final TextField yFiled = getYFiled();
        yFiled.setText(String.valueOf(radiansToDegree(angles[1])));
        yFiled.positionCaret(yFiled.getText().length());

        final TextField zField = getZField();
        zField.setText(String.valueOf(radiansToDegree(angles[2])));
        zField.positionCaret(zField.getText().length());
    }

    /**
     * Updating rotation.
     */
    private void updateRotation(final KeyEvent event) {
        if (isIgnoreListener() || (event != null && event.getCode() != KeyCode.ENTER)) return;

        final Quaternion oldValue = getPropertyValue();
        Objects.requireNonNull(oldValue, "The old value can't be null.");

        final TextField xField = getXField();

        float x;
        try {
            x = degreeToRadians(parseFloat(xField.getText()));
        } catch (final NumberFormatException e) {
            return;
        }

        final TextField yFiled = getYFiled();

        float y;
        try {
            y = degreeToRadians(parseFloat(yFiled.getText()));
        } catch (final NumberFormatException e) {
            return;
        }

        final TextField zField = getZField();

        float z;
        try {
            z = degreeToRadians(parseFloat(zField.getText()));
        } catch (final NumberFormatException e) {
            return;
        }

        final Quaternion newValue = new Quaternion();
        newValue.fromAngles(ArrayFactory.toFloatArray(x, y, z));

        changed(newValue, oldValue.clone());
    }
}
