package com.ss.editor.ui.event;

import static rlib.util.ClassUtils.unsafeCast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.event.Event;
import javafx.event.EventType;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

/**
 * The base implementation of an event in the javaFX UI.
 *
 * @author JavaSaBr
 */
public class SceneEvent extends Event {

    private static final long serialVersionUID = 6827900349094865635L;

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.class.getSimpleName());

    /**
     * The parameters.
     */
    private ObjectDictionary<Object, Object> values;

    public SceneEvent(@Nullable final Object source, @NotNull final EventType<? extends Event> eventType) {
        super(source, null, eventType);
    }

    public SceneEvent(@NotNull final EventType<? extends Event> eventType) {
        super(eventType);
    }

    /**
     * Put a new parameter.
     *
     * @param key   the key.
     * @param value the value.
     */
    public void set(@NotNull final Object key, @NotNull final Object value) {
        if (values == null) values = DictionaryFactory.newObjectDictionary();
        values.put(key, value);
    }

    /**
     * Get a value by a key.
     *
     * @param key the key.
     * @return the value or null.
     */
    @Nullable
    public <T> T get(@NotNull final Object key) {
        if (values == null) return null;
        final Object object = values.get(key);
        if (object == null) return null;
        return unsafeCast(object);
    }
}
