package com.ss.editor.plugin.api.property.control;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

/**
 * The control to edit string values.
 *
 * @author JavaSaBr
 */
public class StringPropertyEditorControl extends PropertyEditorControl<String> {

    /**
     * The value field.
     */
    @NotNull
    private final TextField valueField;

    protected StringPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
        this.valueField = new TextField();
    }

    @Override
    @FxThread
    public void postConstruct() {
        super.postConstruct();

        valueField.prefWidthProperty()
                .bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FxControlUtils.onTextChange(valueField, this::changed);

        FxUtils.addClass(valueField,
                CssClasses.PROPERTY_CONTROL_COMBO_BOX);

        FxUtils.addChild(this, valueField);
    }

    @Override
    @FxThread
    protected void reloadImpl() {

        valueField.setText(getPropertyValueOpt()
                .orElse(""));

        super.reloadImpl();
    }

    @Override
    @FxThread
    protected void changedImpl() {
        setPropertyValue(valueField.getText());
        super.changedImpl();
    }
}
