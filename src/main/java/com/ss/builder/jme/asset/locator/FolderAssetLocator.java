package com.ss.builder.asset.locator;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.config.EditorConfig;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.config.EditorConfig;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * The implementation of {@link AssetLocator} to load data from an asset folder.
 *
 * @author JavaSaBr
 */
public class FolderAssetLocator implements AssetLocator {

    @NotNull
    private static final ThreadLocal<Boolean> IGNORE_LOCAL = ThreadLocal.withInitial(() -> false);

    /**
     * Set the flag of ignoring this locator.
     *
     * @param ignore true if need to ignore this locator.
     */
    public static void setIgnore(final boolean ignore) {
        IGNORE_LOCAL.set(false);
    }

    @Override
    @JmeThread
    public void setRootPath(@NotNull final String rootPath) {
    }

    @Override
    @JmeThread
    public AssetInfo locate(@NotNull final AssetManager manager, @NotNull final AssetKey key) {
        if (IGNORE_LOCAL.get() == Boolean.TRUE) {
            return null;
        }

        final Path absoluteFile = Paths.get(key.getName());
        if (Files.exists(absoluteFile)) {
            return null;
        }

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) {
            return null;
        }

        final String name = key.getName();
        final Path resolve = currentAsset.resolve(name);
        if (!Files.exists(resolve)) {
            return null;
        }

        return new PathAssetInfo(manager, key, resolve);
    }

    public static class PathAssetInfo extends AssetInfo {

        @NotNull
        private final Path path;

        public PathAssetInfo(@NotNull final AssetManager manager, @NotNull final AssetKey key,
                             @NotNull final Path path) {
            super(manager, key);
            this.path = path;
        }

        @Override
        @JmeThread
        public @NotNull InputStream openStream() {
            try {
                return Files.newInputStream(path, StandardOpenOption.READ);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
