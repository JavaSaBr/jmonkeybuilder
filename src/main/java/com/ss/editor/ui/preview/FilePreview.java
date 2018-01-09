package com.ss.editor.ui.preview;

import com.ss.editor.annotation.FXThread;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.pools.Reusable;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The interface to implement a file preview.
 *
 * @author JavaSaBr
 */
public interface FilePreview extends Reusable {

    /**
     * Initialize this preview to work in the pane.
     *
     * @param pane the pane.
     */
    @FXThread
    void initialize(@NotNull StackPane pane);

    /**
     * Hide this preview.
     */
    @FXThread
    void hide();

    /**
     * Check of supporting of the resource.
     *
     * @param resource the resource.
     * @return true if this preview supports the resource.
     */
    @FXThread
    default boolean isSupport(@NotNull final String resource) {
        return !StringUtils.isEmpty(FileUtils.getExtension(resource));
    }

    /**
     * Check of supporting of the file.
     *
     * @param file the file.
     * @return true if this preview supports the file.
     */
    @FXThread
    default boolean isSupport(@NotNull final Path file) {
        return !StringUtils.isEmpty(FileUtils.getExtension(file));
    }

    /**
     * Show the preview of the resource.
     *
     * @param resource the resource.
     */
    @FXThread
    void show(@NotNull String resource);

    /**
     * Show the preview of the file.
     *
     * @param file the file.
     */
    @FXThread
    void show(@NotNull Path file);

    /**
     * Get the order of this preview component.
     *
     * @return the order.
     */
    @FXThread
    default int getOrder() {
        return 0;
    }

    @Override
    @FXThread
    default void release() {
    }
}
