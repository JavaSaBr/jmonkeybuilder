package com.ss.editor.plugin.api.property.control;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.control.input.TypedTextField;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import org.jetbrains.annotations.NotNull;

/**
 * The typed text field base property control.
 *
 * @author JavaSaBr
 */
public abstract class TypedTextFieldPropertyEditorControl<T, F extends TypedTextField<T>>
        extends PropertyEditorControl<T> {

    /**
     * The value field.
     */
    @NotNull
    protected final F valueField;

    protected TypedTextFieldPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
        this.valueField = createField();
    }

    @Override
    @FxThread
    public void postConstruct() {
        super.postConstruct();

        valueField.prefWidthProperty()
                .bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FxControlUtils.onTextChange(valueField, this::change);

        FxUtils.addClass(valueField,
                CssClasses.PROPERTY_CONTROL_COMBO_BOX);

        FxUtils.addChild(this, valueField);
    }

    /**
     * Create a new field.
     *
     * @return the new field.
     */
    @FxThread
    protected abstract @NotNull F createField();
}
