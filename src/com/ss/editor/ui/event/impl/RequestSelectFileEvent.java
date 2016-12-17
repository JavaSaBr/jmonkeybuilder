package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;

import java.nio.file.Path;

import javafx.event.EventType;

/**
 * The event-request for selecting a file.
 *
 * @author JavaSaBr
 */
public class RequestSelectFileEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestSelectFileEvent.class.getSimpleName());
        }
    }

    public static final String FILE = "file";

    public RequestSelectFileEvent() {
        super(EVENT_TYPE);
    }

    /**
     * @return the file.
     */
    public Path getFile() {
        return get(FILE);
    }

    /**
     * @param file the file.
     */
    public void setFile(final Path file) {
        set(FILE, file);
    }
}
