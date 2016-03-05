package com.ss.editor.ui.control.model.tree;

/**
 * Интерфейс для реализации слушателя изменений структуры модели.
 *
 * @author Ronn
 */
public interface ModelTreeChangeListener {

    /**
     * Уведомление и обработка перемещения нода.
     */
    public void notifyMoved(final Object prevParent, final Object newParent, final Object node);

    /**
     * Уведомление и обработка изменения узла модели.
     */
    public void notifyChanged(final Object node);

    /**
     * Уведомление и обработка добавления нового узла.
     */
    public void notifyAdded(final Object parent, final Object node);

    /**
     * Уведомление и обработка удаленя нода.
     */
    public void notifyRemoved(final Object node);
}
