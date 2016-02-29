package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.event.SceneEvent;

import java.nio.file.Path;

import javafx.event.EventType;

/**
 * Событие об запроса на создание файла.
 *
 * @author Ronn
 */
public class RequestedCreateFileEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestedCreateFileEvent.class.getSimpleName());

    public static final String FILE = "file";
    public static final String CREATOR = "creator";

    public RequestedCreateFileEvent() {
        super(EVENT_TYPE);
    }

    /**
     * @return описание создателя.
     */
    public FileCreatorDescription getDescription() {
        return get(CREATOR);
    }

    /**
     * @param description описание создателя.
     */
    public void setDescription(final FileCreatorDescription description) {
        set(CREATOR, description);
    }

    /**
     * @return открываемый файл.
     */
    public Path getFile() {
        return get(FILE);
    }

    /**
     * @param file открываемый файл.
     */
    public void setFile(final Path file) {
        set(FILE, file);
    }
}
