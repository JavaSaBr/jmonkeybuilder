package com.ss.editor.ui.component.editor.state;

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
    int getToolWidth();

    /**
     * Sets tool width.
     *
     * @param toolWidth the tool width
     */
    void setToolWidth(final int toolWidth);

    /**
     * Is tool collapsed boolean.
     *
     * @return the boolean
     */
    boolean isToolCollapsed();

    /**
     * Sets tool collapsed.
     *
     * @param toolCollapsed the tool collapsed
     */
    void setToolCollapsed(final boolean toolCollapsed);
}
