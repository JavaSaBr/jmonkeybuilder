package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.Vector2f;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.UIUtils;
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
 * @param <C> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr.
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

    public Vector2FPropertyControl(@Nullable final Vector2f propertyValue, @NotNull final String propertyName,
                                   @NotNull final C changeConsumer) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FXThread
    public void changeControlWidthPercent(final double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        final HBox valueField = getFieldContainer();
        valueField.prefWidthProperty().unbind();
        valueField.prefWidthProperty().bind(widthProperty().multiply(controlWidthPercent));
    }

    @Override
    @FXThread
    protected void createComponents(@NotNull final HBox container) {
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

        FXUtils.addClassesTo(fieldContainer, CSSClasses.DEF_HBOX, CSSClasses.TEXT_INPUT_CONTAINER,
                CSSClasses.ABSTRACT_PARAM_CONTROL_SHORT_INPUT_CONTAINER);

        FXUtils.addClassesTo(xField, yField, CSSClasses.TRANSPARENT_TEXT_FIELD);

        UIUtils.addFocusBinding(fieldContainer, xField, yField);
    }

    /**
     * @return the field container.
     */
    @FXThread
    private @NotNull HBox getFieldContainer() {
        return notNull(fieldContainer);
    }

    @Override
    @FXThread
    protected void setPropertyValue(@Nullable final Vector2f vector) {
        super.setPropertyValue(vector == null ? null : vector.clone());
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * Check result x value float.
     *
     * @param x the x
     * @param y the y
     * @return the float
     */
    @FXThread
    protected float checkResultXValue(final float x, final float y) {
        return x;
    }

    /**
     * Check result y value float.
     *
     * @param x the x
     * @param y the y
     * @return the float
     */
    @FXThread
    protected float checkResultYValue(final float x, final float y) {
        return y;
    }

    /**
     * Gets x field.
     *
     * @return the field X.
     */
    @FXThread
    protected @NotNull FloatTextField getXField() {
        return notNull(xField);
    }

    /**
     * Gets y field.
     *
     * @return the field Y.
     */
    @FXThread
    protected @NotNull FloatTextField getYField() {
        return notNull(yField);
    }

    @Override
    @FXThread
    protected void reload() {

        final Vector2f vector = getPropertyValue() == null ? Vector2f.ZERO : getPropertyValue();

        final FloatTextField xField = getXField();
        xField.setValue(vector.getX());
        xField.positionCaret(xField.getText().length());

        final FloatTextField yField = getYField();
        yField.setValue(vector.getY());
        yField.positionCaret(yField.getText().length());
    }

    /**
     * Update the vector.
     *
     * @param event the event
     */
    @FXThread
    private void updateVector(@Nullable final KeyEvent event) {
        if (isIgnoreListener() || (event != null && event.getCode() != KeyCode.ENTER)) return;

        final FloatTextField xField = getXField();
        final float x = xField.getValue();

        final FloatTextField yField = getYField();
        final float y = yField.getValue();

        final Vector2f oldValue = getPropertyValue() == null ? Vector2f.ZERO : getPropertyValue();
        final Vector2f newValue = new Vector2f();
        newValue.set(checkResultXValue(x, y), checkResultYValue(x, y));

        changed(newValue, oldValue.clone());
    }
}
