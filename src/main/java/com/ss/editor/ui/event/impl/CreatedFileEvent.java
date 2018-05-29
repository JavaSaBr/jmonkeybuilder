package com.ss.editor.ui.event.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.ui.event.SceneEvent;
import javafx.event.Event;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The event about created a new file.
 *
 * @author JavaSaBr
 */
public class CreatedFileEvent extends SceneEvent {

    public static final EventType<CreatedFileEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, CreatedFileEvent.class.getSimpleName());
        }
    }

    private static final String FILE = "file";
    private static final String NEED_SELECT = "need_select";
    private static final String IS_DIRECTORY = "is_directory";

    public CreatedFileEvent() {
        super(EVENT_TYPE);
    }

    /**
     * Set true if need to select a file.
     *
     * @param needSelect true if need to select a file.
     */
    public void setNeedSelect(final boolean needSelect) {
        set(NEED_SELECT, needSelect);
    }

    /**
     * Return true if need to select a file.
     *
     * @return true if need to select a file.
     */
    public boolean isNeedSelect() {
        return get(NEED_SELECT) == Boolean.TRUE;
    }

    /**
     * Return true if it is a directory.
     *
     * @return true if it is a directory.
     */
    public boolean isDirectory() {
        return get(IS_DIRECTORY) == Boolean.TRUE;
    }

    /**
     * Get the new file.
     *
     * @return the new file.
     */
    public @NotNull Path getFile() {
        return notNull(get(FILE), "Can't find a file");
    }

    /**
     * Set the new file.
     *
     * @param file the new file.
     */
    public void setFile(@NotNull Path file) {
        set(FILE, file);
    }

    /**
     * Set true if it is a directory.
     *
     * @param directory true if it is a directory.
     */
    public void setDirectory(boolean directory) {
        set(IS_DIRECTORY, directory);
    }
}
