package com.ss.editor.part3d.editor.event.impl;

import com.ss.editor.part3d.editor.event.AbstractEditor3dPartEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The event about finishing editing in a scene editor.
 *
 * @author JavaSaBr
 */
public class FinishEditingEvent extends AbstractEditor3dPartEvent {

    public FinishEditingEvent(@NotNull Object source) {
        super(source);
    }
}
