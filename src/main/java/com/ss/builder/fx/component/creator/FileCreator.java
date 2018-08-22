package com.ss.builder.ui.component.creator;

import com.ss.builder.annotation.FxThread;
import com.ss.editor.annotation.FxThread;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The interface to implement a some file creator.
 *
 * @author JavaSaBr
 */
public interface FileCreator {

    /**
     * Start creating a new file.
     *
     * @param file the parent or nearest file.
     */
    @FxThread
    void start(@NotNull Path file);
}
