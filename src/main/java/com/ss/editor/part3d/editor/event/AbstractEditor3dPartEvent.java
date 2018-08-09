package com.ss.editor.part3d.editor.event;

import com.ss.editor.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

/**
 * The base implementation of an editor 3d part event.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditor3dPartEvent implements Editor3dPartEvent {

    @NotNull
    private final Object source;

    public AbstractEditor3dPartEvent(@NotNull Object source) {
        this.source = source;
    }

    @Override
    @FromAnyThread
    public @NotNull Object getSource() {
        return source;
    }
}
