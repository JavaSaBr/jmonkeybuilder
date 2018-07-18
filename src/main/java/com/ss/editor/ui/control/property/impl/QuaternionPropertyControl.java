package com.ss.editor.ui.control.property.impl;

import com.jme3.math.Quaternion;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.LocalObjects;
import com.ss.rlib.common.geom.util.AngleUtils;
import com.ss.rlib.common.util.ExtMath;
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

import java.util.Arrays;

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
    @NotNull
    private final FloatTextField xField;

    /**
     * The field X.
     */
    @NotNull
    private final FloatTextField yField;

    /**
     * The field Z.
     */
    @NotNull
    private final FloatTextField zField;

    public QuaternionPropertyControl(
            @Nullable Quaternion propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
        this.xField = new FloatTextField();
        this.yField = new FloatTextField();
        this.zField = new FloatTextField();
    }

    @Override
    @FxThread
    protected void createControls(@NotNull HBox container) {
        super.createControls(container);

        var xLabel = new Label("x:");

        xField.setOnKeyReleased(this::keyReleased);
        xField.prefWidthProperty()
                .bind(widthProperty().divide(3));

        var yLabel = new Label("y:");

        yField.setOnKeyReleased(this::keyReleased);
        yField.prefWidthProperty()
                .bind(widthProperty().divide(3));

        var zLabel = new Label("z:");

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
                        CssClasses.PROPERTY_CONTROL_VECTOR_3F_FIELD,
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

    @Override
    @FxThread
    protected void reloadImpl() {

        var angles = LocalObjects.get()
                .nextArray3f();

        var value = getPropertyValue();

        if (value != null) {
            value.toAngles(angles);
        } else {
            Arrays.fill(angles, 0F);
        }

        xField.setValue(AngleUtils.radiansToDegree(angles[0]));
        xField.positionCaret(xField.getText().length());

        yField.setValue(AngleUtils.radiansToDegree(angles[1]));
        yField.positionCaret(yField.getText().length());

        zField.setValue(AngleUtils.radiansToDegree(angles[2]));
        zField.positionCaret(zField.getText().length());

        super.reloadImpl();
    }

    @Override
    public boolean isDirty() {

        var angles = LocalObjects.get()
                .nextArray3f();

        var value = getPropertyValue();

        if (value != null) {
            value.toAngles(angles);
        } else {
            Arrays.fill(angles, 0F);
        }

        var x = xField.getPrimitiveValue();
        var y = yField.getPrimitiveValue();
        var z = zField.getPrimitiveValue();

        return !ExtMath.equals(angles[0], x) ||
                !ExtMath.equals(angles[1], y) ||
                !ExtMath.equals(angles[2], z);
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

        var x = AngleUtils.degreeToRadians(xField.getPrimitiveValue());
        var y = AngleUtils.degreeToRadians(yField.getPrimitiveValue());
        var z = AngleUtils.degreeToRadians(zField.getPrimitiveValue());

        var newValue = new Quaternion()
                .fromAngles(ArrayFactory.toFloatArray(x, y, z));

        changed(newValue, oldValue);
    }
}
