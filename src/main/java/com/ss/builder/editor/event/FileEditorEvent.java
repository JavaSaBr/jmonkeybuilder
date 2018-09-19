package com.ss.builder.editor.event;

import com.ss.builder.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement a file editor's event.
 *
 * @author Alex Brui
 */
public interface FileEditorEvent {

    /**
     * Get source of this event.
     *
     * @return the source or reference to this event.
     */
    @FromAnyThread
    default @NotNull Object getSource() {
        return this;
    }
}
