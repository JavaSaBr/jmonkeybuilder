package com.ss.extension.loader;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.export.binary.BinaryImporter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * The implementation of jME Importer to load scenes.
 *
 * @author JavaSaBr
 */
public class SceneLoader implements JmeImporter {

    /**
     * The application.
     */
    private static Application application;

    /**
     * Install a scene loader to the asset manager.
     *
     * @param application the application.
     */
    public static void install(@NotNull final Application application) {
        final AssetManager assetManager = application.getAssetManager();
        assetManager.unregisterLoader(BinaryImporter.class);
        assetManager.registerLoader(SceneLoader.class, "j3o", "j3f", "j3s");
        SceneLoader.application = application;
    }

    @NotNull
    public static AssetManager tryToGetAssetManager() {
        return Objects.requireNonNull(application)
                      .getAssetManager();
    }

    @NotNull
    public static AppStateManager tryToGetStateManager() {
        return Objects.requireNonNull(application)
                      .getStateManager();
    }

    @NotNull
    private final BinaryImporter importer;

    /**
     * The application.
     */
    @NotNull
    private final Application app;

    public SceneLoader() {
        importer = new BinaryImporter();
        app = SceneLoader.application;
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
