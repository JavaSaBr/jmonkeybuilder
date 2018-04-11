package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.plugin.api.property.PropertyDefinition;
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
    @FxThread
    protected void createComponents() {
        super.createComponents();

        checkBox = new CheckBox();
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> change());
        checkBox.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FXUtils.addToPane(checkBox, this);
        FXUtils.addClassTo(checkBox, CssClasses.ABSTRACT_PARAM_CONTROL_CHECK_BOX);
    }

    /**
     * @return the CheckBox with current value.
     */
    @FxThread
    private @NotNull CheckBox getCheckBox() {
        return notNull(checkBox);
    }

    @Override
    @FxThread
    public void reload() {
        super.reload();
        final Boolean value = getPropertyValue();
        getCheckBox().setSelected(value == null ? false : value);
    }

    @Override
    @FxThread
    protected void changeImpl() {
        setPropertyValue(getCheckBox().isSelected());
        super.changeImpl();
    }
}
