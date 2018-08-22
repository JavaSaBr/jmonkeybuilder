package com.ss.builder.jme.editor.part3d.event.impl;

import com.ss.builder.jme.editor.part3d.event.AbstractEditor3dPartEvent;
import com.ss.builder.jme.editor.part3d.event.AbstractEditor3dPartEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The event about starting editing in a scene editor.
 *
 * @author JavaSaBr
 */
public class StartEditingEvent extends AbstractEditor3dPartEvent {

    public StartEditingEvent(@NotNull Object source) {
        super(source);
    }
}
