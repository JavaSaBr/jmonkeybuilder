package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.ComboBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The combo box based property control.
 *
 * @author JavaSaBr
 */
public class ComboBoxPropertyEditorControl<T> extends PropertyEditorControl<T> {

    /**
     * The list of available options.
     */
    @Nullable
    private ComboBox<T> comboBox;

    protected ComboBoxPropertyEditorControl(
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

        comboBox = new ComboBox<>();
        comboBox.setVisibleRowCount(20);
        comboBox.prefWidthProperty()
                .bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FxControlUtils.onSelectedItemChange(comboBox, this::change);

        FxUtils.addClass(comboBox, CssClasses.PROPERTY_CONTROL_COMBO_BOX);
        FxUtils.addChild(this, comboBox);
    }

    /**
     * Get the list of available options.
     *
     * @return the list of available options.
     */
    @FxThread
    protected @NotNull ComboBox<T> getComboBox() {
        return notNull(comboBox);
    }

    @Override
    @FxThread
    public void reload() {
        super.reload();
        getComboBox().getSelectionModel()
                .select(getPropertyValue());
    }

    @Override
    @FxThread
    protected void changeImpl() {
        var comboBox = getComboBox();
        var selectionModel = comboBox.getSelectionModel();
        setPropertyValue(selectionModel.getSelectedItem());
        super.changeImpl();
    }
}
