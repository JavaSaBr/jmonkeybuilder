package com.ss.editor.ui.event;

import java.util.HashMap;
import java.util.Map;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Базавая реализация события сцены javaFX UI.
 * 
 * @author Ronn
 */
public class SceneEvent extends Event {

	private static final long serialVersionUID = 6827900349094865635L;

	public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.class.getSimpleName());

    /**
     * Мапа с параметрами события.
     */
	private Map<Object, Object> values;

	public SceneEvent(final Object source, final EventType<? extends Event> eventType) {
		super(source, null, eventType);
	}

	public SceneEvent(final EventType<? extends Event> eventType) {
		super(eventType);
	}

    /**
     * Вставка параметра для события.
     *
     * @param key ключ параметра.
     * @param value значение параметра.
     */
    public void set(Object key, Object value) {

        if(values == null) {
            values = new HashMap<>();
        }

        values.put(key, value);
    }

    /**
     * Получение параметра события.
     *
     * @param key ключ параметра.
     * @return значение параметра или null.
     */
    public <T> T get(Object key) {

        if(values == null) {
            return null;
        }

        return (T) values.get(key);
    }
}
