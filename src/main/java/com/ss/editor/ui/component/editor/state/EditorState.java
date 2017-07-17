package com.ss.editor.ui.component.editor.state;

import java.io.Serializable;

/**
 * The interface for implementing a state container of Editor.
 *
 * @author JavaSaBr
 */
public interface EditorState extends Serializable {

    /**
     * Sets change handler.
     *
     * @param handle the change handler.
     */
    void setChangeHandler(final Runnable handle);
}
