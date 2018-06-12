package com.ss.editor.ui.preview;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.preview.impl.DefaultFilePreviewFactory;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.ArrayUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.array.ConcurrentArray;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The registry with available factories of file previews.
 *
 * @author JavaSaBr
 */
public class FilePreviewFactoryRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(FilePreviewFactoryRegistry.class);

    private static final FilePreviewFactoryRegistry INSTANCE = new FilePreviewFactoryRegistry();

    public static @NotNull FilePreviewFactoryRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * The list of available factories.
     */
    @NotNull
    private final ConcurrentArray<FilePreviewFactory> factories;

    private FilePreviewFactoryRegistry() {
        this.factories = ArrayFactory.newConcurrentAtomicARSWLockArray(FilePreviewFactory.class);
        register(DefaultFilePreviewFactory.getInstance());
        LOGGER.info("initialized.");
    }

    /**
     * Register the new factory.
     *
     * @param factory the factory.
     */
    @FromAnyThread
    public void register(@NotNull FilePreviewFactory factory) {
        ArrayUtils.runInWriteLock(factories, factory, Collection::add);
    }

    /**
     * Create available of file previews.
     *
     * @return the list of available file previews.
     */
    @FxThread
    public Array<FilePreview> createAvailablePreviews() {

        var result = Array.<FilePreview>ofType(FilePreview.class);

        factories.runInReadLock(result, (previewFactories, toStore) ->
                previewFactories.forEach(toStore, FilePreviewFactory::createFilePreviews));

        result.sort((first, second) ->
                second.getOrder() - first.getOrder());

        return result;
    }
}
