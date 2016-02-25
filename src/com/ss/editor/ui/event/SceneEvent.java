package com.ss.editor.ui.event;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import rlib.util.pools.Foldable;

/**
 * Базавая реализация события сцены javaFX UI.
 * 
 * @author Ronn
 */
public class SceneEvent extends Event implements Foldable {

	private static final long serialVersionUID = 6827900349094865635L;

	public static final EventType<SceneEvent> EVENT_TYPE = new EventType<SceneEvent>(SceneEvent.class.getSimpleName());

	public SceneEvent(final Object source, final EventTarget target, final EventType<? extends Event> eventType) {
		super(source, target, eventType);
	}

	public SceneEvent(final EventType<? extends Event> eventType) {
		super(eventType);
	}

	/**
	 * @param target новая цель события.
	 */
	public void setTarget(final EventTarget target) {
		this.target = target;
	}

	@Override
	public void finalyze() {

		final EventTarget target = getTarget();

		if(target instanceof Foldable) {
			((Foldable) target).release();
		}

		setTarget(null);
	}

	@Override
	public void reinit() {
		this.consumed = false;
	}
}
