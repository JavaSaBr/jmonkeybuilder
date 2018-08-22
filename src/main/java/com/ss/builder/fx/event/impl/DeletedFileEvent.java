package com.ss.builder.ui.event.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
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

    public static final EventType<DeletedFileEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, DeletedFileEvent.class.getSimpleName());
        }
    }

    private static final String FILE = "file";
    private static final String IS_DIRECTORY = "is_directory";

    public DeletedFileEvent() {
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
     * Return true if the file is directory.
     *
     * @return true if the file is directory.
     */
    public boolean isDirectory() {
        return get(IS_DIRECTORY) == Boolean.TRUE;
    }

    /**
     * Set the file.
     *
     * @param file the file.
     */
    public void setFile(@NotNull Path file) {
        set(FILE, file);
    }

    /**
     * Return true if it is a directory.
     *
     * @param directory true if it is a directory.
     */
    public void setDirectory(boolean directory) {
        set(IS_DIRECTORY, directory);
    }
}
