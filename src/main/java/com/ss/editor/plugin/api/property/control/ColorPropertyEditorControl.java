package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.math.ColorRGBA;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
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

    protected ColorPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
    }

    @Override
    @FxThread
    protected void postConstruct() {
        super.postConstruct();

        colorPicker = new ColorPicker();
        colorPicker.prefWidthProperty()
                .bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FxControlUtils.onColorChange(colorPicker, this::change);

        FxUtils.addClass(colorPicker, CssClasses.PROPERTY_CONTROL_COLOR_PICKER);
        FxUtils.addChild(this, colorPicker);
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
    public void reload() {
        super.reload();
        getColorPicker().setValue(UiUtils.from(getPropertyValue()));
    }

    @Override
    @FxThread
    protected void changeImpl() {
        var colorPicker = getColorPicker();
        setPropertyValue(UiUtils.from(colorPicker.getValue()));
        super.changeImpl();
    }
}
