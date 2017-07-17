package com.ss.editor.ui.event.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.ui.event.SceneEvent;
import javafx.event.Event;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The event about renamed a file.
 *
 * @author JavaSaBr
 */
public class RenamedFileEvent extends SceneEvent {

    /**
     * The constant EVENT_TYPE.
     */
    @NotNull
    public static final EventType<SceneEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RenamedFileEvent.class.getSimpleName());
        }
    }

    private static final String PREV_FILE = "prev_file";
    private static final String NEW_FILE = "new_file";

    /**
     * Instantiates a new Renamed file event.
     */
    public RenamedFileEvent() {
        super(EVENT_TYPE);
    }

    /**
     * Gets new file.
     *
     * @return the new file.
     */
    @NotNull
    public Path getNewFile() {
        return notNull(get(NEW_FILE));
    }

    /**
     * Sets new file.
     *
     * @param file the new file.
     */
    public void setNewFile(@NotNull final Path file) {
        set(NEW_FILE, file);
    }

    /**
     * Gets prev file.
     *
     * @return the previous file.
     */
    @NotNull
    public Path getPrevFile() {
        return notNull(get(PREV_FILE));
    }

    /**
     * Sets prev file.
     *
     * @param file the previous file.
     */
    public void setPrevFile(@NotNull final Path file) {
        set(PREV_FILE, file);
    }
}
