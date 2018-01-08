package com.ss.editor.ui.event.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.ui.event.SceneEvent;
import javafx.event.Event;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The event about request to covert a file.
 *
 * @author JavaSaBr
 */
public class RequestedConvertFileEvent extends SceneEvent {

    /**
     * The constant EVENT_TYPE.
     */
    @NotNull
    public static final EventType<SceneEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestedConvertFileEvent.class.getSimpleName());
        }
    }

    private static final String FILE = "file";
    private static final String CONVERTER = "converter";

    public RequestedConvertFileEvent() {
        super(EVENT_TYPE);
    }

    /**
     * Get the description.
     *
     * @return the converter description.
     */
    public @NotNull FileConverterDescription getDescription() {
        return notNull(get(CONVERTER));
    }

    /**
     * Set the description.
     *
     * @param description the converter description.
     */
    public void setDescription(@NotNull final FileConverterDescription description) {
        set(CONVERTER, description);
    }

    /**
     * Get the file.
     *
     * @return the file to convert.
     */
    public @NotNull Path getFile() {
        return notNull(get(FILE));
    }

    /**
     * Set the file.
     *
     * @param file the file to convert.
     */
    public void setFile(@NotNull final Path file) {
        set(FILE, file);
    }
}
