package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.common.geom.util.AngleUtils.degreeToRadians;
import static com.ss.rlib.common.geom.util.AngleUtils.radiansToDegree;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.math.Quaternion;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.fx.control.input.FloatTextField;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import com.ss.rlib.fx.util.ObservableUtils;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit {@link Quaternion} values.
 *
 * @param <C> the type of a change consumer.
 * @param <D> the type of an editing object.
 * @author JavaSaBr
 */
public class QuaternionPropertyControl<C extends ChangeConsumer, D> extends PropertyControl<C, D, Quaternion> {

    /**
     * The field Y.
     */
    @Nullable
    private FloatTextField xField;

    /**
     * The field X.
     */
    @Nullable
    private FloatTextField yField;

    /**
     * The field Z.
     */
    @Nullable
    private FloatTextField zField;

    public QuaternionPropertyControl(
            @Nullable Quaternion propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull HBox container) {
        super.createComponents(container);

        var xLabel = new Label("x:");

        xField = new FloatTextField();
        xField.setOnKeyReleased(this::keyReleased);
        xField.prefWidthProperty()
                .bind(widthProperty().divide(3));

        var yLabel = new Label("y:");

        yField = new FloatTextField();
        yField.setOnKeyReleased(this::keyReleased);
        yField.prefWidthProperty()
                .bind(widthProperty().divide(3));

        var zLabel = new Label("z:");

        zField = new FloatTextField();
        zField.setOnKeyReleased(this::keyReleased);
        zField.prefWidthProperty()
                .bind(widthProperty().divide(3));

        FxControlUtils.onValueChange(xField, this::changeValue);
        FxControlUtils.onValueChange(yField, this::changeValue);
        FxControlUtils.onValueChange(zField, this::changeValue);

        FxUtils.addClass(xLabel, yLabel, zLabel,
                        CssClasses.ABSTRACT_PARAM_CONTROL_NUMBER_LABEL)
                .addClass(container,
                        CssClasses.DEF_HBOX,
                        CssClasses.TEXT_INPUT_CONTAINER,
                        CssClasses.ABSTRACT_PARAM_CONTROL_INPUT_CONTAINER)
                .addClass(xField, yField, zField,
                        CssClasses.ABSTRACT_PARAM_CONTROL_VECTOR3F_FIELD,
                        CssClasses.TRANSPARENT_TEXT_FIELD);

        FxUtils.addChild(container,
                xLabel, xField,
                yLabel, yField,
                zLabel, zField);

        ObservableUtils.onChange(UiUtils.addFocusBinding(container, xField, yField, zField),
                this::applyOnLostFocus);
    }

    @Override
    @FxThread
    protected void setPropertyValue(@Nullable Quaternion quaternion) {
        super.setPropertyValue(quaternion == null ? null : quaternion.clone());
    }

    /**
     * Get the field X.
     *
     * @return the field X.
     */
    @FxThread
    private @NotNull FloatTextField getXField() {
        return notNull(xField);
    }

    /**
     * Get the field Y.
     *
     * @return the field Y.
     */
    @FxThread
    private @NotNull FloatTextField getYFiled() {
        return notNull(yField);
    }

    /**
     * Get the field Z.
     *
     * @return the field Z.
     */
    @FxThread
    private @NotNull FloatTextField getZField() {
        return notNull(zField);
    }

    @Override
    @FxThread
    protected void reload() {

        var angles = new float[3];
        var value = getPropertyValue();

        if(value != null) {
            value.toAngles(angles);
        }

        var xField = getXField();
        xField.setValue(radiansToDegree(angles[0]));
        xField.positionCaret(xField.getText().length());

        var yFiled = getYFiled();
        yFiled.setValue(radiansToDegree(angles[1]));
        yFiled.positionCaret(yFiled.getText().length());

        var zField = getZField();
        zField.setValue(radiansToDegree(angles[2]));
        zField.positionCaret(zField.getText().length());
    }

    @Override
    public boolean isDirty() {

        var angles = new float[3];
        var value = getPropertyValue();

        if(value != null) {
            value.toAngles(angles);
        }

        float xValue = getXField().getValue();
        float yValue = getYFiled().getValue();
        float zValue = getZField().getValue();

        return angles[0] != xValue || angles[1] != yValue || angles[2] != zValue;
    }

    /**
     * Handle of input the enter key.
     */
    @FxThread
    private void keyReleased(@NotNull KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            changeValue();
        }
    }

    /**
     * Change value of rotation.
     */
    @FxThread
    private void changeValue() {
        if (!isIgnoreListener()) {
            apply();
        }
    }

    @Override
    @FxThread
    protected void apply() {
        super.apply();

        var oldValue = getPropertyValue();
        if (oldValue != null) {
            oldValue = oldValue.clone();
        }

        var x = degreeToRadians(getXField().getValue());
        var y = degreeToRadians(getYFiled().getValue());
        var z = degreeToRadians(getZField().getValue());

        var newValue = new Quaternion()
                .fromAngles(ArrayFactory.toFloatArray(x, y, z));

        changed(newValue, oldValue);
    }
}
