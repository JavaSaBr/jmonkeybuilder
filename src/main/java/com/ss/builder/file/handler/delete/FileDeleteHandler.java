package com.ss.builder.file.handler.delete;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The interface to implement a handler to handle deleted file from the editor.
 *
 * @author JavaSaBr
 */
public interface FileDeleteHandler extends Cloneable {

    /**
     * Handle a file to delete before deleting.
     *
     * @param file the file to delete.
     */
    @FxThread
    void preDelete(@NotNull Path file);

    /**
     * Handle a file to delete after deleting.
     *
     * @param file the deleted file.
     */
    @FxThread
    void postDelete(@NotNull Path file);

    /**
     * Check that the file need to handle.
     *
     * @param file the file to check.
     * @return true of the file need to handle.
     */
    @FxThread
    boolean isNeedHandle(@NotNull Path file);

    /**
     * @return the cloned instance.
     */
    @FromAnyThread
    @NotNull FileDeleteHandler clone();
}
