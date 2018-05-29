package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
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

    protected BooleanPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
    }

    @Override
    @FxThread
    protected void createComponents() {
        super.createComponents();

        checkBox = new CheckBox();
        checkBox.prefWidthProperty()
                .bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FxControlUtils.onSelectedChange(checkBox, this::change);

        FxUtils.addClass(checkBox, CssClasses.PROPERTY_CONTROL_CHECK_BOX);
        FxUtils.addChild(this, checkBox);
    }

    /**
     * Get the check box with current value.
     *
     * @return the check box with current value.
     */
    @FxThread
    private @NotNull CheckBox getCheckBox() {
        return notNull(checkBox);
    }

    @Override
    @FxThread
    public void reload() {
        super.reload();
        getCheckBox().setSelected(Boolean.TRUE.equals(getPropertyValue()));
    }

    @Override
    @FxThread
    protected void changeImpl() {
        setPropertyValue(getCheckBox().isSelected());
        super.changeImpl();
    }
}
