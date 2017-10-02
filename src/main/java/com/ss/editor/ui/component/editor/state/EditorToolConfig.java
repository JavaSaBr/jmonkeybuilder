package com.ss.editor.ui.component.editor.state;

import com.ss.editor.annotation.FXThread;

/**
 * The interface implementing a state of editor tool.
 *
 * @author JavaSaBr
 */
public interface EditorToolConfig {

    /**
     * Gets tool width.
     *
     * @return the tool width
     */
    @FXThread
    int getToolWidth();

    /**
     * Sets tool width.
     *
     * @param toolWidth the tool width
     */
    @FXThread
    void setToolWidth(final int toolWidth);

    /**
     * Is tool collapsed boolean.
     *
     * @return the boolean
     */
    @FXThread
    boolean isToolCollapsed();

    /**
     * Sets tool collapsed.
     *
     * @param toolCollapsed the tool collapsed
     */
    @FXThread
    void setToolCollapsed(final boolean toolCollapsed);
}
