package com.ss.editor.ui.event;

import javafx.event.Event;
import javafx.event.EventType;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

import static rlib.util.ClassUtils.unsafeCast;

/**
 * Базавая реализация события сцены javaFX UI.
 *
 * @author Ronn
 */
public class SceneEvent extends Event {

    private static final long serialVersionUID = 6827900349094865635L;

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.class.getSimpleName());

    /**
     * Таблица с параметрами события.
     */
    private ObjectDictionary<Object, Object> values;

    public SceneEvent(final Object source, final EventType<? extends Event> eventType) {
        super(source, null, eventType);
    }

    public SceneEvent(final EventType<? extends Event> eventType) {
        super(eventType);
    }

    /**
     * Вставка параметра для события.
     *
     * @param key   ключ параметра.
     * @param value значение параметра.
     */
    public void set(final Object key, final Object value) {
        if (values == null) values = DictionaryFactory.newObjectDictionary();
        values.put(key, value);
    }

    /**
     * Получение параметра события.
     *
     * @param key ключ параметра.
     * @return значение параметра или null.
     */
    public <T> T get(final Object key) {
        if (values == null) return null;
        return unsafeCast(values.get(key));
    }
}
