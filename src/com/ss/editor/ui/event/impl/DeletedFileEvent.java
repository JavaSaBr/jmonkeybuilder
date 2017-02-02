package com.ss.editor.ui.event.impl;

import static java.util.Objects.requireNonNull;
import com.ss.editor.ui.event.SceneEvent;
import javafx.event.Event;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The event about deleted a file.
 *
 * @author JavaSaBr
 */
public class DeletedFileEvent extends SceneEvent {

    @NotNull
    public static final EventType<SceneEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, DeletedFileEvent.class.getSimpleName());
        }
    }

    private static final String FILE = "file";
    private static final String IS_DIRECTORY = "isDirectory";

    public DeletedFileEvent() {
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
     * @return true if the file is directory.
     */
    public boolean isDirectory() {
        return get(IS_DIRECTORY) == Boolean.TRUE;
    }

    /**
     * @param file the file.
     */
    public void setFile(@NotNull final Path file) {
        set(FILE, file);
    }

    /**
     * @param directory the directory.
     */
    public void setDirectory(final boolean directory) {
        set(IS_DIRECTORY, directory);
    }
}
