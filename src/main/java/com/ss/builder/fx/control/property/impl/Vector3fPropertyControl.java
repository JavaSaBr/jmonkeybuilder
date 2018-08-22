package com.ss.builder.fx.control.property.impl;

import static com.ss.builder.util.GeomUtils.zeroIfNull;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.math.Vector3f;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.util.UiUtils;
import com.ss.builder.util.GeomUtils;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.control.property.PropertyControl;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.util.UiUtils;
import com.ss.builder.util.GeomUtils;
import com.ss.rlib.fx.control.input.FloatTextField;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit {@link Vector3f} values.
 *
 * @param <C> the type of a change consumer.
 * @param <D> the type of an editing object.
 * @author JavaSaBr
 */
public class Vector3fPropertyControl<C extends ChangeConsumer, D> extends PropertyControl<C, D, Vector3f> {

    /**
     * The field X.
     */
    @NotNull
    protected final FloatTextField xField;

    /**
     * The field Y.
     */
    @NotNull
    protected final FloatTextField yField;

    /**
     * The field Z.
     */
    @NotNull
    protected final FloatTextField zField;

    public Vector3fPropertyControl(
            @Nullable Vector3f propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        this(propertyValue, propertyName, changeConsumer, null);
    }

    public Vector3fPropertyControl(
            @Nullable Vector3f propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @Nullable ChangeHandler<C, D, Vector3f> changeHandler
    ) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
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
        xField.setScrollPower(getScrollPower());
        xField.prefWidthProperty().
                bind(widthProperty().divide(3));

        var yLabel = new Label("y:");

        yField.setOnKeyReleased(this::keyReleased);
        yField.setScrollPower(getScrollPower());
        yField.prefWidthProperty()
                .bind(widthProperty().divide(3));

        var zLabel = new Label("z:");

        zField.setOnKeyReleased(this::keyReleased);
        zField.setScrollPower(getScrollPower());
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

        UiUtils.addFocusBinding(container, xField, yField, zField)
                .addListener((observable, oldValue, newValue) -> applyOnLostFocus(newValue));
    }

    @Override
    @FxThread
    protected void setPropertyValue(@Nullable Vector3f vector) {
        super.setPropertyValue(GeomUtils.zeroIfNull(vector).clone());
    }

    /**
     * Get the scroll power.
     *
     * @return the scroll power.
     */
    @FxThread
    protected float getScrollPower() {
        return 10F;
    }

    /**
     * Check result x value.
     *
     * @param x the x.
     * @param y the y.
     * @param z the z.
     * @return the result x value.
     */
    @FxThread
    protected float checkResultXValue(float x, float y, float z) {
        return x;
    }

    /**
     * Check result y value.
     *
     * @param x the x.
     * @param y the y.
     * @param z the z.
     * @return the result y value.
     */
    @FxThread
    protected float checkResultYValue(float x, float y, float z) {
        return y;
    }

    /**
     * Check result z value.
     *
     * @param x the x.
     * @param y the y.
     * @param z the z.
     * @return the result z value.
     */
    @FxThread
    protected float checkResultZValue(float x, float y, float z) {
        return z;
    }


    @Override
    @FxThread
    protected void reloadImpl() {

        var vector = zeroIfNull(getPropertyValue());

        xField.setValue(vector.getX());
        xField.positionCaret(xField.getText().length());

        yField.setValue(vector.getY());
        yField.positionCaret(xField.getText().length());

        zField.setValue(vector.getZ());
        zField.positionCaret(xField.getText().length());

        super.reloadImpl();
    }

    @Override
    @FxThread
    public boolean isDirty() {

        var x = xField.getPrimitiveValue();
        var y = yField.getPrimitiveValue();
        var z = zField.getPrimitiveValue();

        return !GeomUtils.equals(getPropertyValue(), x, y, z);
    }

    /**
     * Handle of input the enter key.
     */
    @FxThread
    protected void keyReleased(@NotNull KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            changeValue();
        }
    }

    /**
     * Change value of vector.
     */
    @FxThread
    protected void changeValue() {
        if (!isIgnoreListener()) {
            apply();
        }
    }

    @Override
    @FxThread
    protected void apply() {
        super.apply();

        var x = xField.getPrimitiveValue();
        var y = yField.getPrimitiveValue();
        var z = zField.getPrimitiveValue();

        var storedValue =  zeroIfNull(getPropertyValue());
        var newValue = new Vector3f();
        newValue.set(checkResultXValue(x, y, z), checkResultYValue(x, y, z), checkResultZValue(x, y, z));

        changed(newValue, storedValue.clone());
    }
}
