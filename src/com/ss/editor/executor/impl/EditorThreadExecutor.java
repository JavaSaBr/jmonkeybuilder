package com.ss.editor.executor.impl;

import rlib.util.ArrayUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.array.ConcurrentArray;

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
    private final ConcurrentArray<Runnable> waitTasks;

    /**
     * Задачи которые должны сейчас выполнится.
     */
    private final Array<Runnable> execute;

    public EditorThreadExecutor() {
        this.waitTasks = ArrayFactory.newConcurrentAtomicARSWLockArray(Runnable.class);
        this.execute = ArrayFactory.newArray(Runnable.class);
    }

    /**
     * Добавление задачи на выполнение.
     *
     * @param task задача на выполнение.
     */
    public void addToExecute(final Runnable task) {
        ArrayUtils.runInWriteLock(waitTasks, task, Array::add);
    }

    /**
     * Выполнить ожидающие задачи.
     */
    public void execute() {
        if (waitTasks.isEmpty()) return;

        ArrayUtils.runInWriteLock(waitTasks, execute, ArrayUtils::move);

        try {
            execute.forEach(Runnable::run);
        } finally {
            execute.clear();
        }
    }
}
