package com.ss.editor.ui.component;

import javafx.scene.input.KeyEvent;

/**
 * Интерфейс для реализации компонента сцены экрана.
 * 
 * @author Ronn
 */
public interface ScreenComponent {

	public default void notifyPostActivate() {
	}

	public default void notifyPreActivate() {
	}

	public default void notifyPostDeactivate() {
	}

	public default void notifyPreDeactivate() {
	}

	/**
	 * @return индентификатор компонента.
	 */
	public default String getComponentId() {
		return null;
	}

	public default void requestHide() {
	}

    public default void notifyKeyReleased(KeyEvent event) {
    }
}
