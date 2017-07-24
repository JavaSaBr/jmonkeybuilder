package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.Vector2f;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit {@link Vector2f} values.
 *
 * @param <C> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr.
 */
public abstract class AbstractVector2fPropertyControl<C extends ChangeConsumer, T>
        extends AbstractPropertyControl<C, T, Vector2f> {

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
     * Instantiates a new Abstract vector 2 f property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     * @param changeHandler  the change handler
     */
    public AbstractVector2fPropertyControl(@Nullable final Vector2f propertyValue, @NotNull final String propertyName,
                                           @NotNull final C changeConsumer,
                                           @NotNull final SixObjectConsumer<C, T, String, Vector2f, Vector2f, BiConsumer<T, Vector2f>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        final HBox field = new HBox();
        field.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        xField = new FloatTextField();
        xField.setOnKeyReleased(this::updateVector);
        xField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        xField.prefWidthProperty().bind(field.widthProperty().multiply(0.5));
        xField.setScrollPower(10F);

        yField = new FloatTextField();
        yField.setOnKeyReleased(this::updateVector);
        yField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        yField.prefWidthProperty().bind(field.widthProperty().multiply(0.5));
        yField.setScrollPower(10F);

        FXUtils.addToPane(xField, field);
        FXUtils.addToPane(yField, field);
        FXUtils.addToPane(field, container);

        FXUtils.addClassesTo(field, CSSClasses.DEF_HBOX, CSSClasses.TEXT_INPUT_CONTAINER,
                CSSClasses.ABSTRACT_PARAM_CONTROL_SHORT_INPUT_CONTAINER);

        FXUtils.addClassesTo(xField, yField, CSSClasses.TRANSPARENT_TEXT_FIELD);

        UIUtils.addFocusBinding(field, xField, yField);
    }

    @Override
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
    protected float checkResultYValue(final float x, final float y) {
        return y;
    }

    /**
     * Gets x field.
     *
     * @return the field X.
     */
    @NotNull
    protected FloatTextField getXField() {
        return notNull(xField);
    }

    /**
     * Gets y field.
     *
     * @return the field Y.
     */
    @NotNull
    protected FloatTextField getYField() {
        return notNull(yField);
    }

    @Override
    protected void reload() {

        final Vector2f element = notNull(getPropertyValue(), "The property value can't be null.");

        final FloatTextField xField = getXField();
        xField.setValue(element.getX());
        xField.positionCaret(xField.getText().length());

        final FloatTextField yField = getYField();
        yField.setValue(element.getY());
        yField.positionCaret(xField.getText().length());
    }

    /**
     * Update the vector.
     *
     * @param event the event
     */
    private void updateVector(@Nullable final KeyEvent event) {
        UIUtils.consumeIfIsNotHotKey(event);

        if (isIgnoreListener() || (event != null && event.getCode() != KeyCode.ENTER)) return;

        final FloatTextField xField = getXField();
        final float x = xField.getValue();

        final FloatTextField yField = getYField();
        final float y = yField.getValue();

        final Vector2f oldValue = notNull(getPropertyValue(), "The property value can't be null.");
        final Vector2f newValue = new Vector2f();
        newValue.set(checkResultXValue(x, y), checkResultYValue(x, y));

        changed(newValue, oldValue == null ? null : oldValue.clone());
    }
}
