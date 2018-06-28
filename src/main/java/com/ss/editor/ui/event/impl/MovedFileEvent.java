package com.ss.editor.ui.event.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.ui.event.SceneEvent;
import javafx.event.Event;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The event about moved a file.
 *
 * @author JavaSaBr
 */
public class MovedFileEvent extends SceneEvent {

    /**
     * The constant EVENT_TYPE.
     */
    @NotNull
    public static final EventType<SceneEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, MovedFileEvent.class.getSimpleName());
        }
    }

    private static final String PREV_FILE = "prev_file";
    private static final String NEW_FILE = "new_file";

    public MovedFileEvent(@NotNull Path prevFile, @NotNull Path newFile) {
        super(EVENT_TYPE);
        setPrevFile(prevFile);
        setNewFile(newFile);
    }

    /**
     * Get the new file.
     *
     * @return the new file.
     */
    public @NotNull Path getNewFile() {
        return notNull(get(NEW_FILE));
    }

    /**
     * Set the new file.
     *
     * @param file the new file.
     */
    public void setNewFile(@NotNull final Path file) {
        set(NEW_FILE, file);
    }

    /**
     * Get the previous file.
     *
     * @return the previous file.
     */
    public @NotNull Path getPrevFile() {
        return notNull(get(PREV_FILE));
    }

    /**
     * Set the previous file.
     *
     * @param file the previous file.
     */
    public void setPrevFile(@NotNull final Path file) {
        set(PREV_FILE, file);
    }
}
