package com.ss.editor.extension.loader;

import static java.util.Objects.requireNonNull;
import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.post.FilterPostProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The implementation of jME Importer to load scenes.
 *
 * @author JavaSaBr
 */
public class SceneLoader implements JmeImporter {

    /**
     * The application.
     */
    @Nullable
    private static Application application;

    /**
     * The filter post processor.
     */
    @Nullable
    private static FilterPostProcessor processor;

    /**
     * Install a scene loader to the asset manager.
     *
     * @param application the application.
     */
    public static void install(@NotNull final Application application) {
        install(application, null);
    }

    /**
     * Install a scene loader to the asset manager.
     *
     * @param application the application.
     * @param processor   the processor.
     */
    public static void install(@NotNull final Application application, @Nullable final FilterPostProcessor processor) {
        final AssetManager assetManager = application.getAssetManager();
        assetManager.unregisterLoader(BinaryImporter.class);
        assetManager.registerLoader(SceneLoader.class, "j3o", "j3f", "j3s");
        SceneLoader.application = application;
        SceneLoader.processor = processor;
    }

    public static @NotNull AssetManager tryToGetAssetManager() {
        return requireNonNull(application).getAssetManager();
    }

    public static @NotNull AppStateManager tryToGetStateManager() {
        return requireNonNull(application).getStateManager();
    }

    public static @Nullable FilterPostProcessor tryToGetPostProcessor() {
        return processor;
    }

    /**
     * The thread local importers.
     */
    @NotNull
    private final ThreadLocal<Deque<BinaryImporter>> threadLocalImporters;

    /**
     * The current importer.
     */
    @NotNull
    private final ThreadLocal<BinaryImporter> currentImporter;

    public SceneLoader() {
        currentImporter = new ThreadLocal<>();
        threadLocalImporters = new ThreadLocal<Deque<BinaryImporter>>() {

            @Override
            protected Deque<BinaryImporter> initialValue() {
                return new ArrayDeque<>();
            }
        };
    }

    @Override
    public InputCapsule getCapsule(final Savable id) {
        final BinaryImporter importer = currentImporter.get();
        return importer.getCapsule(id);
    }

    @Override
    public AssetManager getAssetManager() {
        final BinaryImporter importer = currentImporter.get();
        return importer.getAssetManager();
    }

    @Override
    public int getFormatVersion() {
        final BinaryImporter importer = currentImporter.get();
        return importer.getFormatVersion();
    }

    @Override
    public Object load(@NotNull final AssetInfo assetInfo) throws IOException {

        final Deque<BinaryImporter> importers = threadLocalImporters.get();
        BinaryImporter importer = importers.pollLast();

        if (importer == null) {
            importer = new BinaryImporter();
        }

        final BinaryImporter prev = currentImporter.get();
        currentImporter.set(importer);
        try {
            return importer.load(assetInfo);
        } finally {
            importers.addLast(importer);
            currentImporter.set(prev);
        }
    }
}
