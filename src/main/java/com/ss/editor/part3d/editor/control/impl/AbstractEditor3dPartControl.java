package com.ss.editor.part3d.editor.control.impl;

import com.ss.editor.part3d.editor.ExtendableEditor3dPart;
import com.ss.editor.part3d.editor.control.Editor3dPartControl;
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
}
