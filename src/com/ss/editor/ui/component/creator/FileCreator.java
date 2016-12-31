package com.ss.editor.ui.component.creator;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The interface to implement a some file creator.
 *
 * @author JavaSaBr
 */
public interface FileCreator {

    /**
     * Start creating near the file.
     *
     * @param file the file.
     */
    void start(@NotNull final Path file);
}
