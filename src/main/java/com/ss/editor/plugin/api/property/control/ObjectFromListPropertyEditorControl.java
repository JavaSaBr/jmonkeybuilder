package com.ss.editor.plugin.api.property.control;

import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;

/**
 * The control to choose object value from list.
 *
 * @author JavaSaBr
 */
public class ObjectFromListPropertyEditorControl extends ComboBoxPropertyEditorControl<Object> {

    public ObjectFromListPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback,
            @NotNull Array<?> options
    ) {
        super(vars, definition, validationCallback);
        var comboBox = getComboBox();
        options.forEach(comboBox.getItems(),
                (option, items) -> items.add(option));
    }
}
