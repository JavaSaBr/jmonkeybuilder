package com.ss.editor;

import static java.nio.file.Files.newInputStream;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;
import com.jme3.asset.StreamAssetInfo;
import com.jme3.asset.plugins.UrlAssetInfo;
import com.ss.editor.config.EditorConfig;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jetbrains.annotations.NotNull;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

/**
 * The implementation of {@link AssetLocator} to load data from an asset folder.
 *
 * @author JavaSaBr
 */
public class FolderAssetLocator implements AssetLocator {

    @NotNull
    private static final Array<String> URL_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        URL_EXTENSIONS.add(FileExtensions.MODEL_SCENE);
    }

    @Override
    public void setRootPath(@NotNull final String rootPath) {
    }

    @Override
    public AssetInfo locate(final AssetManager manager, final AssetKey key) {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) return null;

        final String name = key.getName();
        final Path resolve = currentAsset.resolve(name);
        if (!Files.exists(resolve)) return null;

        final String extension = FileUtils.getExtension(resolve);

        if (URL_EXTENSIONS.contains(extension)) {
            try {
                final URL url = resolve.toUri().toURL();
                return UrlAssetInfo.create(manager, key, url);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            return new StreamAssetInfo(manager, key, newInputStream(resolve));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
