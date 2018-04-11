package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.Vector2f;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit {@link Vector2f} values.
 *
 * @param <C> the type of a {@link ChangeConsumer}
 * @param <T> the type of an editing object.
 * @author JavaSaBr
 */
public class Vector2FPropertyControl<C extends ChangeConsumer, T> extends PropertyControl<C, T, Vector2f> {

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
     * The field container.
     */
    @Nullable
    private HBox fieldContainer;

    public Vector2FPropertyControl(
            @Nullable Vector2f propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    public void changeControlWidthPercent(double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        var valueField = getFieldContainer();
        valueField.prefWidthProperty().unbind();
        valueField.prefWidthProperty().bind(widthProperty().multiply(controlWidthPercent));
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull HBox container) {
        super.createComponents(container);

        fieldContainer = new HBox();
        fieldContainer.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        xField = new FloatTextField();
        xField.setOnKeyReleased(this::updateVector);
        xField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        xField.prefWidthProperty().bind(fieldContainer.widthProperty().multiply(0.5));
        xField.setScrollPower(10F);

        yField = new FloatTextField();
        yField.setOnKeyReleased(this::updateVector);
        yField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        yField.prefWidthProperty().bind(fieldContainer.widthProperty().multiply(0.5));
        yField.setScrollPower(10F);

        FXUtils.addToPane(xField, fieldContainer);
        FXUtils.addToPane(yField, fieldContainer);
        FXUtils.addToPane(fieldContainer, container);

        FXUtils.addClassesTo(fieldContainer, CssClasses.DEF_HBOX, CssClasses.TEXT_INPUT_CONTAINER,
                CssClasses.ABSTRACT_PARAM_CONTROL_SHORT_INPUT_CONTAINER);

        FXUtils.addClassesTo(xField, yField, CssClasses.TRANSPARENT_TEXT_FIELD);

        UiUtils.addFocusBinding(fieldContainer, xField, yField);
    }

    /**
     * Get the field container.
     *
     * @return the field container.
     */
    @FxThread
    private @NotNull HBox getFieldContainer() {
        return notNull(fieldContainer);
    }

    @Override
    @FxThread
    protected void setPropertyValue(@Nullable Vector2f vector) {
        super.setPropertyValue(vector == null ? null : vector.clone());
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * Check the result x value.
     *
     * @param x the x.
     * @param y the y.
     * @return the result x value.
     */
    @FxThread
    protected float checkResultXValue(float x, float y) {
        return x;
    }

    /**
     * Check result y value.
     *
     * @param x the x.
     * @param y the y.
     * @return the result y value.
     */
    @FxThread
    protected float checkResultYValue(float x, float y) {
        return y;
    }

    /**
     * Get the X field.
     *
     * @return the X field.
     */
    @FxThread
    protected @NotNull FloatTextField getXField() {
        return notNull(xField);
    }

    /**
     * Get the Y field.
     *
     * @return the Y field.
     */
    @FxThread
    protected @NotNull FloatTextField getYField() {
        return notNull(yField);
    }

    @Override
    @FxThread
    protected void reload() {

        var vector = getPropertyValue() == null ? Vector2f.ZERO : getPropertyValue();

        var xField = getXField();
        xField.setValue(vector.getX());
        xField.positionCaret(xField.getText().length());

        var yField = getYField();
        yField.setValue(vector.getY());
        yField.positionCaret(yField.getText().length());
    }

    /**
     * Update the vector.
     *
     * @param event the event
     */
    @FxThread
    private void updateVector(@Nullable KeyEvent event) {

        if (isIgnoreListener() || (event != null && event.getCode() != KeyCode.ENTER)) {
            return;
        }

        var xField = getXField();
        var x = xField.getValue();

        var yField = getYField();
        var y = yField.getValue();

        var oldValue = getPropertyValue() == null ? Vector2f.ZERO : getPropertyValue();
        var newValue = new Vector2f();
        newValue.set(checkResultXValue(x, y), checkResultYValue(x, y));

        changed(newValue, oldValue.clone());
    }
}
