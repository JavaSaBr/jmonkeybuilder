package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.ColorRGBA;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.VarTable;
import javafx.scene.control.ColorPicker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The control to edit color values.
 *
 * @author JavaSaBr
 */
public class ColorPropertyEditorControl extends PropertyEditorControl<ColorRGBA> {

    /**
     * The color picker.
     */
    @Nullable
    private ColorPicker colorPicker;

    protected ColorPropertyEditorControl(@NotNull final VarTable vars, @NotNull final PropertyDefinition definition,
                                         @NotNull final Runnable validationCallback) {
        super(vars, definition, validationCallback);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        colorPicker = new ColorPicker();
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> change());
        colorPicker.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FXUtils.addClassTo(colorPicker, CSSClasses.ABSTRACT_PARAM_CONTROL_COLOR_PICKER);
        FXUtils.addToPane(colorPicker, this);
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
        super.reload();
        final ColorPicker colorPicker = getColorPicker();
        colorPicker.setValue(UIUtils.from(getPropertyValue()));
    }

    @Override
    protected void change() {
        final ColorPicker colorPicker = getColorPicker();
        setPropertyValue(UIUtils.from(colorPicker.getValue()));
        super.change();
    }
}
