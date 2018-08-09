package com.ss.editor.part3d.editor.impl.scene.event;

import com.ss.editor.part3d.editor.event.AbstractEditor3dPartEvent;
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
