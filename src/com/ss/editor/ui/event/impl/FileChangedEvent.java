package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;

import java.nio.file.Path;

import javafx.event.EventType;

/**
 * Событие об изменении файла в Asset.
 *
 * @author Ronn
 */
public class FileChangedEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, FileChangedEvent.class.getSimpleName());

    public static final String FILE = "file";

    public FileChangedEvent() {
        super(EVENT_TYPE);
    }

    /**
     * @return файл.
     */
    public Path getFile() {
        return get(FILE);
    }

    /**
     * @param file файл.
     */
    public void setFile(final Path file) {
        set(FILE, file);
    }
}
