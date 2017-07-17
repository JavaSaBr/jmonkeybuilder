package com.ss.editor.executor;

import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement a task executor.
 *
 * @author JavaSaBr
 */
public interface EditorTaskExecutor {

    /**
     * Add a new task.
     *
     * @param task the new task.
     */
    void execute(@NotNull Runnable task);
}
