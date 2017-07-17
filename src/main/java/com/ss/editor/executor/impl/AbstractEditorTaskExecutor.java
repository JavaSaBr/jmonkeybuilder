package com.ss.editor.executor.impl;

import com.ss.editor.EditorThread;
import com.ss.editor.executor.EditorTaskExecutor;
import com.ss.rlib.concurrent.lock.LockFactory;
import com.ss.rlib.concurrent.lock.Lockable;
import com.ss.rlib.concurrent.util.ConcurrentUtils;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

/**
 * The base implementation of the {@link EditorThreadExecutor}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditorTaskExecutor extends EditorThread implements EditorTaskExecutor, Lockable {

    /**
     * The constant LOGGER.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(EditorTaskExecutor.class);

    /**
     * The array of task to execute for each iteration.
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

    /**
     * Instantiates a new Abstract editor task executor.
     */
    public AbstractEditorTaskExecutor() {
        this.execute = createExecuteArray();
        this.executed = createExecuteArray();
        this.waitTasks = createExecuteArray();
        this.lock = LockFactory.newAtomicLock();
        this.wait = new AtomicBoolean(false);
    }

    @NotNull
    private Array<Runnable> createExecuteArray() {
        return ArrayFactory.newArray(Runnable.class);
    }

    @Override
    public void execute(@NotNull final Runnable task) {
        lock();
        try {

            waitTasks.add(task);
            if (!wait.get()) return;

            synchronized (wait) {
                if (wait.compareAndSet(true, false)) {
                    ConcurrentUtils.notifyAllInSynchronize(wait);
                }
            }

        } finally {
            unlock();
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

            if (execute.isEmpty()) continue;
            doExecute(execute, executed);
            if (executed.isEmpty()) continue;

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
