package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Objects;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * The event about changing a file.
 *
 * @author JavaSaBr.
 */
public class FileChangedEvent extends SceneEvent {

    @NotNull
    public static final EventType<SceneEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, FileChangedEvent.class.getSimpleName());
        }
    }

    @NotNull
    public static final String FILE = "file";

    public FileChangedEvent() {
        super(EVENT_TYPE);
    }

    /**
     * @return the file.
     */
    @NotNull
    public Path getFile() {
        return Objects.requireNonNull(get(FILE), "Can't find a file");
    }

    /**
     * @param file the file.
     */
    public void setFile(@NotNull final Path file) {
        set(FILE, file);
    }
}
