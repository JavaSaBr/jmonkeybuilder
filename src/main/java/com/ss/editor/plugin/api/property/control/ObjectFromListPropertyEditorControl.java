package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The control to choose object value from list.
 *
 * @author JavaSaBr
 */
public class ObjectFromListPropertyEditorControl extends PropertyEditorControl<Object> {

    /**
     * The list of available options.
     */
    @Nullable
    private ComboBox<Object> comboBox;

    protected ObjectFromListPropertyEditorControl(@NotNull final VarTable vars, @NotNull final PropertyDefinition definition,
                                                  @NotNull final Runnable validationCallback, @NotNull final Array<?> options) {
        super(vars, definition, validationCallback);
        final ComboBox<Object> comboBox = getComboBox();
        options.forEach(comboBox.getItems(), (option, items) -> items.add(option.toString()));
    }

    @Override
    @FxThread
    protected void createComponents() {
        super.createComponents();

        comboBox = new ComboBox<>();
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> change());
        comboBox.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));
        comboBox.setVisibleRowCount(20);

        FXUtils.addClassTo(comboBox, CssClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addToPane(comboBox, this);
    }

    /**
     * Get the list of available options .
     *
     * @return the list of available options .
     */
    @FxThread
    private @NotNull ComboBox<Object> getComboBox() {
        return notNull(comboBox);
    }

    @Override
    @FxThread
    public void reload() {
        super.reload();
        final Object value = getPropertyValue();
        final ComboBox<Object> comboBox = getComboBox();
        comboBox.getSelectionModel().select(value);
    }

    @Override
    @FxThread
    protected void changeImpl() {
        final ComboBox<Object> comboBox = getComboBox();
        final SingleSelectionModel<Object> selectionModel = comboBox.getSelectionModel();
        setPropertyValue(selectionModel.getSelectedItem());
        super.changeImpl();
    }
}
