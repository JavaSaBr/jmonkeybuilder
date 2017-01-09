package com.ss.editor.util;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.asset.TextureKey;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.material.RenderState;
import com.jme3.material.TechniqueDef;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shader.Shader;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import rlib.util.FileUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The class with utility methods for working with {@link Material}.
 *
 * @author JavaSaBr
 */
public class MaterialUtils {

    private static final Editor EDITOR = Editor.getInstance();

    /**
     * Update a material if need.
     *
     * @param file     the changed file.
     * @param material the current material.
     * @return the updated material or null.
     */
    @Nullable
    public static Material updateMaterialIdNeed(@NotNull final Path file, @NotNull final Material material) {

        boolean needToReload = false;
        String textureKey = null;

        if (MaterialUtils.isShaderFile(file)) {
            if (!MaterialUtils.containsShader(material, file)) return null;
            needToReload = true;
        } else if (MaterialUtils.isTextureFile(file)) {
            textureKey = MaterialUtils.containsTexture(material, file);
            if (textureKey == null) return null;
            needToReload = true;
        }

        final AssetManager assetManager = EDITOR.getAssetManager();
        final String assetName = material.getAssetName();

        // try to refresh texture directly
        if (assetName == null && textureKey != null) {
            assetManager.clearCache();
            refreshTextures(material, textureKey);
            return null;
        } else if (!needToReload || StringUtils.isEmpty(assetName)) return null;

        final MaterialKey materialKey = new MaterialKey(assetName);
        assetManager.deleteFromCache(materialKey);

        if (textureKey != null) {
            assetManager.clearCache();
        }

        final Material newMaterial = assetManager.loadAsset(materialKey);
        MaterialUtils.updateTo(newMaterial, material, textureKey);
        return newMaterial;
    }

    /**
     * Check a material on containing a shader.
     *
     * @param material the material for checking.
     * @param file     the file of the shader.
     * @return true if the material contains the shader.
     */
    public static boolean containsShader(@NotNull final Material material, @NotNull final Path file) {

        final MaterialDef materialDef = material.getMaterialDef();

        final Path assetFile = Objects.requireNonNull(getAssetFile(file), "Can't get an asset file.");
        final String assetPath = toAssetPath(assetFile);

        return containsShader(materialDef, assetPath);
    }

    /**
     * Check a material on containing a texture.
     *
     * @param material the material for checking.
     * @param file     the file of the texture.
     * @return changed texture key or null.
     */
    @Nullable
    public static String containsTexture(@NotNull final Material material, @NotNull final Path file) {

        final Path assetFile = Objects.requireNonNull(getAssetFile(file), "Can't get an asset file.");
        final String assetPath = toAssetPath(assetFile);

        return containsTexture(material, assetPath) ? assetPath : null;
    }

    /**
     * Check a material definition on containing a shader.
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
     * Check a material on containing a texture.
     *
     * @param material  the material.
     * @param assetPath the path of the texture.
     * @return true if the material definition contains the texture.
     */
    public static boolean containsTexture(@NotNull final Material material, @NotNull final String assetPath) {

        final Collection<MatParam> materialParams = material.getParams();
        for (final MatParam materialParam : materialParams) {
            if (materialParam.getVarType() != VarType.Texture2D) continue;
            final Texture value = (Texture) materialParam.getValue();
            final TextureKey textureKey = value == null ? null : (TextureKey) value.getKey();
            if (textureKey != null && StringUtils.equals(textureKey.getName(), assetPath)) {
                return true;
            }
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
     * @param path the file path.
     * @return true if the file is texture.
     */
    public static boolean isTextureFile(@NotNull final Path path) {
        final String extension = FileUtils.getExtension(path);
        return FileExtensions.IMAGE_DDS.equals(extension) || FileExtensions.IMAGE_HDR.equals(extension) ||
                FileExtensions.IMAGE_HDR.equals(extension) || FileExtensions.IMAGE_JPEG.equals(extension) ||
                FileExtensions.IMAGE_JPG.equals(extension) || FileExtensions.IMAGE_PNG.equals(extension) ||
                FileExtensions.IMAGE_TGA.equals(extension) || FileExtensions.IMAGE_TIFF.equals(extension);

    }

    /**
     * Refresh textures in a material.
     *
     * @param material   the material.
     * @param textureKey the texture key.
     */
    public static void refreshTextures(@NotNull final Material material, @NotNull final String textureKey) {

        final AssetManager assetManager = EDITOR.getAssetManager();

        final Collection<MatParam> params = material.getParams();
        params.forEach(matParam -> {

            final VarType varType = matParam.getVarType();
            final Object value = matParam.getValue();

            if (varType != VarType.Texture2D || value == null) return;

            final Texture texture = (Texture) value;
            final TextureKey key = (TextureKey) texture.getKey();

            if (key != null && StringUtils.equals(key.getName(), textureKey)) {
                final Texture newTexture = assetManager.loadAsset(key);
                matParam.setValue(newTexture);
            }
        });

    }

    /**
     * Update the first material to the second material.
     *
     * @param toUpdate the material for updating.
     * @param material the target material.
     */
    public static void updateTo(@NotNull final Material toUpdate, @NotNull final Material material,
                                @Nullable final String textureKey) {

        final Collection<MatParam> oldParams = new ArrayList<>(toUpdate.getParams());
        oldParams.forEach(matParam -> {

            final MatParam param = material.getParam(matParam.getName());

            if (param == null || param.getValue() == null) {
                toUpdate.clearParam(matParam.getName());
            }
        });

        final Collection<MatParam> actualParams = material.getParams();
        actualParams.forEach(matParam -> {

            final VarType varType = matParam.getVarType();
            final Object value = matParam.getValue();

            if (varType == VarType.Texture2D && value != null && textureKey != null) {

                final Texture texture = (Texture) value;
                final AssetKey key = texture.getKey();

                if (key != null && StringUtils.equals(key.getName(), textureKey)) {
                    return;
                }
            }

            toUpdate.setParam(matParam.getName(), varType, value);
        });

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
