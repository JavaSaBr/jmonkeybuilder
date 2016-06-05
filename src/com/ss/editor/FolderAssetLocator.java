package com.ss.editor;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;
import com.jme3.asset.StreamAssetInfo;
import com.ss.editor.config.EditorConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Реализация загрузчика контента из папки ассета.
 *
 * @author Ronn
 */
public class FolderAssetLocator implements AssetLocator {

    @Override
    public void setRootPath(String rootPath) {
    }

    @Override
    public AssetInfo locate(final AssetManager manager, final AssetKey key) {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();

        if (currentAsset == null) return null;

        final String name = key.getName();
        final Path resolve = currentAsset.resolve(name);

        if (!Files.exists(resolve)) return null;

        try {
            return new StreamAssetInfo(manager, key, Files.newInputStream(resolve));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
