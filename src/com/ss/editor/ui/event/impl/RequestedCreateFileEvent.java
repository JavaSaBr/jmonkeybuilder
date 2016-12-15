package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.event.SceneEvent;

import java.nio.file.Path;

import javafx.event.EventType;

/**
 * The event-request for creating a new file.
 *
 * @author JavaSaBr
 */
public class RequestedCreateFileEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestedCreateFileEvent.class.getSimpleName());

    public static final String FILE = "file";
    public static final String CREATOR = "creator";

    public RequestedCreateFileEvent() {
        super(EVENT_TYPE);
    }

    /**
     * @return the creator description.
     */
    public FileCreatorDescription getDescription() {
        return get(CREATOR);
    }

    /**
     * @param description the creator description.
     */
    public void setDescription(final FileCreatorDescription description) {
        set(CREATOR, description);
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
