package com.ss.editor.plugin.api.property.control;

import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.fx.util.FxUtils;
import org.controlsfx.control.textfield.TextFields;
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

                //FIXME need to find more userfriendly control
                comboBox.setEditable(true);

                var editor = comboBox.getEditor();
                var selectionModel = comboBox.getSelectionModel();
                var binding = TextFields.bindAutoCompletion(editor, comboBox.getItems());
                binding.setOnAutoCompleted(event -> selectionModel.select(event.getCompletion()));

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
