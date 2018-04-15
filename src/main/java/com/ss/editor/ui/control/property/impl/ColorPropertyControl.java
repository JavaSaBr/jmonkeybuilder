package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.math.ColorRGBA;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit a color values.
 *
 * @param <C> the change
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class ColorPropertyControl<C extends ChangeConsumer, T> extends PropertyControl<C, T, ColorRGBA> {

    /**
     * The color picker.
     */
    @Nullable
    private ColorPicker colorPicker;

    public ColorPropertyControl(
            @Nullable ColorRGBA propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull HBox container) {
        super.createComponents(container);

        colorPicker = new ColorPicker();
        colorPicker.prefWidthProperty()
            .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FxControlUtils.onColorChange(colorPicker, this::updateValue);

        FxUtils.addChild(container, colorPicker);
        FxUtils.addClass(colorPicker, CssClasses.ABSTRACT_PARAM_CONTROL_COLOR_PICKER);
    }

    @Override
    @FxThread
    public void changeControlWidthPercent(double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        var width = getColorPicker().prefWidthProperty();
        width.unbind();
        width.bind(widthProperty().multiply(controlWidthPercent));
    }

    @Override
    @FxThread
    protected void setPropertyValue(@Nullable ColorRGBA color) {
        super.setPropertyValue(color == null ? null : color.clone());
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * Get the color picker.
     *
     * @return the color picker.
     */
    @FxThread
    private @NotNull ColorPicker getColorPicker() {
        return notNull(colorPicker);
    }

    @Override
    @FxThread
    protected void reload() {
        getColorPicker().setValue(UiUtils.from(getPropertyValue()));
    }

    /**
     * Updating value.
     */
    @FxThread
    private void updateValue() {

        if (isIgnoreListener()) {
            return;
        }

        var colorPicker = getColorPicker();
        var newColor = UiUtils.from(colorPicker.getValue());
        var oldValue = getPropertyValue();

        changed(newColor, oldValue == null ? null : oldValue.clone());
    }
}
