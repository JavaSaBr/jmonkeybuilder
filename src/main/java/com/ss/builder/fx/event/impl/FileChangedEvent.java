package com.ss.builder.fx.event.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.fx.event.SceneEvent;
import javafx.event.Event;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The event about changed a file.
 *
 * @author JavaSaBr
 */
public class FileChangedEvent extends SceneEvent {

    public static final EventType<FileChangedEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, FileChangedEvent.class.getSimpleName());
        }
    }

    private static final String FILE = "file";

    public FileChangedEvent() {
        super(EVENT_TYPE);
    }

    /**
     * Get the file.
     *
     * @return the file.
     */
    public @NotNull Path getFile() {
        return notNull(get(FILE), "Can't find a file");
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
