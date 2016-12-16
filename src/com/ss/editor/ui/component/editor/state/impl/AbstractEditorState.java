package com.ss.editor.ui.component.editor.state.impl;

import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.editor.ui.component.editor.state.EditorToolConfig;

/**
 * The base implementation of a state container for an editor.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditorState implements EditorState, EditorToolConfig {

    protected volatile int toolWidth;

    protected volatile boolean toolCollapsed;

    public AbstractEditorState() {
        this.toolWidth = 200;
        this.toolCollapsed = false;
    }

    @Override
    public int getToolWidth() {
        return toolWidth;
    }

    @Override
    public void setToolWidth(final int toolWidth) {
        this.toolWidth = toolWidth;
    }

    @Override
    public boolean isToolCollapsed() {
        return toolCollapsed;
    }

    @Override
    public void setToolCollapsed(final boolean toolCollapsed) {
        this.toolCollapsed = toolCollapsed;
    }
}
