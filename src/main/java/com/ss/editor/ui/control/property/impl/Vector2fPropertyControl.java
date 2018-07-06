package com.ss.editor.ui.control.property.impl;

import static com.ss.editor.util.GeomUtils.zeroIfNull;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.math.Vector2f;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.UiUtils;
import com.ss.editor.util.GeomUtils;
import com.ss.rlib.fx.control.input.FloatTextField;
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

    public Vector2fPropertyControl(
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

        FxUtils.rebindPrefWidth(getFieldContainer(),
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
        getXField().setMinMax(min, max);
        getYField().setMinMax(min, max);
    }

    /**
     * Sets the scroll power.
     *
     * @param scrollPower the scroll power.
     */
    @FxThread
    public void setScrollPower(float scrollPower) {
        getXField().setScrollPower(scrollPower);
        getYField().setScrollPower(scrollPower);
    }

    /**
     * Get the scroll power.
     *
     * @return the scroll power.
     */
    @FxThread
    public float getScrollPower() {
        return getXField().getScrollPower();
    }

    @Override
    @FxThread
    protected void createControls(@NotNull HBox container) {
        super.createControls(container);

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

        FxUtils.addClass(fieldContainer,
                        CssClasses.DEF_HBOX,
                        CssClasses.TEXT_INPUT_CONTAINER,
                        CssClasses.ABSTRACT_PARAM_CONTROL_SHORT_INPUT_CONTAINER)
                .addClass(xField, yField,
                        CssClasses.TRANSPARENT_TEXT_FIELD);

        FxUtils.addChild(fieldContainer, xField, yField)
                .addChild(container, fieldContainer);

        UiUtils.addFocusBinding(fieldContainer, xField, yField)
            .addListener((observable, oldValue, newValue) -> applyOnLostFocus(newValue));
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

        var vector = zeroIfNull(getPropertyValue());

        var xField = getXField();
        xField.setValue(vector.getX());
        xField.positionCaret(xField.getText().length());

        var yField = getYField();
        yField.setValue(vector.getY());
        yField.positionCaret(yField.getText().length());
    }

    @Override
    @FxThread
    public boolean isDirty() {

        var x = getXField().getValue();
        var y = getYField().getValue();

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

        var x = getXField().getValue();
        var y = getYField().getValue();

        var oldValue = zeroIfNull(getPropertyValue());
        var newValue = new Vector2f(checkResultXValue(x, y), checkResultYValue(x, y));

        changed(newValue, oldValue.clone());
    }
}
