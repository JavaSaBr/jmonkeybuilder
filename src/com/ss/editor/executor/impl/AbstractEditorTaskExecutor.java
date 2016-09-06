package com.ss.editor.executor.impl;

import com.ss.editor.EditorThread;
import com.ss.editor.executor.EditorTaskExecutor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import rlib.concurrent.lock.LockFactory;
import rlib.concurrent.util.ConcurrentUtils;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.Lockable;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The base implementation of the {@link EditorThreadExecutor}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditorTaskExecutor extends EditorThread implements EditorTaskExecutor, Lockable {

    protected static final Logger LOGGER = LoggerManager.getLogger(EditorTaskExecutor.class);

    /**
     * The array of task to execute for each iteration.
     */
    protected final Array<Runnable> execute;

    /**
     * The array of executed task of the iteration.
     */
    protected final Array<Runnable> executed;

    /**
     * The array of task to execute.
     */
    protected final Array<Runnable> waitTasks;

    /**
     * Is this executor waiting new tasks.
     */
    protected final AtomicBoolean wait;

    /**
     * The synchronizer.
     */
    protected final Lock lock;

    public AbstractEditorTaskExecutor() {
        this.execute = createExecuteArray();
        this.executed = createExecuteArray();
        this.waitTasks = createExecuteArray();
        this.lock = LockFactory.newAtomicLock();
        this.wait = new AtomicBoolean(false);
    }

    protected Array<Runnable> createExecuteArray() {
        return ArrayFactory.newArray(Runnable.class);
    }

    @Override
    public void execute(final Runnable task) {
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
     */
    protected abstract void doExecute(final Array<Runnable> execute, final Array<Runnable> executed);

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
