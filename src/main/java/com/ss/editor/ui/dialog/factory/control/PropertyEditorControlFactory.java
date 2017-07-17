package com.ss.editor.ui.dialog.factory.control;

import com.ss.editor.ui.dialog.factory.PropertyDefinition;
import com.ss.rlib.util.VarTable;
import org.jetbrains.annotations.NotNull;

/**
 * The factory to create editable controls for a definition.
 *
 * @author JavaSaBr
 */
public class PropertyEditorControlFactory {

    @NotNull
    public static PropertyEditorControl<?> build(@NotNull final VarTable vars,
                                                 @NotNull final PropertyDefinition definition,
                                                 @NotNull final Runnable validation) {

        switch (definition.getPropertyType()) {
            case FLOAT: return new FloatPropertyEditorControl(vars, definition, validation);
            case BOOLEAN: return new BooleanPropertyEditorControl(vars, definition, validation);
            case INTEGER: return new IntegerPropertyEditorControl(vars, definition, validation);
            case VECTOR_3F: return new Vector3fPropertyEditorControl(vars, definition, validation);
            case ENUM: return new EnumPropertyEditorControl<>(vars, definition, validation);
            case STRING: return new StringPropertyEditorControl(vars, definition, validation);
            case GEOMETRY_FROM_ASSET_FOLDER: return new GeometryAssetResourcePropertyControl(vars, definition, validation);
            default:
                throw new IllegalArgumentException("Unknown the type " + definition.getPropertyType());
        }
    }
}
