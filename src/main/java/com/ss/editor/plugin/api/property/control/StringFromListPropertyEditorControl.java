package com.ss.editor.plugin.api.property.control;

import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.SimpleStringSuggestionProvider;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import org.jetbrains.annotations.NotNull;

/**
 * The control to choose string value from list.
 *
 * @author JavaSaBr
 */
public class StringFromListPropertyEditorControl extends ComboBoxPropertyEditorControl<String> {

    public StringFromListPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback,
            @NotNull Array<?> options
    ) {
        super(vars, definition, validationCallback);

        var comboBox = getComboBox();
        options.forEach(comboBox.getItems(),
                (option, items) -> items.add(option.toString()));

        if (options.size() > comboBox.getVisibleRowCount()) {
            setIgnoreListener(true);
            try {

                comboBox.setEditable(true);

                var selectionModel = comboBox.getSelectionModel();
                var editor = comboBox.getEditor();

                var binding = new AutoCompletionTextFieldBinding<String>(editor,
                        new SimpleStringSuggestionProvider(comboBox.getItems()));
                binding.setOnAutoCompleted(event -> selectionModel.select(event.getCompletion()));
                binding.prefWidthProperty().bind(comboBox.widthProperty().multiply(1.3));

                FxControlUtils.onSelectedItemChange(comboBox, newValue -> {
                    var executorManager = ExecutorManager.getInstance();
                    executorManager.addFxTask(() -> editor.positionCaret(newValue.length()));
                });

                FxUtils.addClass(editor,
                        CssClasses.TRANSPARENT_TEXT_FIELD,
                        CssClasses.TEXT_FIELD_IN_COMBO_BOX);

                reload();

            } finally {
                setIgnoreListener(false);
            }
        }
    }
}
