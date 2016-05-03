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
 * Набор утильных методов для работы с материалами.
 *
 * @author Ronn
 */
public class MaterialUtils {

    /**
     * Есть ли у указанного материала указанный шейдер.
     *
     * @param material проверяемый материал.
     * @param file     файл шейдера.
     * @return есть ли у этого материтала такой шейдер.
     */
    public static boolean containsShader(final Material material, final Path file) {

        final MaterialDef materialDef = material.getMaterialDef();

        final Path assetFile = EditorUtil.getAssetFile(file);
        final String assetPath = EditorUtil.toAssetPath(assetFile);

        return containsShader(materialDef, assetPath);
    }

    /**
     * Есть ли у указанного описания материала указанный шейдер.
     *
     * @param materialDef описание материала.
     * @param assetPath   путь к шейдеру.
     * @return есть ли у этого описания такой шейдер.
     */
    public static boolean containsShader(final MaterialDef materialDef, final String assetPath) {

        final List<TechniqueDef> defaultTechniques = materialDef.getTechniqueDefs("Default");

        for (final TechniqueDef technique : defaultTechniques) {

            final EnumMap<Shader.ShaderType, String> shaderProgramNames = technique.getShaderProgramNames();

            if (shaderProgramNames.containsValue(assetPath)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Является ли указанный файл шейдером.
     *
     * @param path путь файла.
     * @return является ли файл шейдером.
     */
    public static boolean isShaderFile(final Path path) {

        final String extension = FileUtils.getExtension(path);

        if (FileExtensions.GLSL_FRAGMENT.equals(extension) || FileExtensions.GLSL_VERTEX.equals(extension)) {
            return true;
        }

        return false;
    }

    /**
     * Обновление первого материала до второго.
     *
     * @param toUpdate материал на обновление.
     * @param material материал до которого надо обновить.
     */
    public static void updateTo(final Material toUpdate, final Material material) {

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
     * Миграция второго материала на первый.
     *
     * @param toMigrate материал на миграцию.
     * @param material  материал с которого надо мигрировать.
     */
    public static void migrateTo(final Material toMigrate, final Material material) {

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

    public static void cleanUpMaterialParams(final Spatial spatial) {

        final AtomicInteger counter = new AtomicInteger();

        final Array<Material> materials = ArrayFactory.newArray(Material.class);

        addMaterialsTo(materials, spatial);

        materials.forEach(material -> cleanUp(material, counter));
    }

    private static void cleanUp(final Material material, final AtomicInteger counter) {
        final Collection<MatParam> params = new ArrayList<>(material.getParams());
        params.forEach(matParam -> {
            if (matParam.getValue() == null) {
                material.clearParam(matParam.getName());
            }
        });
    }

    private static void addMaterialsTo(final Array<Material> materials, final Spatial spatial) {

        if (spatial instanceof Geometry) {

            final Material material = ((Geometry) spatial).getMaterial();

            if (material != null) {
                materials.add(material);
            }

        } else if (spatial instanceof Node) {
            final List<Spatial> children = ((Node) spatial).getChildren();
            children.forEach(child -> addMaterialsTo(materials, child));
        }
    }
}
