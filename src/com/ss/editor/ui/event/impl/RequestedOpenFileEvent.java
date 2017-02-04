package com.ss.editor.ui.event.impl;

import static java.util.Objects.requireNonNull;
import com.ss.editor.ui.component.editor.EditorDescription;
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

    public static final EventType<SceneEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestedOpenFileEvent.class.getSimpleName());
        }
    }

    public static final String EDITOR = "editor";

    private static final String FILE = "file";
    private static final String NEED_SHOW = "need_show";

    public RequestedOpenFileEvent() {
        super(EVENT_TYPE);
        setNeedShow(true);
    }

    /**
     * @return the editor descriptor.
     */
    @Nullable
    public EditorDescription getDescription() {
        return get(EDITOR);
    }

    /**
     * @param description the editor descriptor.
     */
    public void setDescription(final EditorDescription description) {
        set(EDITOR, description);
    }

    /**
     * @return the file to open.
     */
    @NotNull
    public Path getFile() {
        return requireNonNull(get(FILE));
    }

    /**
     * @param file the file to open.
     */
    public void setFile(final Path file) {
        set(FILE, file);
    }

    /**
     * @return true if need to show the editor.
     */
    public boolean isNeedShow() {
        return get(NEED_SHOW) == Boolean.TRUE;
    }

    /**
     * @param needShow true if need to show the editor.
     */
    public void setNeedShow(final boolean needShow) {
        set(NEED_SHOW, needShow);
    }
}
