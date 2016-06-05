package com.ss.editor.ui.control.model.property;

import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.operation.LightPropertyOperation;
import com.ss.editor.ui.util.UIUtils;

import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import rlib.ui.util.FXUtils;

import static java.lang.Math.min;

/**
 * Реализация контрола по редактированию цвета источника света.
 *
 * @author Ronn
 */
public class ColorLightPropertyControl<T extends Light> extends ModelPropertyControl<T, ColorRGBA> {

    /**
     * Контрол для выбора цвета.
     */
    private ColorPicker colorPicker;

    public ColorLightPropertyControl(final ColorRGBA element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(final HBox container) {
        super.createComponents(container);

        colorPicker = new ColorPicker();
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> updateValue());

        FXUtils.addToPane(colorPicker, container);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * @return контрол для выбора цвета.
     */
    private ColorPicker getColorPicker() {
        return colorPicker;
    }

    @Override
    protected void reload() {

        final ColorRGBA element = getPropertyValue();

        final float red = min(element.getRed(), 1F);
        final float green = min(element.getGreen(), 1F);
        final float blue = min(element.getBlue(), 1F);
        final float alpha = min(element.getAlpha(), 1F);

        final ColorPicker colorPicker = getColorPicker();
        colorPicker.setValue(new Color(red, green, blue, alpha));
    }

    /**
     * Обновление вектора.
     */
    private void updateValue() {
        if (isIgnoreListener()) return;

        final ColorPicker colorPicker = getColorPicker();
        final ColorRGBA newColor = UIUtils.convertColor(colorPicker.getValue());
        final ColorRGBA oldValue = getPropertyValue();

        changed(newColor, oldValue.clone());
    }

    @Override
    protected void changed(final ColorRGBA newValue, final ColorRGBA oldValue) {

        final T editObject = getEditObject();

        final LightPropertyOperation<T, ColorRGBA> operation = new LightPropertyOperation<>(editObject, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }
}
