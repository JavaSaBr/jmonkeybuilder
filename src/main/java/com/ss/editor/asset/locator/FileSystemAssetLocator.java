package com.ss.editor.asset.locator;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The implementation of asset locator to use file system as asset folder.
 *
 * @author JavaSaBr
 */
public class FileSystemAssetLocator implements AssetLocator {

    @Override
    public void setRootPath(@NotNull final String rootPath) {

    }

    @Override
    public AssetInfo locate(@NotNull final AssetManager manager, @NotNull final AssetKey key) {

        final Path absoluteFile = Paths.get(key.getName());
        if (!Files.exists(absoluteFile)) return null;

        return new FolderAssetLocator.PathAssetInfo(manager, key, absoluteFile);
    }
}
