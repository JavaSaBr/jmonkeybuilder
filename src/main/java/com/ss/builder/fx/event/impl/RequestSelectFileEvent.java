package com.ss.builder.fx.event.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.fx.event.SceneEvent;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The event about request to select a file.
 *
 * @author JavaSaBr
 */
public class RequestSelectFileEvent extends SceneEvent {

    public static final EventType<RequestSelectFileEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestSelectFileEvent.class.getSimpleName());
        }
    }

    private static final String FILE = "file";

    public RequestSelectFileEvent(@NotNull Path file) {
        super(EVENT_TYPE);
        setFile(file);
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
    public void setFile(@NotNull Path file) {
        set(FILE, file);
    }
}
