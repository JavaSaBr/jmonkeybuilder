package com.ss.editor.model.undo;

/**
 * Интерфейс для поддержки редактора с отменяемыми изменениями.
 *
 * @author Ronn
 */
public interface UndoableEditor {

    /**
     * Увеличение счетчка изменений.
     */
    public void incrementChange();

    /**
     * Уменьшение счетчика изменений.
     */
    public void decrementChange();
}
