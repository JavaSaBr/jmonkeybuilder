package com.ss.editor.file.delete.handler;

import com.ss.editor.annotation.FXThread;
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
    @FXThread
    void preDelete(@NotNull final Path file);

    /**
     * Handle a file to delete after deleting.
     *
     * @param file the deleted file.
     */
    @FXThread
    void postDelete(@NotNull final Path file);

    /**
     * Check that the file need to handle.
     *
     * @param file the file to check.
     * @return true of the file need to handle.
     */
    @FXThread
    boolean isNeedHandle(@NotNull Path file);

    /**
     * @return the cloned instance.
     */
    FileDeleteHandler clone();
}
