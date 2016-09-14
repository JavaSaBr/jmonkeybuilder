package com.ss.editor.ui.control.model.property;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSIds;

import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

import static java.lang.Float.parseFloat;

/**
 * The implementation of the {@link ModelPropertyControl} for editing float values.
 *
 * @author JavaSaBr
 */
public class FloatModelPropertyControl<T extends Spatial> extends ModelPropertyControl<T, Float> {

    /**
     * The filed with current value.
     */
    private TextField valueField;

    /**
     * The power of scrolling.
     */
    private float scrollIncrement;

    public FloatModelPropertyControl(final Float element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
        this.scrollIncrement = 1F;
    }

    @Override
    protected void createComponents(final HBox container) {
        super.createComponents(container);

        valueField = new TextField();
        valueField.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
        valueField.setOnScroll(this::processScroll);
        valueField.textProperty().addListener((observable, oldValue, newValue) -> updateValue());

        FXUtils.addToPane(valueField, container);
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
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * The process of scrolling value.
     */
    private void processScroll(final ScrollEvent event) {
        if (!event.isControlDown()) return;

        final TextField source = (TextField) event.getSource();
        final String text = source.getText();

        float value;
        try {
            value = parseFloat(text);
        } catch (final NumberFormatException e) {
            return;
        }

        long longValue = (long) (value * 1000);
        longValue += event.getDeltaY() * getScrollIncrement();

        source.setText(String.valueOf(longValue / 1000F));
    }

    /**
     * @return the filed with current value.
     */
    private TextField getValueField() {
        return valueField;
    }

    @Override
    protected void reload() {
        final Float element = getPropertyValue();
        final TextField valueField = getValueField();
        valueField.setText(String.valueOf(element));
    }

    /**
     * Update the value.
     */
    private void updateValue() {
        if (isIgnoreListener()) return;

        final TextField valueField = getValueField();
        float value;
        try {
            value = parseFloat(valueField.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        final Float oldValue = getPropertyValue();
        changed(value, oldValue);
    }
}
