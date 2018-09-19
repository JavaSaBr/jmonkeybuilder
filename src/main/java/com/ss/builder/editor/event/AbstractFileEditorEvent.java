package com.ss.builder.editor.event;

import com.ss.builder.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

/**
 * The base implementation of a file editor's event.
 *
 * @author JavaSaBr
 */
public abstract class AbstractFileEditorEvent<S> implements FileEditorEvent {

    /**
     * The event's source.
     */
    @NotNull
    protected final S source;

    protected AbstractFileEditorEvent(@NotNull S source) {
        this.source = source;
    }

    @Override
    @FromAnyThread
    public @NotNull S getSource() {
        return source;
    }
}
