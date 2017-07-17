package com.ss.editor.util;

import static com.ss.editor.util.EditorUtil.*;
import static java.util.Objects.requireNonNull;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.asset.TextureKey;
import com.jme3.material.*;
import com.jme3.scene.Spatial;
import com.jme3.shader.Shader;
import com.jme3.shader.VarType;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.StringUtils;
import jme3tools.converters.ImageToAwt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

/**
 * The class with utility methods for working with {@link Material}.
 *
 * @author JavaSaBr
 */
public class MaterialUtils {

    @NotNull
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

        final AssetManager assetManager = EDITOR.getAssetManager();

        boolean needToReload = false;
        String textureKey = null;

        if (MaterialUtils.isShaderFile(file)) {
            if (!MaterialUtils.containsShader(material, file)) return null;
            needToReload = true;

            // if the shader was changed we need to reload material definition
            final MaterialDef materialDef = material.getMaterialDef();
            final String assetName = materialDef.getAssetName();
            assetManager.deleteFromCache(new AssetKey<>(assetName));

        } else if (MaterialUtils.isTextureFile(file)) {
            textureKey = MaterialUtils.containsTexture(material, file);
            if (textureKey == null) return null;
        }

        final String assetName = material.getAssetName();

        // try to refresh texture directly
        if (textureKey != null) {
            refreshTextures(material, textureKey);
            return null;
        } else if (!needToReload || StringUtils.isEmpty(assetName)) {
            return null;
        }

        final MaterialKey materialKey = new MaterialKey(assetName);

        assetManager.deleteFromCache(materialKey);

        final Material newMaterial = new Material(assetManager, material.getMaterialDef().getAssetName());

        migrateTo(newMaterial, material);

        return newMaterial;
    }

    /**
     * Check a material on containing a shader.
     *
     * @param material the material for checking.
     * @param file     the file of the shader.
     * @return true if the material contains the shader.
     */
    private static boolean containsShader(@NotNull final Material material, @NotNull final Path file) {

        final MaterialDef materialDef = material.getMaterialDef();

        final Path assetFile = requireNonNull(getAssetFile(file), "Can't get an asset file.");
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
    private static String containsTexture(@NotNull final Material material, @NotNull final Path file) {

        final Path assetFile = requireNonNull(getAssetFile(file), "Can't get an asset file.");
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
    private static boolean containsShader(@NotNull final MaterialDef materialDef, @NotNull final String assetPath) {

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
    private static boolean containsTexture(@NotNull final Material material, @NotNull final String assetPath) {

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
     * Is shader file boolean.
     *
     * @param path the file path.
     * @return true if the file is shader.
     */
    public static boolean isShaderFile(@NotNull final Path path) {
        final String extension = FileUtils.getExtension(path);
        return FileExtensions.GLSL_FRAGMENT.equals(extension) || FileExtensions.GLSL_VERTEX.equals(extension);

    }

    /**
     * Is texture file boolean.
     *
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
    private static void refreshTextures(@NotNull final Material material, @NotNull final String textureKey) {

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
    private static void updateTo(@NotNull final Material toUpdate, @NotNull final Material material) {

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

            toUpdate.setParam(matParam.getName(), varType, value);
        });

        final RenderState additionalRenderState = toUpdate.getAdditionalRenderState();
        additionalRenderState.set(material.getAdditionalRenderState());
    }

    /**
     * Migrate the material to second material.
     *
     * @param target the target migrating.
     * @param source the source material.
     */
    public static void migrateTo(@NotNull final Material target, @NotNull final Material source) {

        final MaterialDef materialDef = target.getMaterialDef();
        final Collection<MatParam> actualParams = source.getParams();

        actualParams.forEach(matParam -> {

            final MatParam param = materialDef.getMaterialParam(matParam.getName());

            if (param == null || param.getVarType() != matParam.getVarType()) {
                return;
            }

            target.setParam(matParam.getName(), matParam.getVarType(), matParam.getValue());
        });

        final RenderState additionalRenderState = target.getAdditionalRenderState();
        additionalRenderState.set(source.getAdditionalRenderState());

        target.setKey(source.getKey());
    }

    /**
     * Remove all material parameters with null value for all geometries.
     *
     * @param spatial the model.
     */
    public static void cleanUpMaterialParams(@NotNull final Spatial spatial) {
        NodeUtils.visitGeometry(spatial, geometry -> {
            final Material material = geometry.getMaterial();
            if (material != null) cleanUp(material);
        });
    }

    /**
     * Clean up a material.
     *
     * @param material the material.
     */
    private static void cleanUp(@NotNull final Material material) {
        final Collection<MatParam> params = new ArrayList<>(material.getParams());
        params.stream().filter(param -> param.getValue() ==
                null).forEach(matParam -> material.clearParam(matParam.getName()));
    }

    /**
     * Save if need textures of a material.
     *
     * @param material the material.
     */
    public static void saveIfNeedTextures(@NotNull final Material material) {
        final Collection<MatParam> params = material.getParams();
        params.stream().filter(matParam -> matParam.getVarType() == VarType.Texture2D)
                .map(MatParam::getValue)
                .map(value -> (Texture) value)
                .forEach(MaterialUtils::saveIfNeedTexture);
    }

    /**
     * Save if need a texture.
     *
     * @param texture the texture.
     */
    private static void saveIfNeedTexture(@NotNull final Texture texture) {

        final Image image = texture.getImage();
        if (!image.isChanged()) return;

        final AssetKey key = texture.getKey();
        final Path file = requireNonNull(getRealFile(key.getName()));
        final BufferedImage bufferedImage = ImageToAwt.convert(image, false, true, 0);

        try (final OutputStream out = Files.newOutputStream(file)) {
            ImageIO.write(bufferedImage, "png", out);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        image.clearChanges();
    }
}
