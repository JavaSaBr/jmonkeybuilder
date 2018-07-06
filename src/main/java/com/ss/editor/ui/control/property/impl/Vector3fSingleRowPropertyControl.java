package com.ss.editor.ui.control.property.impl;

import static com.ss.editor.util.GeomUtils.zeroIfNull;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.math.Vector3f;
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
 * The implementation of the {@link PropertyControl} to edit {@link com.jme3.math.Vector3f} values.
 *
 * @param <C> the type of a change consumer.
 * @param <D> the type of an editing object.
 * @author JavaSaBr.
 */
public class Vector3fSingleRowPropertyControl<C extends ChangeConsumer, D> extends PropertyControl<C, D, Vector3f> {

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

    /**
     * The field container.
     */
    @Nullable
    private HBox fieldContainer;

    public Vector3fSingleRowPropertyControl(
            @Nullable Vector3f propertyValue,
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

    @Override
    @FxThread
    protected void createControls(@NotNull HBox container) {
        super.createControls(container);

        fieldContainer = new HBox();
        fieldContainer.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        xField = new FloatTextField();
        xField.setOnKeyReleased(this::keyReleased);
        xField.setScrollPower(10F);
        xField.prefWidthProperty().
                bind(fieldContainer.widthProperty().multiply(0.33));

        yField = new FloatTextField();
        yField.setOnKeyReleased(this::keyReleased);
        yField.setScrollPower(10F);
        yField.prefWidthProperty()
                .bind(fieldContainer.widthProperty().multiply(0.33));

        zField = new FloatTextField();
        zField.setOnKeyReleased(this::keyReleased);
        zField.setScrollPower(10F);
        zField.prefWidthProperty()
                .bind(fieldContainer.widthProperty().multiply(0.33));

        FxControlUtils.onValueChange(xField, this::changeValue);
        FxControlUtils.onValueChange(yField, this::changeValue);
        FxControlUtils.onValueChange(zField, this::changeValue);

        FxUtils.addClass(fieldContainer,
                        CssClasses.DEF_HBOX,
                        CssClasses.TEXT_INPUT_CONTAINER,
                        CssClasses.ABSTRACT_PARAM_CONTROL_SHORT_INPUT_CONTAINER)
                .addClass(xField, yField, zField,
                        CssClasses.TRANSPARENT_TEXT_FIELD);

        FxUtils.addChild(fieldContainer, xField, yField, zField)
                .addChild(container, fieldContainer);

        UiUtils.addFocusBinding(fieldContainer, xField, yField, zField)
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
    protected void setPropertyValue(@Nullable Vector3f vector) {
        super.setPropertyValue(zeroIfNull(vector).clone());
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
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

    /**
     * Get the Z field.
     *
     * @return the Z field.
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

        var yField = getYField();
        yField.setValue(vector.getY());
        yField.positionCaret(yField.getText().length());

        var zField = getZField();
        zField.setValue(vector.getZ());
        zField.positionCaret(zField.getText().length());
    }

    @Override
    @FxThread
    public boolean isDirty() {

        var x = getXField().getValue();
        var y = getYField().getValue();
        var z = getZField().getValue();

        return !GeomUtils.equals(getPropertyValue(), x, y, z);
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
     * Change value of vector.
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

        var x = getXField().getValue();
        var y = getYField().getValue();
        var z = getZField().getValue();

        var oldValue = zeroIfNull(getPropertyValue());
        var newValue = new Vector3f();
        newValue.set(checkResultXValue(x, y, z), checkResultYValue(x, y, z), checkResultZValue(x, y, z));

        changed(newValue, oldValue.clone());
    }
}
