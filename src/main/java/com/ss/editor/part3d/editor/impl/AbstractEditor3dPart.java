package com.ss.editor.part3d.editor.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.part3d.editor.Editor3dPart;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of the {@link Editor3dPart} to use inside {@link FileEditor}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditor3dPart<T extends FileEditor> extends AbstractAppState implements Editor3dPart {

    protected static final Logger LOGGER = LoggerManager.getLogger(Editor3dPart.class);

    /**
     * The current application.
     */
    @Nullable
    protected Application application;


    @Override
    @JmeThread
    public void initialize(@NotNull AppStateManager stateManager, @NotNull Application application) {
        super.initialize(stateManager, application);
        this.application = application;
    }

    @Override
    @JmeThread
    public void cleanup() {
        this.application = null;
        super.cleanup();
    }

    /**
     * Get the current application.
     *
     * @return get the current application.
     */
    @JmeThread
    public @NotNull Application requireApplication() {
        return notNull(application);
    }
}
