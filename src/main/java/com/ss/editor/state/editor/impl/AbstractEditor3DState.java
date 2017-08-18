package com.ss.editor.state.editor.impl;

import com.jme3.app.state.AbstractAppState;
import com.jme3.scene.Node;
import com.ss.editor.Editor;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.state.editor.Editor3DState;
import com.ss.editor.ui.component.editor.FileEditor;

import org.jetbrains.annotations.NotNull;

import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;

/**
 * The base implementation of the {@link Editor3DState} to use inside {@link FileEditor}.
 *
 * @param <T> the type of file editor
 * @author JavaSaBr
 */
public abstract class AbstractEditor3DState<T extends FileEditor> extends AbstractAppState implements Editor3DState {

    /**
     * The constant LOGGER.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(Editor3DState.class);

    /**
     * The constant EXECUTOR_MANAGER.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The constant EDITOR.
     */
    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

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

    /**
     * Create a file editor app state.
     *
     * @param fileEditor the editor.
     */
    public AbstractEditor3DState(@NotNull final T fileEditor) {
        this.fileEditor = fileEditor;
        this.stateNode = new Node(getClass().getSimpleName());
    }

    /**
     * Gets state node.
     *
     * @return the root node.
     */
    @FromAnyThread
    protected @NotNull Node getStateNode() {
        return stateNode;
    }

    /**
     * Gets file editor.
     *
     * @return the owner editor.
     */
    @FromAnyThread
    protected @NotNull T getFileEditor() {
        return fileEditor;
    }
}
