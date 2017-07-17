package com.ss.editor.ui.event.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The event about request to select a file.
 *
 * @author JavaSaBr
 */
public class RequestSelectFileEvent extends SceneEvent {

    /**
     * The constant EVENT_TYPE.
     */
    public static final EventType<SceneEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestSelectFileEvent.class.getSimpleName());
        }
    }

    private static final String FILE = "file";

    /**
     * Instantiates a new Request select file event.
     */
    public RequestSelectFileEvent() {
        super(EVENT_TYPE);
    }

    /**
     * Gets file.
     *
     * @return the file.
     */
    @NotNull
    public Path getFile() {
        return notNull(get(FILE));
    }

    /**
     * Sets file.
     *
     * @param file the file.
     */
    public void setFile(@NotNull final Path file) {
        set(FILE, file);
    }
}
