package com.ss.builder.jme.editor.part3d.event.impl;

import com.ss.builder.jme.editor.part3d.event.AbstractEditor3dPartEvent;
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
