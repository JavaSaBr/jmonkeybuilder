package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.Vector3f;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
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
 * The implementation of the {@link PropertyControl} to edit {@link com.jme3.math.Vector3f} values.
 *
 * @param <C> the change consumer's type.
 * @param <T> the edited object's type.
 * @author JavaSaBr.
 */
public class Vector3FSingleRowPropertyControl<C extends ChangeConsumer, T> extends PropertyControl<C, T, Vector3f> {

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

    public Vector3FSingleRowPropertyControl(@Nullable final Vector3f propertyValue, @NotNull final String propertyName,
                                            @NotNull final C changeConsumer) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    public void changeControlWidthPercent(final double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        final HBox valueField = getFieldContainer();
        valueField.prefWidthProperty().unbind();
        valueField.prefWidthProperty().bind(widthProperty().multiply(controlWidthPercent));
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        fieldContainer = new HBox();
        fieldContainer.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        xField = new FloatTextField();
        xField.setOnKeyReleased(this::updateVector);
        xField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        xField.prefWidthProperty().bind(fieldContainer.widthProperty().multiply(0.33));
        xField.setScrollPower(10F);

        yField = new FloatTextField();
        yField.setOnKeyReleased(this::updateVector);
        yField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        yField.prefWidthProperty().bind(fieldContainer.widthProperty().multiply(0.33));
        yField.setScrollPower(10F);

        zField = new FloatTextField();
        zField.setOnKeyReleased(this::updateVector);
        zField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        zField.prefWidthProperty().bind(fieldContainer.widthProperty().multiply(0.33));
        zField.setScrollPower(10F);

        FXUtils.addToPane(xField, fieldContainer);
        FXUtils.addToPane(yField, fieldContainer);
        FXUtils.addToPane(zField, fieldContainer);
        FXUtils.addToPane(fieldContainer, container);

        FXUtils.addClassesTo(fieldContainer, CssClasses.DEF_HBOX, CssClasses.TEXT_INPUT_CONTAINER,
                CssClasses.ABSTRACT_PARAM_CONTROL_SHORT_INPUT_CONTAINER);
        FXUtils.addClassesTo(xField, yField, zField, CssClasses.TRANSPARENT_TEXT_FIELD);

        UiUtils.addFocusBinding(fieldContainer, xField, yField, zField);
    }

    /**
     * @return the field container.
     */
    @FxThread
    private @NotNull HBox getFieldContainer() {
        return notNull(fieldContainer);
    }

    @Override
    @FxThread
    protected void setPropertyValue(@Nullable final Vector3f vector) {
        super.setPropertyValue(vector == null ? null : vector.clone());
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
    protected float checkResultXValue(final float x, final float y, final float z) {
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
    protected float checkResultYValue(final float x, final float y, final float z) {
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
    protected float checkResultZValue(final float x, final float y, final float z) {
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

        final Vector3f vector = getPropertyValue() == null ? Vector3f.ZERO : getPropertyValue();

        final FloatTextField xField = getXField();
        xField.setValue(vector.getX());
        xField.positionCaret(xField.getText().length());

        final FloatTextField yField = getYField();
        yField.setValue(vector.getY());
        yField.positionCaret(yField.getText().length());

        final FloatTextField zField = getZField();
        zField.setValue(vector.getZ());
        zField.positionCaret(zField.getText().length());
    }

    /**
     * Update the current value.
     *
     * @param event the change event.
     */
    @FxThread
    private void updateVector(@Nullable final KeyEvent event) {

        if (isIgnoreListener() || (event != null && event.getCode() != KeyCode.ENTER)) {
            return;
        }

        final FloatTextField xField = getXField();
        final float x = xField.getValue();

        final FloatTextField yField = getYField();
        final float y = yField.getValue();

        final FloatTextField zField = getZField();
        final float z = zField.getValue();

        final Vector3f oldValue = getPropertyValue() == null ? Vector3f.ZERO : getPropertyValue();
        final Vector3f newValue = new Vector3f();
        newValue.set(checkResultXValue(x, y, z), checkResultYValue(x, y, z), checkResultZValue(x, y, z));

        changed(newValue, oldValue.clone());
    }
}
