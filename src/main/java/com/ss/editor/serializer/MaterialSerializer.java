package com.ss.editor.serializer;

import com.jme3.asset.TextureKey;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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
    public static String serializeToString(@NotNull final Material material) {

        final MaterialDef materialDef = material.getMaterialDef();
        final Collection<MatParam> params = material.getParams();

        final StringBuilder builder = new StringBuilder();
        builder.append("Material MyMaterial : ").append(materialDef.getAssetName()).append(" {\n");
        builder.append("    MaterialParameters {\n");

        params.forEach(matParam -> {
            final String value = toString(matParam.getVarType(), matParam.getValue());
            builder.append("        ").append(matParam.getName()).append(" : ").append(value).append('\n');
        });

        builder.append("    }\n");
        builder.append("    AdditionalRenderState {\n");

        final RenderState renderState = material.getAdditionalRenderState();
        final RenderState.BlendMode blendMode = renderState.getBlendMode();
        final RenderState.FaceCullMode faceCullMode = renderState.getFaceCullMode();

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

        final float polyOffsetFactor = renderState.getPolyOffsetFactor();
        final float polyOffsetUnits = renderState.getPolyOffsetUnits();

        if (polyOffsetFactor != 0 || polyOffsetUnits != 0) {
            builder.append("      PolyOffset ").append(polyOffsetFactor).append(' ').append(polyOffsetUnits).append('\n');
        }

        builder.append("    }\n");
        builder.append("}");

        return builder.toString();
    }

    private static String toString(@NotNull final VarType varType, @NotNull final Object value) {

        switch (varType) {
            case Int:
            case Float:
            case Boolean:
                return String.valueOf(value);
            case Vector4: {

                if (value instanceof ColorRGBA) {
                    final ColorRGBA color = (ColorRGBA) value;
                    return color.getRed() + " " + color.getGreen() + " " + color.getBlue() + " " + color.getAlpha();
                } else if (value instanceof Vector4f) {
                    final Vector4f vector4f = (Vector4f) value;
                    return vector4f.getX() + " " + vector4f.getY() + " " + vector4f.getZ() + " " + vector4f.getW();
                }

                break;
            }
            case Vector3: {
                final Vector3f vector3f = (Vector3f) value;
                return vector3f.getX() + " " + vector3f.getY() + " " + vector3f.getZ();
            }
            case Texture2D: {

                final Texture2D texture2D = (Texture2D) value;
                final TextureKey textureKey = (TextureKey) texture2D.getKey();

                final StringBuilder builder = new StringBuilder();
                if (textureKey.isFlipY()) builder.append("Flip ");

                if (texture2D.getWrap(Texture.WrapAxis.T) == Texture.WrapMode.Repeat) {
                    builder.append("Repeat ");
                }

                builder.append(textureKey.getName());

                return builder.toString();
            }
        }

        throw new RuntimeException("can't support this type " + varType);
    }
}
