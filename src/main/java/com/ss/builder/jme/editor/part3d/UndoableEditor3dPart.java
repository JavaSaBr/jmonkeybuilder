package com.ss.builder.editor.part3d;

import com.ss.builder.annotation.JmeThread;
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
