package com.ss.editor.ui.component;

/**
 * Интерфейс для реализации компонента сцены экрана.
 *
 * @author Ronn
 */
public interface ScreenComponent {

    /**
     * Gets component id.
     *
     * @return индентификатор компонента.
     */
    public default String getComponentId() {
        return null;
    }

    /**
     * Уведомление о завершении построения сцены.
     */
    public default void notifyFinishBuild() {
    }
}
