package com.ss.editor.ui.control.model.property;

import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link ModelPropertyControl} for editing {@link Vector2f} values.
 *
 * @author JavaSaBr.
 */
public class Vector2fModelPropertyControl<T extends Spatial> extends ModelPropertyControl<T, Vector2f> {

    /**
     * The field X.
     */
    private TextField xField;

    /**
     * The field Y.
     */
    private TextField yFiled;

    /**
     * The power of scrolling.
     */
    private float scrollIncrement;

    public Vector2fModelPropertyControl(final Vector2f element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
        this.scrollIncrement = 10F;
    }

    /**
     * @param scrollIncrement the power of scrolling.
     */
    public void setScrollIncrement(float scrollIncrement) {
        this.scrollIncrement = scrollIncrement;
    }

    /**
     * @return the power of scrolling.
     */
    private float getScrollIncrement() {
        return scrollIncrement;
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        final Label xLabel = new Label(getXLabelText());
        xLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        xField = new TextField();
        xField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR2F_FIELD);
        xField.setOnScroll(this::processScroll);
        xField.setOnKeyReleased(this::updateVector);

        final Label yLabel = new Label(getYLabelText());
        yLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL);

        yFiled = new TextField();
        yFiled.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR2F_FIELD);
        yFiled.setOnScroll(this::processScroll);
        yFiled.setOnKeyReleased(this::updateVector);

        FXUtils.addToPane(xLabel, container);
        FXUtils.addToPane(xField, container);
        FXUtils.addToPane(yLabel, container);
        FXUtils.addToPane(yFiled, container);
    }

    @NotNull
    protected String getYLabelText() {
        return "y:";
    }

    @NotNull
    protected String getXLabelText() {
        return "x:";
    }

    /**
     * The process of scrolling.
     */
    protected void processScroll(final ScrollEvent event) {
        if (!event.isControlDown()) return;

        final TextField source = (TextField) event.getSource();
        final String text = source.getText();

        float value;
        try {
            value = Float.parseFloat(text);
        } catch (final NumberFormatException e) {
            return;
        }

        long longValue = (long) (value * 1000);
        longValue += event.getDeltaY() * getScrollIncrement();

        source.setText(String.valueOf(longValue / 1000F));
        updateVector(null);
    }

    /**
     * @return the field X.
     */
    private TextField getXField() {
        return xField;
    }

    /**
     * @return the field Y.
     */
    private TextField getYFiled() {
        return yFiled;
    }

    @Override
    protected void reload() {
        final Vector2f element = getPropertyValue();
        final TextField xField = getXField();
        xField.setText(String.valueOf(element.getX()));
        final TextField yFiled = getYFiled();
        yFiled.setText(String.valueOf(element.getY()));
    }

    /**
     * Update the vector.
     */
    protected void updateVector(final KeyEvent event) {
        if (isIgnoreListener() || (event != null && event.getCode() != KeyCode.ENTER)) return;

        final TextField xField = getXField();
        float x;
        try {
            x = Float.parseFloat(xField.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        final TextField yFiled = getYFiled();
        float y;
        try {
            y = Float.parseFloat(yFiled.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        final Vector2f oldValue = getPropertyValue();
        final Vector2f newValue = new Vector2f();
        newValue.set(x, y);

        changed(newValue, oldValue == null ? null : oldValue.clone());
    }
}
