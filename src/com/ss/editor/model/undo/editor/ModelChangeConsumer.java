package com.ss.editor.model.undo.editor;

import com.jme3.scene.Spatial;

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
}
