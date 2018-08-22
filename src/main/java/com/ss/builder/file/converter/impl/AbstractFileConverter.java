package com.ss.builder.file.converter.impl;

import static com.ss.rlib.common.util.FileUtils.containsExtensions;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.config.EditorConfig;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.fx.event.FxEventManager;
import com.ss.builder.fx.util.UiUtils;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.config.EditorConfig;
import com.ss.builder.file.converter.FileConverter;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.fx.event.FxEventManager;
import com.ss.builder.fx.util.UiUtils;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The base implementation of {@link FileConverter}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractFileConverter implements FileConverter {

    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(FileConverter.class);

    /**
     * The empty array.
     */
    @NotNull
    private static final Array<String> EMPTY_ARRAY = ArrayFactory.newArray(String.class);

    /**
     * The editor config.
     */
    @NotNull
    protected static final EditorConfig EDITOR_CONFIG = EditorConfig.getInstance();

    /**
     * The executor manager.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The FX event manager.
     */
    @NotNull
    protected static final FxEventManager FX_EVENT_MANAGER = FxEventManager.getInstance();

    @Override
    public void convert(@NotNull final Path source) {

        final String targetFileName = FileUtils.getNameWithoutExtension(source) + "." + getTargetExtension();

        final Path parent = source.getParent();
        final Path targetFile = parent.resolve(targetFileName);

        convert(source, targetFile);
    }

    @Override
    public void convert(@NotNull final Path source, @NotNull final Path destination) {

        if (Files.isDirectory(source) || Files.isDirectory(destination)) {
            throw new IllegalArgumentException("source or destination is folder.");
        }

        final Array<String> extensions = getAvailableExtensions();
        if (!extensions.isEmpty() && !containsExtensions(extensions.array(), source)) {
            throw new IllegalArgumentException("incorrect extension of file " + source);
        }

        UiUtils.incrementLoading();

        EXECUTOR_MANAGER.addBackgroundTask(() -> {
            try {

                final boolean overwrite = Files.exists(destination);

                convertImpl(source, destination, overwrite);

                if (overwrite) {
                    notifyFileChanged(destination);
                } else {
                    notifyFileCreated(destination);
                }

            } catch (final Exception e) {
                EditorUtils.handleException(LOGGER, this, e);
                EXECUTOR_MANAGER.addFxTask(() -> notifyFileCreatedImpl(null));
            }
        });
    }

    /**
     * Implementation of converting a file.
     *
     * @param source      the source file.
     * @param destination the target file.
     * @param overwrite   is need to overwrite.
     * @throws IOException if was problem with writing to the destination or reading from the source file.
     */
    protected void convertImpl(@NotNull final Path source, @NotNull final Path destination, final boolean overwrite)
            throws IOException {
    }

    /**
     * Get the list of available extensions.
     *
     * @return the list of available extensions.
     */
    protected @NotNull Array<String> getAvailableExtensions() {
        return EMPTY_ARRAY;
    }

    @Override
    public abstract @NotNull String getTargetExtension();

    /**
     * Notify the eEditor about file changing.
     *
     * @param file the changed file.
     */
    @FromAnyThread
    protected void notifyFileChanged(@NotNull final Path file) {
        EXECUTOR_MANAGER.addFxTask(() -> notifyFileChangedImpl(file));
    }

    @FxThread
    private void notifyFileChangedImpl(@NotNull final Path file) {
        UiUtils.decrementLoading();
    }

    /**
     * Notify the editor about file creating.
     *
     * @param file the created file.
     */
    @FromAnyThread
    protected void notifyFileCreated(@Nullable final Path file) {
        EXECUTOR_MANAGER.addFxTask(() -> notifyFileCreatedImpl(file));
    }

    @FxThread
    private void notifyFileCreatedImpl(@Nullable final Path file) {
        UiUtils.decrementLoading();
    }
}
