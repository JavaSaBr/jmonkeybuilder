package com.ss.editor.ui.event;

/**
 * Интерфейс для пометки, что событие надо завершать только когда оно consumed.
 * 
 * @author Ronn
 */
public interface ConsumeableEvent {

	/**
	 * @return завершено ли событие.
	 */
	public boolean isConsumed();
}
