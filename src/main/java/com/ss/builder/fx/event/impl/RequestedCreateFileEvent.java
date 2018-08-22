package com.ss.builder.fx.event.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.fx.component.creator.FileCreatorDescriptor;
import com.ss.builder.fx.event.SceneEvent;
import javafx.event.Event;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The event about request to create a new file.
 *
 * @author JavaSaBr
 */
public class RequestedCreateFileEvent extends SceneEvent {

    public static final EventType<RequestedCreateFileEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestedCreateFileEvent.class.getSimpleName());
        }
    }

    private static final String FILE = "file";
    private static final String CREATOR = "creator";

    public RequestedCreateFileEvent(@NotNull Path file, @NotNull FileCreatorDescriptor descriptor) {
        super(EVENT_TYPE);
        setFile(file);
        setDescriptor(descriptor);
    }

    /**
     * Get the descriptor.
     *
     * @return the creator's descriptor.
     */
    public @NotNull FileCreatorDescriptor getDescriptor() {
        return notNull(get(CREATOR));
    }

    /**
     * Set the descriptor.
     *
     * @param descriptor the creator's descriptor.
     */
    public void setDescriptor(@NotNull FileCreatorDescriptor descriptor) {
        set(CREATOR, descriptor);
    }

    /**
     * Get the file.
     *
     * @return the file.
     */
    public @NotNull Path getFile() {
        return notNull(get(FILE));
    }

    /**
     * Set the file.
     *
     * @param file the file.
     */
    public void setFile(@NotNull Path file) {
        set(FILE, file);
    }
}
