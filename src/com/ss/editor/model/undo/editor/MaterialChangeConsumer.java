package com.ss.editor.model.undo.editor;

import com.jme3.material.Material;

/**
 * Интерфейс для взаимодействия операциям с асбтрактным редактором материала.
 *
 * @author Ronn
 */
public interface MaterialChangeConsumer {

    /**
     * @return текущий материал редактора.
     */
    public Material getCurrentMaterial();

    /**
     * Уведомление об изменении параметра материала.
     */
    public void notifyChangeParam(final String paramName);

    /**
     * Уведомление об изминении настроек рендера.
     */
    public void notifyChangedRenderState();
}
