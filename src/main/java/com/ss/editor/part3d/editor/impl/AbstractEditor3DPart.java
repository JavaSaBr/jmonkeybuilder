package com.ss.editor.part3d.editor.impl;

import com.jme3.app.state.AbstractAppState;
import com.jme3.scene.Node;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.part3d.editor.Editor3DPart;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import org.jetbrains.annotations.NotNull;

/**
 * The base implementation of the {@link Editor3DPart} to use inside {@link FileEditor}.
 *
 * @param <T> the type of file editor
 * @author JavaSaBr
 */
public abstract class AbstractEditor3DPart<T extends FileEditor> extends AbstractAppState implements Editor3DPart {

    /**
     * The logger.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(Editor3DPart.class);

    /**
     * The executor manager.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The owner editor.
     */
    @NotNull
    private final T fileEditor;

    /**
     * The root node.
     */
    @NotNull
    private final Node stateNode;

    public AbstractEditor3DPart(@NotNull final T fileEditor) {
        this.fileEditor = fileEditor;
        this.stateNode = new Node(getClass().getSimpleName());
    }

    /**
     * Get the state node.
     *
     * @return the root node.
     */
    @FromAnyThread
    protected @NotNull Node getStateNode() {
        return stateNode;
    }

    /**
     * Get the file editor.
     *
     * @return the owner editor.
     */
    @FromAnyThread
    protected @NotNull T getFileEditor() {
        return fileEditor;
    }
}
