package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.util.EditorUtil;
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

        var defaultValue = ClassUtils.<T>unsafeCast(notNull(definition.getDefaultValue()));
        var enumConstants = EditorUtil.<T>getEnumValues(defaultValue.getClass());

        getComboBox().getItems()
                .addAll(enumConstants);
    }
}
