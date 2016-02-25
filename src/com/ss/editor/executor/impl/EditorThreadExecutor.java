package com.ss.editor.executor.impl;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реализация исполнителя задач в основном потооке.
 *
 * @author Ronn
 */
public class EditorThreadExecutor {

    private static final Logger LOGGER = LoggerManager.getLogger(EditorThreadExecutor.class);

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

        final Array<Runnable> waitTasks = getWaitTasks();
        waitTasks.writeLock();
        try {
            waitTasks.add(task);
        } finally {
            waitTasks.writeUnlock();
        }
    }

    /**
     * Выполнить ожидающие задачи.
     */
    public void execute() {

        final Array<Runnable> waitTasks = getWaitTasks();

        if (waitTasks.isEmpty()) {
            return;
        }

        final Array<Runnable> execute = getExecute();
        try {

            waitTasks.writeLock();
            try {
                execute.addAll(waitTasks);
                waitTasks.clear();
            } finally {
                waitTasks.writeUnlock();
            }

            for (final Runnable task : execute.array()) {

                if (task == null) {
                    break;
                }

                task.run();
            }

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
