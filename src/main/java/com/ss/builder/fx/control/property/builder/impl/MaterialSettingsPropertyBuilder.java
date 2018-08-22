package com.ss.builder.fx.control.property.builder.impl;

import static com.ss.editor.extension.property.EditablePropertyType.*;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.material.RenderState;
import com.jme3.shader.VarType;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.node.material.*;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.extension.property.SimpleProperty;
import com.ss.builder.model.node.material.*;
import com.ss.builder.fx.control.property.builder.PropertyBuilder;
import com.ss.rlib.common.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The implementation of {@link PropertyBuilder} to build properties for material settings node.
 *
 * @author JavaSaBr
 */
public class MaterialSettingsPropertyBuilder extends MaterialPropertyBuilder {

    private static final Comparator<MatParam> MAT_PARAM_NAME_COMPARATOR = (first, second) -> {

        int result = MAT_PARAM_COMPARATOR.compare(first, second);

        if (result != 0) {
            return result;
        }

        return StringUtils.compareIgnoreCase(first.getName(), second.getName());
    };

    private static final Set<VarType> TEXTURE_TYPES = Set.of(
            VarType.Texture2D,
            VarType.TextureCubeMap,
            VarType.Texture3D,
            VarType.TextureArray,
            VarType.TextureBuffer
    );

    private static final Set<VarType> COLOR_TYPES = Set.of(VarType.Vector4);

    private static final PropertyBuilder INSTANCE = new MaterialSettingsPropertyBuilder();

    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    @Override
    @FxThread
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull Object object) {

        if (!(object instanceof MaterialSettings) || object instanceof RootMaterialSettings) {
            return null;
        }

        var settings = (MaterialSettings) object;
        var material = settings.getMaterial();

        if(object instanceof RenderSettings) {

            var renderState = material.getAdditionalRenderState();

            var result = new ArrayList<EditableProperty<?, ?>>();
            result.add(new SimpleProperty<>(BOOLEAN, Messages.MATERIAL_RENDER_STATE_COLOR_WRITE, settings,
                    sett -> renderState.isColorWrite(),
                    (sett, property) -> renderState.setColorWrite(property)));
            result.add(new SimpleProperty<>(BOOLEAN, Messages.MATERIAL_RENDER_STATE_DEPTH_WRITE, settings,
                    sett -> renderState.isDepthWrite(),
                    (sett, property) -> renderState.setDepthWrite(property)));
            result.add(new SimpleProperty<>(BOOLEAN, Messages.MATERIAL_RENDER_STATE_DEPTH_TEST, settings,
                    sett -> renderState.isDepthTest(),
                    (sett, property) -> renderState.setDepthTest(property)));
            result.add(new SimpleProperty<>(BOOLEAN, Messages.MATERIAL_RENDER_STATE_WIREFRAME, settings,
                    sett -> renderState.isWireframe(),
                    (sett, property) -> renderState.setWireframe(property)));
            result.add(new SimpleProperty<>(ENUM, Messages.MATERIAL_RENDER_STATE_FACE_CULL_MODE, settings,
                    sett -> renderState.getFaceCullMode(),
                    (sett, property) -> renderState.setFaceCullMode(property)));
            result.add(new SimpleProperty<>(ENUM, Messages.MATERIAL_RENDER_STATE_BLEND_MODE, settings,
                    sett -> renderState.getBlendMode(),
                    (sett, property) -> renderState.setBlendMode(property)));
            result.add(new SimpleProperty<>(ENUM, Messages.MATERIAL_RENDER_STATE_BLEND_EQUATION, settings,
                    sett -> renderState.getBlendEquation(),
                    (sett, property) -> renderState.setBlendEquation(property)));
            result.add(new SimpleProperty<>(ENUM, Messages.MATERIAL_RENDER_STATE_BLEND_EQUATION_ALPHA, settings,
                    sett -> renderState.getBlendEquationAlpha(),
                    (sett, property) -> renderState.setBlendEquationAlpha(property)));
            result.add(new SimpleProperty<>(FLOAT,  Messages.MATERIAL_RENDER_STATE_POLY_OFFSET_FACTOR, 0.1F, settings,
                    sett -> renderState.getPolyOffsetFactor(),
                    (sett, property) -> renderState.setPolyOffset(property, renderState.getPolyOffsetUnits())));
            result.add(new SimpleProperty<>(FLOAT,  Messages.MATERIAL_RENDER_STATE_POLY_OFFSET_UNITS, 0.1F, settings,
                    sett -> renderState.getPolyOffsetUnits(),
                    (sett, property) -> renderState.setPolyOffset(renderState.getPolyOffsetFactor(), property)));

            return result;
        }

        return material.getMaterialDef()
                .getMaterialParams()
                .stream()
                .filter(param -> filter(param, object))
                .sorted(MAT_PARAM_NAME_COMPARATOR)
                .map(param -> convert(param, material, settings))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Filter material parameters for the presented object.
     *
     * @param param  the material parameter.
     * @param object the presented object.
     * @return true of we can show the parameter.
     */
    @FxThread
    private boolean filter(@NotNull MatParam param, @NotNull Object object) {

        if (object instanceof TexturesSettings) {
            return TEXTURE_TYPES.contains(param.getVarType());
        } else if (object instanceof ColorsSettings) {
            return COLOR_TYPES.contains(param.getVarType());
        }

        return !TEXTURE_TYPES.contains(param.getVarType()) && !COLOR_TYPES.contains(param.getVarType());
    }

    /**
     * Convert the material parameter to an editable property.
     *
     * @param param    the material parameter.
     * @param material the material.
     * @param settings the settings.
     * @return the editable property or null.
     */
    @FxThread
    private @Nullable EditableProperty<?, ?> convert(
            @NotNull MatParam param,
            @NotNull Material material,
            @NotNull MaterialSettings settings
    ) {

        var propertyType = convert(param.getVarType());
        if (propertyType == null) {
            return null;
        }

        return new SimpleProperty<>(propertyType, param.getName(), 0.1F, settings,
                object -> getParamValue(param, material),
                (object, newValue) -> applyParam(param, material, newValue));
    }
}
