package com.ss.editor.ui.dialog.factory.control;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.factory.PropertyDefinition;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.VarTable;
import javafx.scene.control.CheckBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The control to edit boolean values.
 *
 * @author JavaSaBr
 */
public class BooleanPropertyEditorControl extends PropertyEditorControl<Boolean> {

    /**
     * The {@link CheckBox} with current value.
     */
    @Nullable
    private CheckBox checkBox;

    protected BooleanPropertyEditorControl(@NotNull final VarTable vars, @NotNull final PropertyDefinition definition,
                                           @NotNull final Runnable validationCallback) {
        super(vars, definition, validationCallback);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        checkBox = new CheckBox();
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> change());
        checkBox.prefWidthProperty().bind(widthProperty().multiply(0.5F));

        FXUtils.addToPane(checkBox, this);
        FXUtils.addClassTo(checkBox, CSSClasses.ABSTRACT_PARAM_CONTROL_CHECK_BOX);
    }

    @NotNull
    private CheckBox getCheckBox() {
        return notNull(checkBox);
    }

    @Override
    protected void reload() {
        super.reload();
        final Boolean value = getPropertyValue();
        getCheckBox().setSelected(value == null ? false : value);
    }

    @Override
    protected void change() {
        setPropertyValue(getCheckBox().isSelected());
        super.change();
    }
}
