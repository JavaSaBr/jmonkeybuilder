package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.math.ColorRGBA;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.common.util.VarTable;
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
    @FxThread
    protected void createComponents() {
        super.createComponents();

        colorPicker = new ColorPicker();
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> change());
        colorPicker.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FXUtils.addClassTo(colorPicker, CssClasses.ABSTRACT_PARAM_CONTROL_COLOR_PICKER);
        FXUtils.addToPane(colorPicker, this);
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
    public void reload() {
        super.reload();
        final ColorPicker colorPicker = getColorPicker();
        colorPicker.setValue(UiUtils.from(getPropertyValue()));
    }

    @Override
    @FxThread
    protected void changeImpl() {
        final ColorPicker colorPicker = getColorPicker();
        setPropertyValue(UiUtils.from(colorPicker.getValue()));
        super.changeImpl();
    }
}
