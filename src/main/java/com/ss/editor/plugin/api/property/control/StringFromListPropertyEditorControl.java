package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The control to choose string value from list.
 *
 * @author JavaSaBr
 */
public class StringFromListPropertyEditorControl extends PropertyEditorControl<String> {

    /**
     * The list of available options of the string value.
     */
    @Nullable
    private ComboBox<String> comboBox;

    protected StringFromListPropertyEditorControl(@NotNull final VarTable vars, @NotNull final PropertyDefinition definition,
                                                  @NotNull final Runnable validationCallback, @NotNull final Array<?> options) {
        super(vars, definition, validationCallback);

        final ComboBox<String> comboBox = getComboBox();
        options.forEach(comboBox.getItems(), (option, items) -> items.add(option.toString()));

        if (options.size() > comboBox.getVisibleRowCount()) {
            setIgnoreListener(true);
            try {

                //FIXME need to find more userfriendly control
                comboBox.setEditable(true);

                final TextField editor = comboBox.getEditor();
                final SingleSelectionModel<String> selectionModel = comboBox.getSelectionModel();
                final AutoCompletionBinding<String> binding = TextFields.bindAutoCompletion(editor, comboBox.getItems());
                binding.setOnAutoCompleted(event -> selectionModel.select(event.getCompletion()));

                FXUtils.addClassesTo(editor, CssClasses.TRANSPARENT_TEXT_FIELD,
                        CssClasses.TEXT_FIELD_IN_COMBO_BOX);

                reload();

            } finally {
                setIgnoreListener(false);
            }
        }
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
     * @return The list of available options of the string value.
     */
    @FxThread
    private @NotNull ComboBox<String> getComboBox() {
        return notNull(comboBox);
    }

    @Override
    @FxThread
    public void reload() {
        super.reload();
        final String value = getPropertyValue();
        final ComboBox<String> comboBox = getComboBox();
        comboBox.getSelectionModel().select(value);
    }

    @Override
    @FxThread
    protected void changeImpl() {
        final ComboBox<String> comboBox = getComboBox();
        final SingleSelectionModel<String> selectionModel = comboBox.getSelectionModel();
        setPropertyValue(selectionModel.getSelectedItem());
        super.changeImpl();
    }
}
