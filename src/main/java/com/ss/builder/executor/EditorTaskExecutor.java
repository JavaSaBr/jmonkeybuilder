package com.ss.builder.executor;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

/**
 * The interface to implement a task executor.
 *
 * @author JavaSaBr
 */
public interface EditorTaskExecutor extends Executor {

    /**
     * Add the new task.
     *
     * @param task the new task.
     */
    @Override
    @FromAnyThread
    void execute(@NotNull Runnable task);
}
