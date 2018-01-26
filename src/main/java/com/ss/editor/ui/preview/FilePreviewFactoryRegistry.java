package com.ss.editor.ui.preview;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.preview.impl.DefaultFilePreviewFactory;
import com.ss.rlib.util.ArrayUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.array.ConcurrentArray;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The registry with available factories of file previews.
 *
 * @author JavaSaBr
 */
public class FilePreviewFactoryRegistry {

    @NotNull
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
    }

    /**
     * Register the new factory.
     *
     * @param factory the factory.
     */
    @FromAnyThread
    public void register(@NotNull final FilePreviewFactory factory) {
        ArrayUtils.runInWriteLock(factories, factory, Collection::add);
    }

    /**
     * Create available of file previews.
     *
     * @return the list of available file previews.
     */
    @FxThread
    public Array<FilePreview> createAvailablePreviews() {

        final Array<FilePreview> result = ArrayFactory.newArray(FilePreview.class);

        ArrayUtils.runInReadLock(factories, result, (previewFactories, toStore) ->
                previewFactories.forEach(toStore, FilePreviewFactory::createFilePreviews));

        result.sort((first, second) -> second.getOrder() - first.getOrder());

        return result;
    }
}
