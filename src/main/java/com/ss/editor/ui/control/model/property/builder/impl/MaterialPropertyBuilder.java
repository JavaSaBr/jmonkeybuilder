package com.ss.editor.ui.control.model.property.builder.impl;

import static java.util.stream.Collectors.toList;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.shader.VarType;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.extension.property.SimpleProperty;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.EditableObjectPropertyBuilder;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link com.jme3.material.Material} objects.
 *
 * @author JavaSaBr
 */
public class MaterialPropertyBuilder extends EditableObjectPropertyBuilder {

    @NotNull
    private static final PropertyBuilder INSTANCE = new MaterialPropertyBuilder();

    @NotNull
    private static final ObjectDictionary<VarType, Integer> SIZE_MAP = DictionaryFactory.newObjectDictionary();

    static {

        for (final VarType varType : VarType.values()) {
            SIZE_MAP.put(varType, 0);
        }

        SIZE_MAP.put(VarType.Texture2D, -1);
        SIZE_MAP.put(VarType.Vector3, -2);
        SIZE_MAP.put(VarType.Boolean, 3);
        SIZE_MAP.put(VarType.Float, 2);
        SIZE_MAP.put(VarType.Int, 2);
    }

    @NotNull
    private static final Comparator<MatParam> MAT_PARAM_COMPARATOR = (first, second) -> {
        final VarType firstType = first.getVarType();
        final VarType secondType = second.getVarType();
        return SIZE_MAP.get(secondType) - SIZE_MAP.get(firstType);
    };

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private MaterialPropertyBuilder() {
        super(ChangeConsumer.class);
    }

    @Override
    protected @Nullable List<EditableProperty<?, ?>> getProperties(final @NotNull Object object) {
        if(!(object instanceof Material)) return null;

        final Material material = (Material) object;
        final MaterialDef definition = material.getMaterialDef();

        final Collection<MatParam> materialParams = definition.getMaterialParams();
        return materialParams.stream()
                .sorted(MAT_PARAM_COMPARATOR)
                .map(param -> convert(param, material))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private @Nullable EditableProperty<?, Material> convert(@NotNull final MatParam param, @NotNull final Material material) {

        final EditablePropertyType propertyType = convert(param.getVarType());
        if (propertyType == null) {
            return null;
        }

        return new SimpleProperty<>(propertyType, param.getName(), 0.1F, material,
                object -> getParamValue(param, object),
                (object, newValue) -> applyParam(param, object, newValue));
    }

    private void applyParam(@NotNull final MatParam param, @NotNull final Material object,
                            @Nullable final Object newValue) {
        if (newValue == null) {
            object.clearParam(param.getName());
        } else {
            object.setParam(param.getName(), param.getVarType(), newValue);
        }
    }

    private static @Nullable Object getParamValue(@NotNull final MatParam param, @NotNull final Material material) {
        final MatParam currentParam = material.getParam(param.getName());
        return currentParam == null ? null : currentParam.getValue();
    }

    private @Nullable EditablePropertyType convert(@NotNull final VarType varType) {

        switch (varType) {
            case Boolean: return EditablePropertyType.BOOLEAN;
            case Float: return EditablePropertyType.FLOAT;
            case Int: return EditablePropertyType.INTEGER;
            case Vector3: return EditablePropertyType.VECTOR_3F;
            case Texture2D: return EditablePropertyType.TEXTURE_2D;
            case Vector2: return EditablePropertyType.VECTOR_2F;
        }

        return null;
    }
}
