package com.ss.editor.ui.control.model.property.builder.impl;

import static java.util.stream.Collectors.toList;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.shader.VarType;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
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
    protected static final Comparator<MatParam> MAT_PARAM_COMPARATOR = (first, second) -> {
        final VarType firstType = first.getVarType();
        final VarType secondType = second.getVarType();
        return SIZE_MAP.get(secondType) - SIZE_MAP.get(firstType);
    };

    /**
     * Get the single instance.
     *
     * @return the single instance
     */
    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    protected MaterialPropertyBuilder() {
        super(ChangeConsumer.class);
    }

    @Override
    @FXThread
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull final Object object) {
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

    /**
     * Convert the material parameter to editable property.
     *
     * @param param    the material parameter.
     * @param material the material.
     * @return the editable property or null.
     */
    @FXThread
    private @Nullable EditableProperty<?, Material> convert(@NotNull final MatParam param,
                                                            @NotNull final Material material) {

        final EditablePropertyType propertyType = convert(param.getVarType());
        if (propertyType == null) {
            return null;
        }

        return new SimpleProperty<>(propertyType, param.getName(), 0.1F, material,
                object -> getParamValue(param, object),
                (object, newValue) -> applyParam(param, object, newValue));
    }

    /**
     * Apply changes for the material parameter.
     *
     * @param param    the parameter.
     * @param material the material.
     * @param newValue the new value.
     */
    @FXThread
    protected void applyParam(@NotNull final MatParam param, @NotNull final Material material,
                              @Nullable final Object newValue) {

        if (newValue == null) {
            material.clearParam(param.getName());
        } else {
            material.setParam(param.getName(), param.getVarType(), newValue);
        }
    }

    /**
     * Get relevant value of the material parameter.
     *
     * @param param    the material parameter.
     * @param material the material.
     * @return the relevant value.
     */
    @FXThread
    protected @Nullable Object getParamValue(@NotNull final MatParam param, @NotNull final Material material) {
        final MatParam currentParam = material.getParam(param.getName());
        return currentParam == null ? null : currentParam.getValue();
    }

    /**
     * Convert material parameter type to editable property type.
     *
     * @param varType the material parameter type.
     * @return the editable property type or null.
     */
    @FXThread
    protected @Nullable EditablePropertyType convert(@NotNull final VarType varType) {

        switch (varType) {
            case Boolean:
                return EditablePropertyType.BOOLEAN;
            case Float:
                return EditablePropertyType.FLOAT;
            case Int:
                return EditablePropertyType.INTEGER;
            case Vector4:
                return EditablePropertyType.COLOR;
            case Vector3:
                return EditablePropertyType.VECTOR_3F;
            case Vector2:
                return EditablePropertyType.VECTOR_2F;
            case Texture2D:
                return EditablePropertyType.TEXTURE_2D;
        }

        return null;
    }
}
