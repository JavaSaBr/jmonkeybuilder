package com.ss.editor.ui.control.model.property;

import static java.lang.Integer.parseInt;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link ModelPropertyControl} for editing integer values.
 *
 * @author JavaSaBr
 */
public abstract class AbstractIntegerModelPropertyControl<T> extends ModelPropertyControl<T, Integer> {

    /**
     * The filed with current value.
     */
    private TextField valueField;

    /**
     * The power of scrolling.
     */
    private float scrollIncrement;

    public AbstractIntegerModelPropertyControl(final Integer element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
        this.scrollIncrement = 30F;
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        valueField = new TextField();
        valueField.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
        valueField.setOnScroll(this::processScroll);
        valueField.textProperty().addListener((observable, oldValue, newValue) -> updateValue());
        valueField.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addClassTo(valueField, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addToPane(valueField, container);
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

        int value;
        try {
            value = parseInt(text);
        } catch (final NumberFormatException e) {
            return;
        }

        long longValue = (long) (value * 1000);
        longValue += event.getDeltaY() * getScrollIncrement();

        final int caretPosition = source.getCaretPosition();
        final String result = String.valueOf(longValue / 1000);
        source.setText(result);
        source.positionCaret(caretPosition);
    }

    /**
     * @return the filed with current value.
     */
    private TextField getValueField() {
        return valueField;
    }

    @Override
    protected void reload() {
        final Integer element = getPropertyValue();
        final TextField valueField = getValueField();
        final int caretPosition = valueField.getCaretPosition();
        valueField.setText(String.valueOf(element));
        valueField.positionCaret(caretPosition);
    }

    /**
     * Update the value.
     */
    private void updateValue() {
        if (isIgnoreListener()) return;

        final TextField valueField = getValueField();
        int value;
        try {
            value = parseInt(valueField.getText());
        } catch (final NumberFormatException e) {
            return;
        }

        final Integer oldValue = getPropertyValue();
        changed(value, oldValue);
    }
}
