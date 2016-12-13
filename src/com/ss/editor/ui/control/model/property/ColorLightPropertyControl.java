package com.ss.editor.ui.control.model.property;

import static java.lang.Math.min;

import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.operation.LightPropertyOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.UIUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link ModelPropertyControl} for editing a color of the {@link Light}.
 *
 * @author JavaSaBr
 */
public class ColorLightPropertyControl<T extends Light> extends ModelPropertyControl<T, ColorRGBA> {

    /**
     * The color picker.
     */
    private ColorPicker colorPicker;

    public ColorLightPropertyControl(final ColorRGBA element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        colorPicker = new ColorPicker();
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> updateValue());
        colorPicker.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addClassTo(colorPicker, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addToPane(colorPicker, container);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * @return the color picker.
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
     * Updating value.
     */
    private void updateValue() {
        if (isIgnoreListener()) return;

        final ColorPicker colorPicker = getColorPicker();
        final ColorRGBA newColor = UIUtils.convertColor(colorPicker.getValue());
        final ColorRGBA oldValue = getPropertyValue();

        changed(newColor, oldValue.clone());
    }

    @Override
    protected void changed(@Nullable final ColorRGBA newValue, @Nullable final ColorRGBA oldValue) {

        final T editObject = getEditObject();

        final LightPropertyOperation<T, ColorRGBA> operation = new LightPropertyOperation<>(editObject, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(getApplyHandler());

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }
}
