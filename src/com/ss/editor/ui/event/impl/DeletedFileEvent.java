package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;

import java.nio.file.Path;

import javafx.event.EventType;

/**
 * Событие об удалении файла в Asset.
 *
 * @author Ronn
 */
public class DeletedFileEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, DeletedFileEvent.class.getSimpleName());

    public static final String FILE = "file";

    public DeletedFileEvent() {
        super(EVENT_TYPE);
    }

    /**
     * @return удаленный файл.
     */
    public Path getFile() {
        return get(FILE);
    }

    /**
     * @param file удаленный файл.
     */
    public void setFile(final Path file) {
        set(FILE, file);
    }
}
