package com.ss.builder.ui.event.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.ui.component.editor.EditorDescriptor;
import com.ss.editor.ui.event.SceneEvent;
import javafx.event.Event;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The event about request to open a file.
 *
 * @author JavaSaBr
 */
public class RequestedOpenFileEvent extends SceneEvent {

    public static final EventType<RequestedOpenFileEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestedOpenFileEvent.class.getSimpleName());
        }
    }

    private static final String EDITOR = "editor";
    private static final String FILE = "file";
    private static final String NEED_SHOW = "need_show";

    public RequestedOpenFileEvent(@NotNull Path file) {
        super(EVENT_TYPE);
        setNeedShow(true);
        setFile(file);
    }

    public RequestedOpenFileEvent(@NotNull Path file, @NotNull EditorDescriptor description) {
        super(EVENT_TYPE);
        setNeedShow(true);
        setFile(file);
        setDescription(description);
    }

    /**
     * Get the description.
     *
     * @return the editor descriptor.
     */
    public @Nullable EditorDescriptor getDescription() {
        return get(EDITOR);
    }

    /**
     * Set the description.
     *
     * @param description the editor descriptor.
     */
    public void setDescription(@Nullable EditorDescriptor description) {
        if (description == null) {
            remove(EDITOR);
        } else {
            set(EDITOR, description);
        }
    }

    /**
     * Get the file.
     *
     * @return the file to open.
     */
    public @NotNull Path getFile() {
        return notNull(get(FILE));
    }

    /**
     * Set the file.
     *
     * @param file the file to open.
     */
    public void setFile(@NotNull Path file) {
        set(FILE, file);
    }

    /**
     * Return true if need to show the editor.
     *
     * @return true if need to show the editor.
     */
    public boolean isNeedShow() {
        return get(NEED_SHOW) == Boolean.TRUE;
    }

    /**
     * Set true if need to show the editor.
     *
     * @param needShow true if need to show the editor.
     */
    public void setNeedShow(boolean needShow) {
        set(NEED_SHOW, needShow);
    }
}
