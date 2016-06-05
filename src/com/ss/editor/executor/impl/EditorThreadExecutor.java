package com.ss.editor.executor.impl;

import rlib.util.ArrayUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реализация исполнителя задач в основном потооке.
 *
 * @author Ronn
 */
public class EditorThreadExecutor {

    private static final EditorThreadExecutor INSTANCE = new EditorThreadExecutor();

    public static EditorThreadExecutor getInstance() {
        return INSTANCE;
    }

    /**
     * Ожидающие исполнения задачи.
     */
    private final Array<Runnable> waitTasks;

    /**
     * Задачи которые должны сейчас выполнится.
     */
    private final Array<Runnable> execute;

    public EditorThreadExecutor() {
        this.waitTasks = ArrayFactory.newConcurrentAtomicArray(Runnable.class);
        this.execute = ArrayFactory.newArray(Runnable.class);
    }

    /**
     * Добавление задачи на выполнение.
     *
     * @param task задача на выполнение.
     */
    public void addToExecute(final Runnable task) {
        ArrayUtils.runInWriteLock(getWaitTasks(), task, Array::add);
    }

    /**
     * Выполнить ожидающие задачи.
     */
    public void execute() {

        final Array<Runnable> waitTasks = getWaitTasks();
        if (waitTasks.isEmpty()) return;

        final Array<Runnable> execute = getExecute();

        ArrayUtils.runInWriteLock(waitTasks, execute, ArrayUtils::move);

        try {
            execute.forEach(Runnable::run);
        } finally {
            execute.clear();
        }
    }

    /**
     * @return задачи которые должны сейчас выполнится.
     */
    private Array<Runnable> getExecute() {
        return execute;
    }

    /**
     * @return ожидающие исполнения задачи.
     */
    private Array<Runnable> getWaitTasks() {
        return waitTasks;
    }
}
