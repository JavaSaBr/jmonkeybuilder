package com.ss.editor.ui.control.model.property;

import static java.util.Objects.requireNonNull;

import com.jme3.math.Vector2f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSClasses;
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
public abstract class AbstractVector2fModelPropertyControl<T> extends ModelPropertyControl<T, Vector2f> {

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

    public AbstractVector2fModelPropertyControl(final Vector2f element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
        this.scrollIncrement = 10F;
    }

    /**
     * @param scrollIncrement the power of scrolling.
     */
    public void setScrollIncrement(final float scrollIncrement) {
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
        xLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL2F);

        xField = new TextField();
        xField.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR2F_FIELD);
        xField.setOnScroll(this::processScroll);
        xField.setOnKeyReleased(this::updateVector);
        xField.prefWidthProperty().bind(widthProperty().divide(2));

        final Label yLabel = new Label(getYLabelText());
        yLabel.setId(CSSIds.MODEL_PARAM_CONTROL_NUMBER_LABEL2F);

        yFiled = new TextField();
        yFiled.setId(CSSIds.MODEL_PARAM_CONTROL_VECTOR2F_FIELD);
        yFiled.setOnScroll(this::processScroll);
        yFiled.setOnKeyReleased(this::updateVector);
        yFiled.prefWidthProperty().bind(widthProperty().divide(2));

        FXUtils.addToPane(xLabel, container);
        FXUtils.addToPane(xField, container);
        FXUtils.addToPane(yLabel, container);
        FXUtils.addToPane(yFiled, container);

        FXUtils.addClassTo(xLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(xField, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(yLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(yFiled, CSSClasses.SPECIAL_FONT_13);
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

        final String result = String.valueOf(longValue / 1000F);
        source.setText(result);
        source.positionCaret(result.length());

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

        final Vector2f element = requireNonNull(getPropertyValue(), "The property value can't be null.");

        final TextField xField = getXField();
        xField.setText(String.valueOf(element.getX()));
        xField.positionCaret(xField.getText().length());

        final TextField yFiled = getYFiled();
        yFiled.setText(String.valueOf(element.getY()));
        yFiled.positionCaret(xField.getText().length());
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

        final Vector2f oldValue = requireNonNull(getPropertyValue(), "The property value can't be null.");
        final Vector2f newValue = new Vector2f();
        newValue.set(x, y);

        changed(newValue, oldValue == null ? null : oldValue.clone());
    }
}
