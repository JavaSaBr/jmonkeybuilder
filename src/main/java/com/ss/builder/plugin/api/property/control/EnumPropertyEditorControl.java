package com.ss.builder.plugin.api.property.control;

import com.ss.builder.util.EditorUtils;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.common.util.VarTable;
import org.jetbrains.annotations.NotNull;

/**
 * The property control to edit enum values.
 *
 * @author JavaSaBr
 */
public class EnumPropertyEditorControl<T extends Enum<T>> extends ComboBoxPropertyEditorControl<T> {

    protected EnumPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);

        var defaultValue = ClassUtils.<T>unsafeCast(definition.requireDefaultValue());
        var enumConstants = EditorUtils.<T>getEnumValues(defaultValue.getClass());

        comboBox.getItems()
                .addAll(enumConstants);
    }
}
