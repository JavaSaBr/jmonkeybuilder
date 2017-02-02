package com.ss.editor.ui.event.impl;

import static java.util.Objects.requireNonNull;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.event.SceneEvent;
import javafx.event.Event;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The event about request to create a new file.
 *
 * @author JavaSaBr
 */
public class RequestedCreateFileEvent extends SceneEvent {

    @NotNull
    public static final EventType<SceneEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestedCreateFileEvent.class.getSimpleName());
        }
    }

    private static final String FILE = "file";
    private static final String CREATOR = "creator";

    public RequestedCreateFileEvent() {
        super(EVENT_TYPE);
    }

    /**
     * @return the creator description.
     */
    @NotNull
    public FileCreatorDescription getDescription() {
        return requireNonNull(get(CREATOR));
    }

    /**
     * @param description the creator description.
     */
    public void setDescription(@NotNull final FileCreatorDescription description) {
        set(CREATOR, description);
    }

    /**
     * @return the file.
     */
    @NotNull
    public Path getFile() {
        return requireNonNull(get(FILE));
    }

    /**
     * @param file the file.
     */
    public void setFile(@NotNull final Path file) {
        set(FILE, file);
    }
}
