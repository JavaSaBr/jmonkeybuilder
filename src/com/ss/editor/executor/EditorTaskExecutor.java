package com.ss.editor.executor;

/**
 * Интерфейс для реализация исполнителей задач.
 *
 * @author Ronn
 */
public interface EditorTaskExecutor {

    /**
     * Добавление на исполнение задачи.
     *
     * @param task задача.
     */
    public void execute(Runnable task);
}
