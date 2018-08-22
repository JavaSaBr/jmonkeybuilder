package com.ss.builder.editor.event;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The event about a renamed file.
 *
 * @author JavaSaBr
 */
public class FileRenamedFileEditorEvent extends FilePathChangedFileEditorEvent {

    public FileRenamedFileEditorEvent(@NotNull Path prevFile, @NotNull Path newFile) {
        super(prevFile, newFile);
    }
}
