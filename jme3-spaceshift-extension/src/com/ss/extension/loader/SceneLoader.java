package com.ss.extension.loader;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.export.binary.BinaryImporter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * The implementation of jME Importer to load scenes.
 *
 * @author JavaSaBr
 */
public class SceneLoader implements JmeImporter {

    /**
     * Install a scene loader to the asset manager.
     *
     * @param assetManager the asset manager.
     */
    public static void install(@NotNull final AssetManager assetManager) {
        assetManager.unregisterLoader(BinaryImporter.class);
        assetManager.registerLoader(SceneLoader.class, "j3o", "j3f", "j3s");
    }

    @NotNull
    private final BinaryImporter importer;

    public SceneLoader() {
        importer = new BinaryImporter();
    }

    @Override
    public InputCapsule getCapsule(final Savable id) {
        return importer.getCapsule(id);
    }

    @Override
    public AssetManager getAssetManager() {
        return importer.getAssetManager();
    }

    @Override
    public int getFormatVersion() {
        return importer.getFormatVersion();
    }

    @Override
    public Object load(@NotNull final AssetInfo assetInfo) throws IOException {
        return importer.load(assetInfo);
    }
}
