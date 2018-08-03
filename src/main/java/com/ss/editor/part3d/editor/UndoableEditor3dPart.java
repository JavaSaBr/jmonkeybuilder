package com.ss.editor.part3d.editor;

import com.ss.editor.annotation.JmeThread;

/**
 * The interface to mark an editor 3d part that it supports undo/redo methods.
 *
 * @author JavaSaBr
 */
public interface UndoableEditor3dPart extends Editor3dPart {

    /**
     * Undo the previous operation.
     */
    @JmeThread
    void undo();

    /**
     * Redo the last operation.
     */
    @JmeThread
    void redo();
}
