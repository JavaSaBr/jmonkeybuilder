package com.ss.builder.executor.impl;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.util.EditorUtils;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.ArrayUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.array.ConcurrentArray;
import org.jetbrains.annotations.NotNull;

/**
 * The executor to execute tasks in the editor thread.
 *
 * @author JavaSaBr
 */
public class JmeThreadExecutor {

    private static final Logger LOGGER = LoggerManager.getLogger(JmeThreadExecutor.class);

    private static final JmeThreadExecutor INSTANCE = new JmeThreadExecutor();

    @FromAnyThread
    public static @NotNull JmeThreadExecutor getInstance() {
        return INSTANCE;
    }

    /**
     * The list of waited tasks.
     */
    @NotNull
    private final ConcurrentArray<Runnable> waitTasks;

    /**
     * The list with tasks to execute.
     */
    @NotNull
    private final Array<Runnable> execute;

    private JmeThreadExecutor() {
        this.waitTasks = ArrayFactory.newConcurrentAtomicARSWLockArray(Runnable.class);
        this.execute = ArrayFactory.newArray(Runnable.class);
    }

    /**
     * Add a task to execute.
     *
     * @param task the task.
     */
    @FromAnyThread
    public void addToExecute(@NotNull Runnable task) {
        waitTasks.runInWriteLock(task, (tasks, toAdd) -> tasks.add(task));
    }

    /**
     * Execute waited tasks.
     */
    @JmeThread
    public void execute() {

        if (waitTasks.isEmpty()) {
            return;
        }

        waitTasks.runInWriteLock(execute, ArrayUtils::move);
        try {
            execute.forEach(JmeThreadExecutor::execute);
        } finally {
            execute.clear();
        }
    }

    @JmeThread
    private static void execute(@NotNull Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            EditorUtils.handleException(LOGGER, getInstance(), e);
        }
    }
}
