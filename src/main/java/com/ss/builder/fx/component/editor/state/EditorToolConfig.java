package com.ss.builder.ui.component.editor.state;

import com.ss.builder.annotation.FxThread;
import com.ss.editor.annotation.FxThread;

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
    @FxThread
    int getToolWidth();

    /**
     * Sets tool width.
     *
     * @param toolWidth the tool width
     */
    @FxThread
    void setToolWidth(final int toolWidth);

    /**
     * Is tool collapsed boolean.
     *
     * @return the boolean
     */
    @FxThread
    boolean isToolCollapsed();

    /**
     * Sets tool collapsed.
     *
     * @param toolCollapsed the tool collapsed
     */
    @FxThread
    void setToolCollapsed(final boolean toolCollapsed);
}
