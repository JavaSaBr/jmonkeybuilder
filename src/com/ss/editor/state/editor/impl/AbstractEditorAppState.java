package com.ss.editor.state.editor.impl;

import com.jme3.app.state.AbstractAppState;
import com.jme3.scene.Node;
import com.ss.editor.Editor;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.state.editor.EditorAppState;
import com.ss.editor.ui.component.editor.FileEditor;

import org.jetbrains.annotations.NotNull;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;

/**
 * The base implementation of the {@link EditorAppState} for using inside {@link FileEditor}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditorAppState<T extends FileEditor> extends AbstractAppState implements EditorAppState {

    protected static final Logger LOGGER = LoggerManager.getLogger(EditorAppState.class);

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
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
    public AbstractEditorAppState(@NotNull final T fileEditor) {
        this.fileEditor = fileEditor;
        this.stateNode = new Node(getClass().getSimpleName());
    }

    /**
     * @return the root node.
     */
    @NotNull
    @FromAnyThread
    protected Node getStateNode() {
        return stateNode;
    }

    /**
     * @return the owner editor.
     */
    @NotNull
    @FromAnyThread
    protected T getFileEditor() {
        return fileEditor;
    }
}
