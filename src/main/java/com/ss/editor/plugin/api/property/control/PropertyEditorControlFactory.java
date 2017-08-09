package com.ss.editor.plugin.api.property.control;

import com.ss.editor.plugin.api.property.PropertyDefinition;
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
            case FLOAT: {
                final FloatPropertyEditorControl control = new FloatPropertyEditorControl(vars, definition, validation);
                control.setMinMax(definition.getMin(), definition.getMax());
                return control;
            }
            case COLOR: return new ColorPropertyEditorControl(vars, definition, validation);
            case BOOLEAN: return new BooleanPropertyEditorControl(vars, definition, validation);
            case INTEGER: {
                final IntegerPropertyEditorControl control = new IntegerPropertyEditorControl(vars, definition, validation);
                control.setMinMax(definition.getMin(), definition.getMax());
                return control;
            }
            case VECTOR_3F: return new Vector3fPropertyEditorControl(vars, definition, validation);
            case ENUM: return new EnumPropertyEditorControl<>(vars, definition, validation);
            case STRING: return new StringPropertyEditorControl(vars, definition, validation);
            case GEOMETRY_FROM_ASSET_FOLDER: return new GeometryAssetResourcePropertyControl(vars, definition, validation);
            case STRING_FROM_LIST: return new StringFromListPropertyEditorControl(vars, definition, validation, definition.getOptions());
            case AWT_FONT: return new AwtFontPropertyEditorControl(vars, definition, validation);
            default: throw new IllegalArgumentException("Unknown the type " + definition.getPropertyType());
        }
    }
}
