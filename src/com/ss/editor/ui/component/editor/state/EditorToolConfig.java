package com.ss.editor.ui.component.editor.state;

/**
 * The interface implementing a state of editor tool.
 *
 * @author JavaSaBr
 */
public interface EditorToolConfig {

    int getToolWidth();

    void setToolWidth(final int toolWidth);

    boolean isToolCollapsed();

    void setToolCollapsed(final boolean toolCollapsed);
}
