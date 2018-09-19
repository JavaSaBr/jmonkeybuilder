package com.ss.builder.editor.event;

import com.ss.builder.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The event about a file with changed path.
 *
 * @author JavaSaBr
 */
public class FilePathChangedFileEditorEvent extends AbstractFileEditorEvent<Object> {

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

    public FilePathChangedFileEditorEvent(@NotNull Object source, @NotNull Path prevFile, @NotNull Path newFile) {
        super(source);
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
