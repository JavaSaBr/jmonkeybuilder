package com.ss.builder.asset.locator;

import static com.ss.editor.util.EditorUtils.getAssetManager;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetLocator;
import com.jme3.asset.AssetManager;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.util.EditorUtils;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.rlib.common.util.ArrayUtils;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.array.ConcurrentArray;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

/**
 * The implementation of asset locator to use file system as asset folder.
 *
 * @author JavaSaBr
 */
public class FileSystemAssetLocator implements AssetLocator {

    @NotNull
    private static final ConcurrentArray<AssetKey<?>> LOCATED_KEYS = ArrayFactory.newConcurrentStampedLockArray(AssetKey.class);

    /**
     * Clear all located objects from this locator.
     */
    @FromAnyThread
    public static void clear() {
        final long stamp = LOCATED_KEYS.writeLock();
        try {
            LOCATED_KEYS.forEach(EditorUtils.getAssetManager(), (assetKey, manager) -> manager.deleteFromCache(assetKey));
        } finally {
            LOCATED_KEYS.writeUnlock(stamp);
        }
    }

    @Override
    public void setRootPath(@NotNull final String rootPath) {
    }

    @Override
    public AssetInfo locate(@NotNull final AssetManager manager, @NotNull final AssetKey key) {

        final Path absoluteFile = Paths.get(key.getName());
        if (!Files.exists(absoluteFile)) {
            return null;
        }

        ArrayUtils.runInWriteLock(LOCATED_KEYS, key, Collection::add);

        return new FolderAssetLocator.PathAssetInfo(manager, key, absoluteFile);
    }
}
