package com.ss.editor.ui.control.property.impl;

import static java.util.Objects.requireNonNull;

import com.jme3.math.Vector2f;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import rlib.function.SixObjectConsumer;
import rlib.ui.control.input.FloatTextField;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit {@link Vector2f} values.
 *
 * @author JavaSaBr.
 */
public abstract class AbstractVector2fPropertyControl<C extends ChangeConsumer, T>
        extends AbstractPropertyControl<C, T, Vector2f> {

    /**
     * The field X.
     */
    private FloatTextField xField;

    /**
     * The field Y.
     */
    private FloatTextField yField;

    public AbstractVector2fPropertyControl(@Nullable final Vector2f propertyValue, @NotNull final String propertyName,
                                           @NotNull final C changeConsumer,
                                           @NotNull final SixObjectConsumer<C, T, String, Vector2f, Vector2f, BiConsumer<T, Vector2f>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        final Label xLabel = new Label(getXLabelText());
        xLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL2F);

        xField = new FloatTextField();
        xField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR2F_FIELD);
        xField.setOnKeyReleased(this::updateVector);
        xField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        xField.prefWidthProperty().bind(widthProperty().divide(2));
        xField.setScrollPower(10F);

        final Label yLabel = new Label(getYLabelText());
        yLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL2F);

        yField = new FloatTextField();
        yField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR2F_FIELD);
        yField.setOnKeyReleased(this::updateVector);
        yField.addChangeListener((observable, oldValue, newValue) -> updateVector(null));
        yField.prefWidthProperty().bind(widthProperty().divide(2));
        yField.setScrollPower(10F);

        FXUtils.addToPane(xLabel, container);
        FXUtils.addToPane(xField, container);
        FXUtils.addToPane(yLabel, container);
        FXUtils.addToPane(yField, container);

        FXUtils.addClassTo(xLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(xField, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(yLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(yField, CSSClasses.SPECIAL_FONT_13);
    }

    @NotNull
    protected String getYLabelText() {
        return "y:";
    }

    @NotNull
    protected String getXLabelText() {
        return "x:";
    }

    protected float checkResultXValue(final float x, final float y) {
        return x;
    }

    protected float checkResultYValue(final float x, final float y) {
        return y;
    }

    /**
     * @return the field X.
     */
    protected FloatTextField getXField() {
        return xField;
    }

    /**
     * @return the field Y.
     */
    protected FloatTextField getYField() {
        return yField;
    }

    @Override
    protected void reload() {

        final Vector2f element = requireNonNull(getPropertyValue(), "The property value can't be null.");

        final FloatTextField xField = getXField();
        xField.setValue(element.getX());
        xField.positionCaret(xField.getText().length());

        final FloatTextField yField = getYField();
        yField.setValue(element.getY());
        yField.positionCaret(xField.getText().length());
    }

    /**
     * Update the vector.
     */
    protected void updateVector(@Nullable final KeyEvent event) {
        if (isIgnoreListener() || (event != null && event.getCode() != KeyCode.ENTER)) return;

        final FloatTextField xField = getXField();
        final float x = xField.getValue();

        final FloatTextField yField = getYField();
        final float y = yField.getValue();

        final Vector2f oldValue = requireNonNull(getPropertyValue(), "The property value can't be null.");
        final Vector2f newValue = new Vector2f();
        newValue.set(checkResultXValue(x, y), checkResultYValue(x, y));

        changed(newValue, oldValue == null ? null : oldValue.clone());
    }
}
