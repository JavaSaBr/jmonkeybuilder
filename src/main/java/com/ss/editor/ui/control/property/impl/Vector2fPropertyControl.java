package com.ss.editor.ui.control.property.impl;

import static com.ss.editor.util.GeomUtils.zeroIfNull;
import com.jme3.math.Vector2f;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.GeomUtils;
import com.ss.rlib.fx.control.input.FloatTextField;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit {@link Vector2f} values.
 *
 * @param <C> the type of a change consumer.
 * @param <D> the type of an editing object.
 * @author JavaSaBr
 */
public class Vector2fPropertyControl<C extends ChangeConsumer, D> extends PropertyControl<C, D, Vector2f> {

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
     * The field container.
     */
    @NotNull
    private final HBox fieldContainer;

    public Vector2fPropertyControl(
            @Nullable Vector2f propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
        this.fieldContainer = new HBox();
        this.xField = new FloatTextField();
        this.yField = new FloatTextField();
    }

    @Override
    @FxThread
    public void changeControlWidthPercent(double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        FxUtils.rebindPrefWidth(fieldContainer,
                widthProperty().multiply(controlWidthPercent));
    }

    /**
     * Set value limits for this field.
     *
     * @param min the min value.
     * @param max the max value.
     */
    @FxThread
    public void setMinMax(float min, float max) {
        xField.setMinMax(min, max);
        yField.setMinMax(min, max);
    }

    /**
     * Sets the scroll power.
     *
     * @param scrollPower the scroll power.
     */
    @FxThread
    public void setScrollPower(float scrollPower) {
        xField.setScrollPower(scrollPower);
        yField.setScrollPower(scrollPower);
    }

    /**
     * Get the scroll power.
     *
     * @return the scroll power.
     */
    @FxThread
    public float getScrollPower() {
        return xField.getScrollPower();
    }

    @Override
    @FxThread
    protected void createControls(@NotNull HBox container) {
        super.createControls(container);

        fieldContainer.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        xField.setOnKeyReleased(this::updateVector);
        xField.setScrollPower(10F);
        xField.prefWidthProperty()
                .bind(fieldContainer.widthProperty().multiply(0.5));

        yField.setOnKeyReleased(this::updateVector);
        yField.setScrollPower(10F);
        yField.prefWidthProperty()
                .bind(fieldContainer.widthProperty().multiply(0.5));

        FxUtils.addClass(fieldContainer,
                        CssClasses.DEF_HBOX,
                        CssClasses.TEXT_INPUT_CONTAINER,
                        CssClasses.ABSTRACT_PARAM_CONTROL_SHORT_INPUT_CONTAINER)
                .addClass(xField, yField,
                        CssClasses.TRANSPARENT_TEXT_FIELD);

        FxControlUtils.onValueChange(xField, () -> updateVector(null));
        FxControlUtils.onValueChange(yField, () -> updateVector(null));

        FxUtils.addChild(fieldContainer, xField, yField)
                .addChild(container, fieldContainer);

        UiUtils.addFocusBinding(fieldContainer, xField, yField)
                .addListener((observable, oldValue, newValue) -> applyOnLostFocus(newValue));
    }

    @Override
    @FxThread
    protected void setPropertyValue(@Nullable Vector2f vector) {
        super.setPropertyValue(zeroIfNull(vector).clone());
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

    @Override
    @FxThread
    protected void reloadImpl() {

        var vector = zeroIfNull(getPropertyValue());

        xField.setValue(vector.getX());
        xField.positionCaret(xField.getText().length());

        yField.setValue(vector.getY());
        yField.positionCaret(yField.getText().length());

        super.reloadImpl();
    }

    @Override
    @FxThread
    public boolean isDirty() {
        var x = xField.getPrimitiveValue();
        var y = yField.getPrimitiveValue();
        return !GeomUtils.equals(getPropertyValue(), x, y);
    }

    /**
     * Update the vector.
     *
     * @param event the event
     */
    @FxThread
    private void updateVector(@Nullable KeyEvent event) {
        if (!isIgnoreListener() && (event == null || event.getCode() == KeyCode.ENTER)) {
            apply();
        }
    }

    @Override
    @FxThread
    protected void apply() {
        super.apply();

        var x = xField.getPrimitiveValue();
        var y = yField.getPrimitiveValue();

        var oldValue = zeroIfNull(getPropertyValue());
        var newValue = new Vector2f(checkResultXValue(x, y), checkResultYValue(x, y));

        changed(newValue, oldValue.clone());
    }
}
