package com.ss.editor.ui.event.impl;

import static java.util.Objects.requireNonNull;
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

    @NotNull
    public static final EventType<SceneEvent> EVENT_TYPE;

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
     * @return the file.
     */
    @NotNull
    public Path getFile() {
        return requireNonNull(get(FILE), "Can't find a file");
    }

    /**
     * @param file the file.
     */
    public void setFile(@NotNull final Path file) {
        set(FILE, file);
    }
}
