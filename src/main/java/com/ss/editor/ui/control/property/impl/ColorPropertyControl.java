package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.ColorRGBA;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit a color values.
 *
 * @param <C> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class ColorPropertyControl<C extends ChangeConsumer, T> extends PropertyControl<C, T, ColorRGBA> {

    /**
     * The color picker.
     */
    @Nullable
    private ColorPicker colorPicker;

    public ColorPropertyControl(@Nullable final ColorRGBA propertyValue, @NotNull final String propertyName,
                                @NotNull final C changeConsumer) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        colorPicker = new ColorPicker();
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> updateValue());
        colorPicker.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addToPane(colorPicker, container);
        FXUtils.addClassTo(colorPicker, CSSClasses.ABSTRACT_PARAM_CONTROL_COLOR_PICKER);
    }

    @Override
    @FxThread
    public void changeControlWidthPercent(final double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        final ColorPicker colorPicker = getColorPicker();
        colorPicker.prefWidthProperty().unbind();
        colorPicker.prefWidthProperty().bind(widthProperty().multiply(controlWidthPercent));
    }

    @Override
    @FxThread
    protected void setPropertyValue(@Nullable final ColorRGBA color) {
        super.setPropertyValue(color == null ? null : color.clone());
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * @return the color picker.
     */
    @FxThread
    private @NotNull ColorPicker getColorPicker() {
        return notNull(colorPicker);
    }

    @Override
    @FxThread
    protected void reload() {
        final ColorPicker colorPicker = getColorPicker();
        colorPicker.setValue(UIUtils.from(getPropertyValue()));
    }

    /**
     * Updating value.
     */
    @FxThread
    private void updateValue() {
        if (isIgnoreListener()) return;

        final ColorPicker colorPicker = getColorPicker();
        final ColorRGBA newColor = UIUtils.from(colorPicker.getValue());
        final ColorRGBA oldValue = getPropertyValue();

        changed(newColor, oldValue == null ? null : oldValue.clone());
    }
}
