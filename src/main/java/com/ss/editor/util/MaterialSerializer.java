package com.ss.editor.util;

import com.jme3.asset.TextureKey;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.TextureCubeMap;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.rlib.common.util.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of a material serializer.
 *
 * @author JavaSaBr
 */
public class MaterialSerializer {

    /**
     * Serialize to string string.
     *
     * @param material the material
     * @return the string
     */
    @FromAnyThread
    public static @NotNull String serializeToString(@NotNull Material material) {

        var materialDef = material.getMaterialDef();
        var params = material.getParams();

        var builder = new StringBuilder();
        builder.append("Material MyMaterial : ").append(materialDef.getAssetName()).append(" {\n");
        builder.append("    MaterialParameters {\n");
        params.forEach(matParam -> addMaterialParameter(builder, matParam));
        builder.append("    }\n");
        builder.append("    AdditionalRenderState {\n");

        var renderState = material.getAdditionalRenderState();
        var blendMode = renderState.getBlendMode();
        var faceCullMode = renderState.getFaceCullMode();

        if (blendMode != RenderState.BlendMode.Off) {
            builder.append("      Blend ").append(blendMode.name()).append('\n');
        }

        if (faceCullMode != RenderState.FaceCullMode.Back) {
            builder.append("      FaceCull ").append(faceCullMode.name()).append('\n');
        }

        if (renderState.isWireframe()) builder.append("      Wireframe On\n");
        if (!renderState.isDepthTest()) builder.append("      DepthTest Off\n");
        if (!renderState.isDepthWrite()) builder.append("      DepthWrite Off\n");
        if (!renderState.isColorWrite()) builder.append("      ColorWrite Off\n");

        var polyOffsetFactor = renderState.getPolyOffsetFactor();
        var polyOffsetUnits = renderState.getPolyOffsetUnits();

        if (polyOffsetFactor != 0 || polyOffsetUnits != 0) {
            builder.append("      PolyOffset ")
                    .append(polyOffsetFactor)
                    .append(' ')
                    .append(polyOffsetUnits)
                    .append('\n');
        }

        builder.append("    }\n");
        builder.append("}");

        return builder.toString();
    }

    /**
     * Add the material parameter to the builder.
     *
     * @param builder  the builder.
     * @param matParam the material parameter.
     */
    @FromAnyThread
    private static void addMaterialParameter(@NotNull StringBuilder builder, @NotNull MatParam matParam) {

        var value = toString(matParam.getVarType(), matParam.getValue());
        if (StringUtils.isEmpty(value)) {
            return;
        }

        builder.append("        ")
                .append(matParam.getName())
                .append(" : ")
                .append(value)
                .append('\n');
    }

    @FromAnyThread
    private static @NotNull String toString(@NotNull VarType varType, @NotNull Object value) {

        switch (varType) {
            case Int:
            case Float:
            case Boolean:
                return String.valueOf(value);
            case Vector4: {

                if (value instanceof ColorRGBA) {
                    var color = (ColorRGBA) value;
                    return color.getRed() + " " + color.getGreen() + " " + color.getBlue() + " " + color.getAlpha();
                } else if (value instanceof Vector4f) {
                    var vector4f = (Vector4f) value;
                    return vector4f.getX() + " " + vector4f.getY() + " " + vector4f.getZ() + " " + vector4f.getW();
                }

                break;
            }
            case Vector2: {
                var vector2f = (Vector2f) value;
                return vector2f.getX() + " " + vector2f.getY();
            }
            case Vector3: {
                var vector3f = (Vector3f) value;
                return vector3f.getX() + " " + vector3f.getY() + " " + vector3f.getZ();
            }
            case Texture2D: {

                var texture2D = (Texture2D) value;
                var textureKey = (TextureKey) texture2D.getKey();
                if (textureKey == null) {
                    return "";
                }

                var builder = new StringBuilder();

                if (textureKey.isFlipY()) {
                    builder.append("Flip ");
                }

                builder.append("Wrap").append(texture2D.getWrap(Texture.WrapAxis.T)).append("_T").append(' ');
                builder.append("Wrap").append(texture2D.getWrap(Texture.WrapAxis.S)).append("_S").append(' ');
                builder.append("Mag").append(texture2D.getMagFilter()).append(' ');
                builder.append("Min").append(texture2D.getMinFilter()).append(' ');
                builder.append(textureKey.getName());

                return builder.toString();
            }
            case TextureCubeMap: {

                var textureCubeMap = (TextureCubeMap) value;
                var textureKey = (TextureKey) textureCubeMap.getKey();

                if (textureKey == null) {
                    return "";
                }

                var builder = new StringBuilder();

                if (textureKey.isFlipY()) {
                    builder.append("Flip ");
                }

                builder.append("Wrap").append(textureCubeMap.getWrap(Texture.WrapAxis.T)).append("_T").append(' ');
                builder.append("Wrap").append(textureCubeMap.getWrap(Texture.WrapAxis.S)).append("_S").append(' ');
                builder.append("Wrap").append(textureCubeMap.getWrap(Texture.WrapAxis.R)).append("_R").append(' ');
                builder.append("Mag").append(textureCubeMap.getMagFilter()).append(' ');
                builder.append("Min").append(textureCubeMap.getMinFilter()).append(' ');
                builder.append(textureKey.getName());

                return builder.toString();
            }
        }

        throw new RuntimeException("can't support this type " + varType);
    }
}
