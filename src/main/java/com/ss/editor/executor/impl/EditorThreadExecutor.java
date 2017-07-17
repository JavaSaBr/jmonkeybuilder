package com.ss.editor.executor.impl;

import com.ss.editor.annotation.JMEThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.util.EditorUtil;

import org.jetbrains.annotations.NotNull;

import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.util.ArrayUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.array.ConcurrentArray;

/**
 * The executor to execute tasks in the editor thread.
 *
 * @author JavaSaBr
 */
public class EditorThreadExecutor {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(EditorThreadExecutor.class);

    @NotNull
    private static final EditorThreadExecutor INSTANCE = new EditorThreadExecutor();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static EditorThreadExecutor getInstance() {
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

    private EditorThreadExecutor() {
        this.waitTasks = ArrayFactory.newConcurrentAtomicARSWLockArray(Runnable.class);
        this.execute = ArrayFactory.newArray(Runnable.class);
    }

    /**
     * Add a task to execute.
     *
     * @param task the task.
     */
    @FromAnyThread
    public void addToExecute(@NotNull final Runnable task) {
        ArrayUtils.runInWriteLock(waitTasks, task, (tasks, toAdd) -> tasks.add(task));
    }

    /**
     * Execute waited tasks.
     */
    @JMEThread
    public void execute() {
        if (waitTasks.isEmpty()) return;

        ArrayUtils.runInWriteLock(waitTasks, execute, ArrayUtils::move);

        try {
            execute.forEach(EditorThreadExecutor::execute);
        } finally {
            execute.clear();
        }
    }

    @JMEThread
    private static void execute(@NotNull final Runnable runnable) {
        try {
            runnable.run();
        } catch (final Exception e) {
            EditorUtil.handleException(LOGGER, getInstance(), e);
        }
    }
}
