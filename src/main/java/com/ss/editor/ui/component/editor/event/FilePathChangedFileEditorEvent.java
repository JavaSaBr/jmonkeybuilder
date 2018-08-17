package com.ss.editor.ui.component.editor.event;

import com.ss.editor.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The event about a file with changed path.
 *
 * @author JavaSaBr
 */
public class FilePathChangedFileEditorEvent extends AbstractFileEditorEvent {

    /**
     * The previous file path.
     */
    @NotNull
    private final Path prevFile;

    /**
     * THe new file path.
     */
    @NotNull
    private final Path newFile;

    public FilePathChangedFileEditorEvent(@NotNull Path prevFile, @NotNull Path newFile) {
        this.prevFile = prevFile;
        this.newFile = newFile;
    }

    /**
     * Get the previous file path.
     *
     * @return the previous file path.
     */
    @FromAnyThread
    public @NotNull Path getPrevFile() {
        return prevFile;
    }

    /**
     * Get the new file path.
     *
     * @return the new file path.
     */
    @FromAnyThread
    public @NotNull Path getNewFile() {
        return newFile;
    }
}
