package com.ss.editor.part3d.editor.impl;

import com.jme3.app.state.AbstractAppState;
import com.jme3.scene.Node;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.part3d.editor.Editor3dPart;
import com.ss.editor.ui.component.editor.FileEditor;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import org.jetbrains.annotations.NotNull;

/**
 * The base implementation of the {@link Editor3dPart} to use inside {@link FileEditor}.
 *
 * @param <T> the type of file editor
 * @author JavaSaBr
 */
public abstract class AbstractEditor3dPart<T extends FileEditor> extends AbstractAppState implements Editor3dPart {

    protected static final Logger LOGGER = LoggerManager.getLogger(Editor3dPart.class);

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

    public AbstractEditor3dPart(@NotNull T fileEditor) {
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
