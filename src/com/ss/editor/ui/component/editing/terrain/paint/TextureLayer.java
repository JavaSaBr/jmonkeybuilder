package com.ss.editor.ui.component.editing.terrain.paint;

import static com.ss.editor.util.EditorUtil.*;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture;
import com.ss.editor.Editor;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.ExecutorManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The model of texture layer.
 *
 * @author JavaSaBr
 */
public class TextureLayer implements Comparable<TextureLayer> {

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    @NotNull
    private static final Editor EDITOR = Editor.getInstance();

    /**
     * The reference to settings.
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TextureLayer that = (TextureLayer) o;
        return layer == that.layer;
    }

    /**
     * @return the layer.
     */
    @FromAnyThread
    public int getLayer() {
        return layer;
    }

    /**
     * @return the texture scale.
     */
    @FromAnyThread
    public float getScale() {
        return settings.getTextureScale(getLayer());
    }

    /**
     * @param scale the texture scale.
     */
    @FromAnyThread
    public void setScale(final float scale) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> settings.setTextureScale(scale, getLayer()));
    }

    /**
     * @return the diffuse file.
     */
    @Nullable
    @FromAnyThread
    public Path getDiffuseFile() {
        final Texture diffuse = settings.getDiffuse(getLayer());
        final AssetKey assetKey = diffuse == null ? null : diffuse.getKey();
        if (diffuse == null || assetKey == null) return null;
        return getRealFile(assetKey.getName());
    }

    /**
     * Set a new diffuse texture.
     *
     * @param diffuseFile the file to diffuse texture or null.
     */
    @FromAnyThread
    public void setDiffuseFile(@Nullable final Path diffuseFile) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            final AssetManager assetManager = EDITOR.getAssetManager();
            final Path assetFile = diffuseFile == null ? null : getAssetFile(diffuseFile);
            final String assetPath = assetFile == null ? null : toAssetPath(assetFile);
            final Texture texture = assetPath == null ? null : assetManager.loadTexture(assetPath);
            settings.setDiffuse(texture, getLayer());
        });
    }

    @FromAnyThread
    public Path getNormalFile() {
        final Texture normal = settings.getNormal(getLayer());
        final AssetKey assetKey = normal == null ? null : normal.getKey();
        if (normal == null || assetKey == null) return null;
        return getRealFile(assetKey.getName());
    }

    /**
     * Set a new normal texture.
     *
     * @param normalFile the file to normal texture or null.
     */
    @FromAnyThread
    public void setNormalFile(@Nullable final Path normalFile) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            final AssetManager assetManager = EDITOR.getAssetManager();
            final Path assetFile = normalFile == null ? null : getAssetFile(normalFile);
            final String assetPath = assetFile == null ? null : toAssetPath(assetFile);
            final Texture texture = assetPath == null ? null : assetManager.loadTexture(assetPath);
            settings.setNormal(texture, getLayer());
        });
    }

    @Override
    public int hashCode() {
        return layer;
    }
}
