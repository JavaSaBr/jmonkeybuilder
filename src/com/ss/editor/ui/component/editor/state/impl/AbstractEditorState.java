package com.ss.editor.ui.component.editor.state.impl;

import static java.lang.Math.abs;

import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.EditorToolConfig;

/**
 * The base implementation of a state container for an editor.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditorState implements EditorState, EditorToolConfig {

    /**
     * The change handler.
     */
    protected transient volatile Runnable changeHandler;

    protected volatile int toolWidth;

    protected volatile boolean toolCollapsed;

    public AbstractEditorState() {
        this.toolWidth = 200;
        this.toolCollapsed = false;
    }

    @Override
    public void setChangeHandler(final Runnable changeHandler) {
        this.changeHandler = changeHandler;
    }

    /**
     * @return the change handler.
     */
    private Runnable getChangeHandler() {
        return changeHandler;
    }

    @Override
    public int getToolWidth() {
        return toolWidth;
    }

    @Override
    public void setToolWidth(final int toolWidth) {
        final boolean changed = abs(getToolWidth() - toolWidth) > 3;
        this.toolWidth = toolWidth;
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }

    @Override
    public boolean isToolCollapsed() {
        return toolCollapsed;
    }

    @Override
    public void setToolCollapsed(final boolean toolCollapsed) {
        final boolean changed = isToolCollapsed() != toolCollapsed;
        this.toolCollapsed = toolCollapsed;
        if (changed && changeHandler != null) {
            changeHandler.run();
        }
    }
}
