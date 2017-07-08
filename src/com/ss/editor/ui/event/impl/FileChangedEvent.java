package com.ss.editor.ui.event.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.ui.event.SceneEvent;
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

    /**
     * The constant EVENT_TYPE.
     */
    @NotNull
    public static final EventType<SceneEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, FileChangedEvent.class.getSimpleName());
        }
    }

    private static final String FILE = "file";

    /**
     * Instantiates a new File changed event.
     */
    public FileChangedEvent() {
        super(EVENT_TYPE);
    }

    /**
     * Gets file.
     *
     * @return the file.
     */
    @NotNull
    public Path getFile() {
        return notNull(get(FILE), "Can't find a file");
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
