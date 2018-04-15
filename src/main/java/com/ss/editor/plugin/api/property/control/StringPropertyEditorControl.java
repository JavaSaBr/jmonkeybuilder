package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.common.util.VarTable;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The control to edit string values.
 *
 * @author JavaSaBr
 */
public class StringPropertyEditorControl extends PropertyEditorControl<String> {

    /**
     * The value field.
     */
    @Nullable
    private TextField valueField;

    protected StringPropertyEditorControl(@NotNull final VarTable vars, @NotNull final PropertyDefinition definition,
                                          @NotNull final Runnable validationCallback) {
        super(vars, definition, validationCallback);
    }

    @Override
    @FxThread
    protected void createComponents() {
        super.createComponents();

        valueField = new TextField();
        valueField.textProperty().addListener((observable, oldValue, newValue) -> change());
        valueField.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FXUtils.addClassTo(valueField, CssClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addToPane(valueField, this);
    }

    /**
     * @return the value field.
     */
    @FxThread
    private @NotNull TextField getValueField() {
        return notNull(valueField);
    }

    @Override
    @FxThread
    public void reload() {
        super.reload();
        final String value = getPropertyValue();
        getValueField().setText(value == null ? "" : value);
    }

    @Override
    @FxThread
    protected void changeImpl() {
        setPropertyValue(getValueField().getText());
        super.changeImpl();
    }
}
