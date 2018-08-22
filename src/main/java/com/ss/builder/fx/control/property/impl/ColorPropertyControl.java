package com.ss.builder.fx.control.property.impl;

import com.jme3.math.ColorRGBA;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.util.UiUtils;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.control.property.PropertyControl;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.util.UiUtils;
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
    @NotNull
    private final ColorPicker colorPicker;

    public ColorPropertyControl(
            @Nullable ColorRGBA propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
        this.colorPicker = new ColorPicker();
    }

    @Override
    @FxThread
    protected void createControls(@NotNull HBox container) {
        super.createControls(container);

        colorPicker.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FxControlUtils.onColorChange(colorPicker, this::updateValue);

        FxUtils.addClass(colorPicker,
                CssClasses.PROPERTY_CONTROL_COLOR_PICKER);

        FxUtils.addChild(container, colorPicker);
    }

    @Override
    @FxThread
    public void changeControlWidthPercent(double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        FxUtils.rebindPrefWidth(colorPicker,
                widthProperty().multiply(controlWidthPercent));
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

    @Override
    @FxThread
    protected void reloadImpl() {
        colorPicker.setValue(UiUtils.from(getPropertyValue()));
        super.reloadImpl();
    }

    /**
     * Updating value.
     */
    @FxThread
    private void updateValue() {

        if (isIgnoreListener()) {
            return;
        }

        var newColor = UiUtils.from(colorPicker.getValue());
        var oldValue = getPropertyValue();

        changed(newColor, oldValue == null ? null : oldValue.clone());
    }
}
