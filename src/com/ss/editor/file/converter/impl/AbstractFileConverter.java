package com.ss.editor.file.converter.impl;

import static com.ss.rlib.util.FileUtils.containsExtensions;
import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.file.converter.FileConverter;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The base implementation of a file converter.
 *
 * @author JavaSaBr
 */
public abstract class AbstractFileConverter implements FileConverter {

    /**
     * The constant LOGGER.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(FileConverter.class);

    @NotNull
    private static final Array<String> EMPTY_ARRAY = ArrayFactory.newArray(String.class);

    /**
     * The constant EDITOR_CONFIG.
     */
    @NotNull
    protected static final EditorConfig EDITOR_CONFIG = EditorConfig.getInstance();

    /**
     * The constant EXECUTOR_MANAGER.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The constant FX_EVENT_MANAGER.
     */
    @NotNull
    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The constant JFX_APPLICATION.
     */
    @NotNull
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    /**
     * The constant EDITOR.
     */
    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

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

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        scene.incrementLoading();

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
                EditorUtil.handleException(LOGGER, this, e);
                EXECUTOR_MANAGER.addFXTask(() -> notifyFileCreatedImpl(null));
            }
        });
    }

    /**
     * Implementation of converting a file.
     *
     * @param source      the source file.
     * @param destination the target file.
     * @param overwrite   is need to overwrite.
     */
    protected void convertImpl(@NotNull final Path source, @NotNull final Path destination, final boolean overwrite) {
    }

    /**
     * Gets available extensions.
     *
     * @return the list of available extensions.
     */
    @NotNull
    protected Array<String> getAvailableExtensions() {
        return EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getTargetExtension() {
        return "";
    }

    /**
     * Notify the Editor about file changing.
     *
     * @param file the changed file.
     */
    @FromAnyThread
    protected void notifyFileChanged(@NotNull final Path file) {
        EXECUTOR_MANAGER.addFXTask(() -> notifyFileChangedImpl(file));
    }

    @FXThread
    private void notifyFileChangedImpl(@NotNull final Path file) {
        final EditorFXScene scene = JFX_APPLICATION.getScene();
        scene.decrementLoading();
    }

    /**
     * Notify the Editor about file creating.
     *
     * @param file the created file.
     */
    @FromAnyThread
    protected void notifyFileCreated(@Nullable final Path file) {
        EXECUTOR_MANAGER.addFXTask(() -> notifyFileCreatedImpl(file));
    }

    @FXThread
    private void notifyFileCreatedImpl(@Nullable final Path file) {
        final EditorFXScene scene = JFX_APPLICATION.getScene();
        scene.decrementLoading();
    }
}
