package com.ss.editor.ui.control.model.property;

import com.jme3.light.Light;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.operation.LightPropertyOperation;
import com.ss.editor.ui.css.CSSIds;

import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

import static java.lang.Float.parseFloat;

/**
 * Реализация контрола по редактированию какого-то числового параметра источника света.
 *
 * @author Ronn
 */
public class FloatLightPropertyControl<T extends Light> extends ModelPropertyControl<T, Float> {

    /**
     * Поле со значением.
     */
    private TextField valueField;

    /**
     * Сила влияние колесика мышки.
     */
    private float scrollIncrement;

    public FloatLightPropertyControl(final Float element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
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
     * @param scrollIncrement сила влияние колесика мышки.
     */
    public void setScrollIncrement(float scrollIncrement) {
        this.scrollIncrement = scrollIncrement;
    }

    /**
     * @return сила влияние колесика мышки.
     */
    private float getScrollIncrement() {
        return scrollIncrement;
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * Процесс скролирования значения.
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
     * @return поле со значением.
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
     * Обновление вектора.
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

    @Override
    protected void changed(final Float newValue, final Float oldValue) {

        final T editObject = getEditObject();

        final LightPropertyOperation<T, Float> operation = new LightPropertyOperation<>(editObject, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }
}
