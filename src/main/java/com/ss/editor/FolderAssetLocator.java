package com.ss.editor;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;
import com.ss.editor.config.EditorConfig;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * The implementation of {@link AssetLocator} to load data from an asset folder.
 *
 * @author JavaSaBr
 */
public class FolderAssetLocator implements AssetLocator {

    @Override
    public void setRootPath(@NotNull final String rootPath) {
    }

    @Override
    public AssetInfo locate(final AssetManager manager, final AssetKey key) {

        System.out.println("Locate " + key.getName() + " in the " + key.getFolder());

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) return null;

        final String name = key.getName();
        final Path resolve = currentAsset.resolve(name);
        if (!Files.exists(resolve)) return null;

        return new PathAssetInfo(manager, key, resolve);
    }

    private class PathAssetInfo extends AssetInfo {

        @NotNull
        private final Path path;

        private PathAssetInfo(@NotNull final AssetManager manager, @NotNull final AssetKey key,
                              @NotNull final Path path) {
            super(manager, key);
            this.path = path;
        }

        @Override
        public @NotNull InputStream openStream() {
            try {
                return Files.newInputStream(path, StandardOpenOption.READ);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
