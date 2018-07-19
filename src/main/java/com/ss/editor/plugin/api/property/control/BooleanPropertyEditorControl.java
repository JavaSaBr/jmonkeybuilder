package com.ss.editor.plugin.api.property.control;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.CheckBox;
import org.jetbrains.annotations.NotNull;

/**
 * The control to edit boolean values.
 *
 * @author JavaSaBr
 */
public class BooleanPropertyEditorControl extends PropertyEditorControl<Boolean> {

    /**
     * The {@link CheckBox} with current value.
     */
    @NotNull
    private final CheckBox checkBox;

    protected BooleanPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
        this.checkBox = new CheckBox();
    }

    @Override
    @FxThread
    public void postConstruct() {
        super.postConstruct();

        checkBox.prefWidthProperty()
                .bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FxControlUtils.onSelectedChange(checkBox, this::changed);

        FxUtils.addClass(checkBox, CssClasses.PROPERTY_CONTROL_CHECK_BOX);
        FxUtils.addChild(this, checkBox);
    }

    @Override
    @FxThread
    protected void reloadImpl() {
        checkBox.setSelected(Boolean.TRUE.equals(getPropertyValue()));
        super.reloadImpl();
    }

    @Override
    @FxThread
    protected void changedImpl() {
        setPropertyValue(checkBox.isSelected());
        super.changedImpl();
    }
}
