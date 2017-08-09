package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.ColorRGBA;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit a color values.
 *
 * @param <C> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractColorPropertyControl<C extends ChangeConsumer, T> extends AbstractPropertyControl<C, T, ColorRGBA> {

    /**
     * The color picker.
     */
    @Nullable
    private ColorPicker colorPicker;

    /**
     * Instantiates a new Abstract color property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     * @param changeHandler  the change handler
     */
    public AbstractColorPropertyControl(@Nullable final ColorRGBA propertyValue, @NotNull final String propertyName,
                                        @NotNull final C changeConsumer,
                                        @NotNull final SixObjectConsumer<C, T, String, ColorRGBA, ColorRGBA, BiConsumer<T, ColorRGBA>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        colorPicker = new ColorPicker();
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> updateValue());
        colorPicker.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addToPane(colorPicker, container);
        FXUtils.addClassTo(colorPicker, CSSClasses.ABSTRACT_PARAM_CONTROL_COLOR_PICKER);
    }

    @Override
    protected void setPropertyValue(@Nullable final ColorRGBA color) {
        super.setPropertyValue(color == null ? null : color.clone());
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * @return the color picker.
     */
    @NotNull
    private ColorPicker getColorPicker() {
        return notNull(colorPicker);
    }

    @Override
    protected void reload() {
        final ColorPicker colorPicker = getColorPicker();
        colorPicker.setValue(UIUtils.from(getPropertyValue()));
    }

    /**
     * Updating value.
     */
    private void updateValue() {
        if (isIgnoreListener()) return;

        final ColorPicker colorPicker = getColorPicker();
        final ColorRGBA newColor = UIUtils.from(colorPicker.getValue());
        final ColorRGBA oldValue = getPropertyValue();

        changed(newColor, oldValue == null ? null : oldValue.clone());
    }
}
