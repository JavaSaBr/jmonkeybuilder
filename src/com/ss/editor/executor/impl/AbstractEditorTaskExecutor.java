package com.ss.editor.executor.impl;

import com.ss.editor.EditorThread;
import com.ss.editor.executor.EditorTaskExecutor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import rlib.concurrent.lock.LockFactory;
import rlib.concurrent.util.ConcurrentUtils;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.Synchronized;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Базовая реализация исполнителя задач.
 *
 * @author Ronn
 */
public abstract class AbstractEditorTaskExecutor extends EditorThread implements EditorTaskExecutor, Synchronized {

    protected static final Logger LOGGER = LoggerManager.getLogger(EditorTaskExecutor.class);

    /**
     * Список задач на исполнение во время ближайшей фазы.
     */
    private final Array<Runnable> execute;

    /**
     * Список выполненных задач.
     */
    private final Array<Runnable> executed;

    /**
     * Список ожидающих задач на исполнении.
     */
    private final Array<Runnable> waitTasks;

    /**
     * Находится ли исполнитель в ожидании.
     */
    private final AtomicBoolean wait;

    /**
     * Блокировщик.
     */
    private final Lock lock;

    public AbstractEditorTaskExecutor() {
        this.execute = createExecuteArray();
        this.executed = createExecuteArray();
        this.waitTasks = createExecuteArray();
        this.lock = LockFactory.newPrimitiveAtomicLock();
        this.wait = new AtomicBoolean(false);
    }

    protected Array<Runnable> createExecuteArray() {
        return ArrayFactory.newArray(Runnable.class);
    }

    @Override
    public void execute(final Runnable task) {
        lock();
        try {

            final Array<Runnable> waitTasks = getWaitTasks();
            waitTasks.add(task);

            final AtomicBoolean wait = getWait();

            if (wait.get()) {
                synchronized (wait) {
                    if (wait.compareAndSet(true, false)) {
                        ConcurrentUtils.notifyAllInSynchronize(wait);
                    }
                }
            }

        } finally {
            unlock();
        }
    }

    /**
     * @return список задач на исполнение во время ближайшей фазы.
     */
    public Array<Runnable> getExecute() {
        return execute;
    }

    /**
     * @return список выполненных задач.
     */
    public Array<Runnable> getExecuted() {
        return executed;
    }

    /**
     * @return находится ли исполнитель в ожидании.
     */
    public AtomicBoolean getWait() {
        return wait;
    }

    /**
     * @return список ожидающих задач на исполнении.
     */
    public Array<Runnable> getWaitTasks() {
        return waitTasks;
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
