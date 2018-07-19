package com.ss.editor.plugin.api.property.control;

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

/**
 * The control to edit color values.
 *
 * @author JavaSaBr
 */
public class ColorPropertyEditorControl extends PropertyEditorControl<ColorRGBA> {

    /**
     * The color picker.
     */
    @NotNull
    private final ColorPicker colorPicker;

    protected ColorPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
        this.colorPicker = new ColorPicker();
    }

    @Override
    @FxThread
    public void postConstruct() {
        super.postConstruct();

        colorPicker.prefWidthProperty()
                .bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FxControlUtils.onColorChange(colorPicker, this::changed);

        FxUtils.addClass(colorPicker,
                CssClasses.PROPERTY_CONTROL_COLOR_PICKER);

        FxUtils.addChild(this, colorPicker);
    }

    @Override
    protected void reloadImpl() {
        colorPicker.setValue(UiUtils.from(getPropertyValue()));
        super.reloadImpl();
    }

    @Override
    @FxThread
    protected void changedImpl() {
        setPropertyValue(UiUtils.from(colorPicker.getValue()));
        super.changedImpl();
    }
}
