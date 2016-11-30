package com.ss.editor.util;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.material.RenderState;
import com.jme3.material.TechniqueDef;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shader.Shader;
import com.ss.editor.FileExtensions;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import rlib.util.FileUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The class with utility methods for working with {@link Material}.
 *
 * @author JavaSaBr
 */
public class MaterialUtils {

    /**
     * Check the material on containing the shader.
     *
     * @param material the material for checking.
     * @param file     the file of the shader.
     * @return true if the material contains the shader.
     */
    public static boolean containsShader(@NotNull final Material material, @NotNull final Path file) {

        final MaterialDef materialDef = material.getMaterialDef();

        final Path assetFile = EditorUtil.getAssetFile(file);
        final String assetPath = EditorUtil.toAssetPath(assetFile);

        return containsShader(materialDef, assetPath);
    }

    /**
     * Check the material definition on containing the shader.
     *
     * @param materialDef the material definition.
     * @param assetPath   the path of the shader.
     * @return true if the material definition contains the shader.
     */
    public static boolean containsShader(@NotNull final MaterialDef materialDef, @NotNull final String assetPath) {

        final List<TechniqueDef> defaultTechniques = materialDef.getTechniqueDefs("Default");

        for (final TechniqueDef technique : defaultTechniques) {
            final EnumMap<Shader.ShaderType, String> shaderProgramNames = technique.getShaderProgramNames();
            if (shaderProgramNames.containsValue(assetPath)) return true;
        }

        return false;
    }

    /**
     * @param path the file path.
     * @return true if the file is shader.
     */
    public static boolean isShaderFile(@NotNull final Path path) {
        final String extension = FileUtils.getExtension(path);
        return FileExtensions.GLSL_FRAGMENT.equals(extension) || FileExtensions.GLSL_VERTEX.equals(extension);

    }

    /**
     * Update the first material to the second material.
     *
     * @param toUpdate the material for updating.
     * @param material the target material.
     */
    public static void updateTo(@NotNull final Material toUpdate, @NotNull final Material material) {

        final Collection<MatParam> oldParams = new ArrayList<>(toUpdate.getParams());
        oldParams.forEach(matParam -> {

            final MatParam param = material.getParam(matParam.getName());

            if (param == null || param.getValue() == null) {
                toUpdate.clearParam(matParam.getName());
            }
        });

        final Collection<MatParam> actualParams = material.getParams();
        actualParams.forEach(matParam -> toUpdate.setParam(matParam.getName(), matParam.getVarType(), matParam.getValue()));

        final RenderState additionalRenderState = toUpdate.getAdditionalRenderState();
        additionalRenderState.set(material.getAdditionalRenderState());
    }

    /**
     * Migrate the material to second material.
     *
     * @param toMigrate the material for migrating.
     * @param material  the target material.
     */
    public static void migrateTo(@NotNull final Material toMigrate, @NotNull final Material material) {

        final MaterialDef materialDef = toMigrate.getMaterialDef();
        final Collection<MatParam> actualParams = material.getParams();

        actualParams.forEach(matParam -> {

            final MatParam param = materialDef.getMaterialParam(matParam.getName());

            if (param == null || param.getVarType() != matParam.getVarType()) {
                return;
            }

            toMigrate.setParam(matParam.getName(), matParam.getVarType(), matParam.getValue());
        });

        final RenderState additionalRenderState = toMigrate.getAdditionalRenderState();
        additionalRenderState.set(material.getAdditionalRenderState());
    }

    public static void cleanUpMaterialParams(@NotNull final Spatial spatial) {

        final AtomicInteger counter = new AtomicInteger();

        final Array<Material> materials = ArrayFactory.newArray(Material.class);

        addMaterialsTo(materials, spatial);

        materials.forEach(material -> cleanUp(material, counter));
    }

    private static void cleanUp(@NotNull final Material material, @NotNull final AtomicInteger counter) {
        final Collection<MatParam> params = new ArrayList<>(material.getParams());
        params.forEach(matParam -> {
            if (matParam.getValue() == null) {
                material.clearParam(matParam.getName());
            }
        });
    }

    private static void addMaterialsTo(@NotNull final Array<Material> materials, @NotNull final Spatial spatial) {
        if (spatial instanceof Geometry) {
            final Material material = ((Geometry) spatial).getMaterial();
            if (material != null) materials.add(material);
        } else if (spatial instanceof Node) {
            final List<Spatial> children = ((Node) spatial).getChildren();
            children.forEach(child -> addMaterialsTo(materials, child));
        }
    }
}
