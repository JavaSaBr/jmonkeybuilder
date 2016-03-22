package com.ss.editor.model.undo.editor;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.EditorOperation;

/**
 * Интерфейс для взаимодействия операциям с асбтрактным редактором материала.
 *
 * @author Ronn
 */
public interface ModelChangeConsumer {

    /**
     * @return текущая модель в редакторе.
     */
    public Spatial getCurrentModel();

    /**
     * Уведомление об изменении свойства модели.
     */
    public void notifyChangeProperty(final Object object, final String propertyName);

    /**
     * Уведомление об добавлении нового дочернего элемента.
     */
    public void notifyAddedChild(final Node parent, final Spatial added);

    /**
     * Уведомление об удалении дочернего элемента.
     */
    public void notifyRemovedChild(final Node parent, final Spatial removed);

    /**
     * Уведомление о замене одной части модели на другую.
     */
    public void notifyReplaced(final Node parent, final Spatial oldChild, final Spatial newChild);

    /**
     * Уведомление и обработка перемещения нода.
     */
    public void notifyMoved(final Node prevParent, final Node newParent, final Spatial node, int index);

    /**
     * Выполнение операции.
     */
    public void execute(final EditorOperation operation);
}
