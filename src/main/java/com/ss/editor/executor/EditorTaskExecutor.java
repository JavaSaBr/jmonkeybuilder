package com.ss.editor.executor;

import com.ss.editor.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement a task executor.
 *
 * @author JavaSaBr
 */
public interface EditorTaskExecutor {

    /**
     * Add the new task.
     *
     * @param task the new task.
     */
    @FromAnyThread
    void execute(@NotNull Runnable task);
}
