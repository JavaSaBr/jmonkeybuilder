package com.ss.builder.jme.editor.part3d.control.impl;

import com.ss.builder.annotation.JmeThread;
import com.ss.builder.jme.editor.part3d.control.Editor3dPartControl;
import com.ss.builder.jme.editor.part3d.event.Editor3dPartEvent;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.jme.editor.part3d.control.Editor3dPartControl;
import com.ss.builder.jme.editor.part3d.ExtendableEditor3dPart;
import com.ss.builder.jme.editor.part3d.event.Editor3dPartEvent;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import org.jetbrains.annotations.NotNull;

/**
 * The base implementation of {@link Editor3dPartControl}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditor3dPartControl<E extends ExtendableEditor3dPart> implements Editor3dPartControl {

    protected static final Logger LOGGER = LoggerManager.getLogger(Editor3dPartControl.class);

    /**
     * The editor's 3d part.
     */
    @NotNull
    protected final E editor3dPart;

    protected AbstractEditor3dPartControl(@NotNull E editor3dPart) {
        this.editor3dPart = editor3dPart;
    }

    @Override
    @JmeThread
    public void notify(@NotNull Editor3dPartEvent event) {
        if (event.getSource() != this) {
            notifyImpl(event);
        }
    }

    /**
     * Notify this 3d part about some events.
     *
     * @param event the event.
     */
    @JmeThread
    protected void notifyImpl(@NotNull Editor3dPartEvent event) {
    }
}
