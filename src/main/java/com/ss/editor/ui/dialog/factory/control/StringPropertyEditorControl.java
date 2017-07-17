package com.ss.editor.ui.dialog.factory.control;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.factory.PropertyDefinition;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.VarTable;
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
    protected void createComponents() {
        super.createComponents();

        valueField = new TextField();
        valueField.setOnKeyReleased(UIUtils::consumeIfIsNotHotKey);
        valueField.textProperty().addListener((observable, oldValue, newValue) -> change());
        valueField.prefWidthProperty().bind(widthProperty().multiply(0.5F));

        FXUtils.addClassTo(valueField, CSSClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addToPane(valueField, this);
    }

    @NotNull
    private TextField getValueField() {
        return notNull(valueField);
    }

    @Override
    protected void reload() {
        super.reload();
        final String value = getPropertyValue();
        getValueField().setText(value == null ? "" : value);
    }

    @Override
    protected void change() {
        setPropertyValue(getValueField().getText());
        super.change();
    }
}
