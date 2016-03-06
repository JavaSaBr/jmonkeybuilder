package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;

import java.nio.file.Path;

import javafx.event.EventType;

/**
 * Событие о перемещении файла в Asset.
 *
 * @author Ronn
 */
public class MovedFileEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, MovedFileEvent.class.getSimpleName());

    public static final String PREV_FILE = "prev_file";
    public static final String NEW_FILE = "new_file";

    public MovedFileEvent() {
        super(EVENT_TYPE);
    }

    /**
     * @return новый файл.
     */
    public Path getNewFile() {
        return get(NEW_FILE);
    }

    /**
     * @param file новый файл.
     */
    public void setNewFile(final Path file) {
        set(NEW_FILE, file);
    }

    /**
     * @return предыдущий файл.
     */
    public Path getPrevFile() {
        return get(PREV_FILE);
    }

    /**
     * @param file предыдущий файл.
     */
    public void setPrevFile(final Path file) {
        set(PREV_FILE, file);
    }
}
