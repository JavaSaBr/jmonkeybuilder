package com.ss.editor.ui.preview;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.preview.impl.DefaultFilePreviewFactory;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

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
    private final Array<FilePreviewFactory> factories;

    private FilePreviewFactoryRegistry() {
        this.factories = ArrayFactory.newCopyOnModifyArray(FilePreviewFactory.class);
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
        factories.add(factory);
    }

    /**
     * Create available of file previews.
     *
     * @return the list of available file previews.
     */
    @FxThread
    public Array<FilePreview> createAvailablePreviews() {

        var result = Array.<FilePreview>ofType(FilePreview.class);

        factories.forEach(result, FilePreviewFactory::createFilePreviews);

        result.sort((first, second) ->
                second.getOrder() - first.getOrder());

        return result;
    }
}
