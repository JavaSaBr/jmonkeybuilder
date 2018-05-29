package com.ss.editor.ui.event.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
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

    /**
     * The constant EVENT_TYPE.
     */
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
     * Get the description.
     *
     * @return the creator description.
     */
    public @NotNull FileCreatorDescription getDescription() {
        return notNull(get(CREATOR));
    }

    /**
     * Set the description.
     *
     * @param description the creator description.
     */
    public void setDescription(@NotNull final FileCreatorDescription description) {
        set(CREATOR, description);
    }

    /**
     * Get the file.
     *
     * @return the file.
     */
    public @NotNull Path getFile() {
        return notNull(get(FILE));
    }

    /**
     * Set the file.
     *
     * @param file the file.
     */
    public void setFile(@NotNull final Path file) {
        set(FILE, file);
    }
}
