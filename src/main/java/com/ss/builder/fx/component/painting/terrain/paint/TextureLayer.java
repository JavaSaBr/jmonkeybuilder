package com.ss.builder.fx.component.painting.terrain.paint;

import static com.ss.builder.util.EditorUtils.*;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.util.EditorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The model of texture layer.
 *
 * @author JavaSaBr
 */
public class TextureLayer implements Comparable<TextureLayer> {

    /**
     * The settings.
     */
    @NotNull
    private final TextureLayerSettings settings;

    /**
     * The layer.
     */
    private final int layer;

    public TextureLayer(@NotNull final TextureLayerSettings settings, final int layer) {
        this.settings = settings;
        this.layer = layer;
    }

    @Override
    public int compareTo(@NotNull final TextureLayer textureLayer) {
        return layer - textureLayer.layer;
    }

    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TextureLayer that = (TextureLayer) o;
        return layer == that.layer;
    }

    /**
     * Get the layer.
     *
     * @return the layer.
     */
    @FromAnyThread
    public int getLayer() {
        return layer;
    }

    /**
     * Get the scale.
     *
     * @return the texture scale.
     */
    @FromAnyThread
    public float getScale() {
        return settings.getTextureScale(getLayer());
    }

    /**
     * Set the scale.
     *
     * @param scale the texture scale.
     */
    @FromAnyThread
    public void setScale(final float scale) {
        settings.setTextureScale(scale, getLayer());
    }

    /**
     * Get diffuse file.
     *
     * @return the diffuse file.
     */
    @FromAnyThread
    public @Nullable Path getDiffuseFile() {

        final Texture diffuse = settings.getDiffuse(getLayer());
        final AssetKey assetKey = diffuse == null ? null : diffuse.getKey();
        if (diffuse == null || assetKey == null) {
            return null;
        }

        return EditorUtils.getRealFile(assetKey.getName());
    }

    /**
     * Set a new diffuse texture.
     *
     * @param diffuseFile the file to diffuse texture or null.
     */
    @FromAnyThread
    public void setDiffuseFile(@Nullable final Path diffuseFile) {
        final AssetManager assetManager = EditorUtils.getAssetManager();
        final Path assetFile = diffuseFile == null ? null : EditorUtils.getAssetFile(diffuseFile);
        final String assetPath = assetFile == null ? null : EditorUtils.toAssetPath(assetFile);
        final Texture texture = assetPath == null ? null : assetManager.loadTexture(assetPath);
        settings.setDiffuse(texture, getLayer());
    }

    /**
     * Get the normal file.
     *
     * @return the normal file
     */
    @FromAnyThread
    public @Nullable Path getNormalFile() {

        final Texture normal = settings.getNormal(getLayer());
        final AssetKey assetKey = normal == null ? null : normal.getKey();
        if (normal == null || assetKey == null) {
            return null;
        }

        return EditorUtils.getRealFile(assetKey.getName());
    }

    /**
     * Set the new normal texture.
     *
     * @param normalFile the file to normal texture or null.
     */
    @FromAnyThread
    public void setNormalFile(@Nullable final Path normalFile) {
        final AssetManager assetManager = EditorUtils.getAssetManager();
        final Path assetFile = normalFile == null ? null : EditorUtils.getAssetFile(normalFile);
        final String assetPath = assetFile == null ? null : EditorUtils.toAssetPath(assetFile);
        final Texture texture = assetPath == null ? null : assetManager.loadTexture(assetPath);
        settings.setNormal(texture, getLayer());
    }

    @Override
    public int hashCode() {
        return layer;
    }
}
