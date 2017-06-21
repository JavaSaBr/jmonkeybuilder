package com.ss.editor.model.undo.editor;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.EditorOperation;

import org.jetbrains.annotations.NotNull;

/**
 * The interface to notify about any changes.
 *
 * @author JavaSaBr
 */
public interface ChangeConsumer {

    /**
     * Execute the operation.
     *
     * @param operation the operation
     */
    @FromAnyThread
    void execute(@NotNull EditorOperation operation);
}
