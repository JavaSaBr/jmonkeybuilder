package com.ss.editor.executor.impl;

import com.ss.editor.EditorThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.executor.EditorTaskExecutor;
import com.ss.rlib.common.concurrent.lock.LockFactory;
import com.ss.rlib.common.concurrent.lock.Lockable;
import com.ss.rlib.common.concurrent.util.ConcurrentUtils;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

/**
 * The base implementation of the {@link JmeThreadExecutor}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditorTaskExecutor extends EditorThread implements EditorTaskExecutor, Lockable {

    /**
     * The logger.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(EditorTaskExecutor.class);

    /**
     * The task list to execute for each iteration.
     */
    @NotNull
    protected final Array<Runnable> execute;

    /**
     * The array of executed task of the iteration.
     */
    @NotNull
    final Array<Runnable> executed;

    /**
     * The array of task to execute.
     */
    @NotNull
    final Array<Runnable> waitTasks;

    /**
     * Is this executor waiting new tasks.
     */
    @NotNull
    final AtomicBoolean wait;

    /**
     * The synchronizer.
     */
    @NotNull
    private final Lock lock;

    public AbstractEditorTaskExecutor() {
        this.execute = createExecuteArray();
        this.executed = createExecuteArray();
        this.waitTasks = createExecuteArray();
        this.lock = LockFactory.newAtomicLock();
        this.wait = new AtomicBoolean(false);
    }

    /**
     * Create a new execute tasks array.
     *
     * @return the new execute tasks array.
     */
    @FromAnyThread
    private @NotNull Array<Runnable> createExecuteArray() {
        return ArrayFactory.newArray(Runnable.class);
    }

    @Override
    @FromAnyThread
    public void execute(@NotNull final Runnable task) {
        lock();
        try {
            waitTasks.add(task);
            if (!wait.get()) {
                return;
            }

            synchronized (wait) {
                if (wait.compareAndSet(true, false)) {
                    ConcurrentUtils.notifyAllInSynchronize(wait);
                }
            }

        } finally {
            unlock();
        }

        if (!wait.get()) {
            return;
        }

        synchronized (wait) {
            if (wait.compareAndSet(true, false)) {
                ConcurrentUtils.notifyAllInSynchronize(wait);
            }
        }
    }

    /**
     * Execute the array of tasks.
     *
     * @param execute  the execute
     * @param executed the executed
     */
    protected abstract void doExecute(@NotNull final Array<Runnable> execute, @NotNull final Array<Runnable> executed);

    @Override
    public void run() {
        while (true) {

            executed.clear();
            execute.clear();

            lock();
            try {

                if (waitTasks.isEmpty()) {
                    wait.getAndSet(true);
                } else {
                    execute.addAll(waitTasks);
                }

            } finally {
                unlock();
            }

            if (wait.get()) {
                synchronized (wait) {
                    if (wait.get()) {
                        ConcurrentUtils.waitInSynchronize(wait);
                    }
                }
            }

            if (execute.isEmpty()) {
                continue;
            }

            doExecute(execute, executed);

            if (executed.isEmpty()) {
                continue;
            }

            lock();
            try {
                waitTasks.removeAll(executed);
            } finally {
                unlock();
            }
        }
    }

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void unlock() {
        lock.unlock();
    }
}
