package com.ss.editor.ui.control.property.impl;

import static com.ss.editor.util.GeomUtils.zeroIfNull;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.Vector3f;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.GeomUtils;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;
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
    @Nullable
    private FloatTextField xField;

    /**
     * The field Y.
     */
    @Nullable
    private FloatTextField yField;

    /**
     * The field Z.
     */
    @Nullable
    private FloatTextField zField;

    public Vector3fPropertyControl(
            @Nullable Vector3f propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
    }

    public Vector3fPropertyControl(
            @Nullable Vector3f propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @Nullable ChangeHandler<C, D, Vector3f> changeHandler
    ) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull HBox container) {
        super.createComponents(container);

        var xLabel = new Label("x:");

        xField = new FloatTextField();
        xField.setOnKeyReleased(this::updateVector);
        xField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        xField.prefWidthProperty().bind(widthProperty().divide(3));
        xField.setScrollPower(getScrollPower());

        var yLabel = new Label("y:");

        yField = new FloatTextField();
        yField.setOnKeyReleased(this::updateVector);
        yField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        yField.prefWidthProperty().bind(widthProperty().divide(3));
        yField.setScrollPower(getScrollPower());

        var zLabel = new Label("z:");

        zField = new FloatTextField();
        zField.setOnKeyReleased(this::updateVector);
        zField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        zField.prefWidthProperty().bind(widthProperty().divide(3));
        zField.setScrollPower(getScrollPower());

        FXUtils.addToPane(xLabel, container);
        FXUtils.addToPane(xField, container);
        FXUtils.addToPane(yLabel, container);
        FXUtils.addToPane(yField, container);
        FXUtils.addToPane(zLabel, container);
        FXUtils.addToPane(zField, container);

        FXUtils.addClassTo(xLabel, yLabel, zLabel, CssClasses.ABSTRACT_PARAM_CONTROL_NUMBER_LABEL);
        FXUtils.addClassesTo(container, CssClasses.DEF_HBOX, CssClasses.TEXT_INPUT_CONTAINER,
                CssClasses.ABSTRACT_PARAM_CONTROL_INPUT_CONTAINER);
        FXUtils.addClassesTo(xField, yField, zField, CssClasses.ABSTRACT_PARAM_CONTROL_VECTOR3F_FIELD,
                CssClasses.TRANSPARENT_TEXT_FIELD);

        UiUtils.addFocusBinding(container, xField, yField, zField)
                .addListener((observable, oldValue, newValue) -> applyOnLostFocus(newValue));
    }

    @Override
    @FxThread
    protected void setPropertyValue(@Nullable Vector3f vector) {
        super.setPropertyValue(zeroIfNull(vector).clone());
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
     * Get the field X.
     *
     * @return the field X.
     */
    @FxThread
    protected @NotNull FloatTextField getXField() {
        return notNull(xField);
    }

    /**
     * Get the filed Y.
     *
     * @return the field Y.
     */
    @FxThread
    protected @NotNull FloatTextField getYField() {
        return notNull(yField);
    }

    /**
     * Get the field Z.
     *
     * @return the field Z.
     */
    @FxThread
    protected @NotNull FloatTextField getZField() {
        return notNull(zField);
    }

    @Override
    @FxThread
    protected void reload() {

        var vector = zeroIfNull(getPropertyValue());

        var xField = getXField();
        xField.setValue(vector.getX());
        xField.positionCaret(xField.getText().length());

        var yFiled = getYField();
        yFiled.setValue(vector.getY());
        yFiled.positionCaret(xField.getText().length());

        var zField = getZField();
        zField.setValue(vector.getZ());
        zField.positionCaret(xField.getText().length());
    }

    @Override
    @FxThread
    public boolean isDirty() {

        var x = getXField().getValue();
        var y = getYField().getValue();
        var z = getZField().getValue();

        return GeomUtils.equals(getPropertyValue(), x, y, z);
    }

    /**
     * Update the vector.
     *
     * @param event the event.
     */
    @FxThread
    protected void updateVector(@Nullable KeyEvent event) {
        if (!isIgnoreListener() && (event == null || event.getCode() == KeyCode.ENTER)) {
            apply();
        }
    }

    @Override
    @FxThread
    protected void apply() {
        super.apply();

        var x = getXField().getValue();
        var y = getYField().getValue();
        var z = getZField().getValue();

        var storedValue =  zeroIfNull(getPropertyValue());

        changed(new Vector3f(x, y, z), storedValue.clone());
    }
}
