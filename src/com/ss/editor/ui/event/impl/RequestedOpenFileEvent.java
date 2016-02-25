package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;

import java.nio.file.Path;

import javafx.event.EventType;

/**
 * Событие об запроса на открытие файла.
 *
 * @author Ronn
 */
public class RequestedOpenFileEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestedOpenFileEvent.class.getSimpleName());

    public static final String FILE = "file";

    public RequestedOpenFileEvent() {
        super(EVENT_TYPE);
    }

    /**
     * @param file открываемый файл.
     */
    public void setFile(final Path file) {
        set(FILE, file);
    }

    /**
     * @return открываемый файл.
     */
    public Path getFile() {
        return get(FILE);
    }
}
