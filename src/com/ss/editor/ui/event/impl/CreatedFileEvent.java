package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;

import java.nio.file.Path;

import javafx.event.EventType;

/**
 * Событие об создании нового файла в Asset.
 *
 * @author Ronn
 */
public class CreatedFileEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, CreatedFileEvent.class.getSimpleName());

    public static final String FILE = "file";

    public CreatedFileEvent() {
        super(EVENT_TYPE);
    }

    /**
     * @return новый файл.
     */
    public Path getFile() {
        return get(FILE);
    }

    /**
     * @param file новый файл.
     */
    public void setFile(final Path file) {
        set(FILE, file);
    }
}
