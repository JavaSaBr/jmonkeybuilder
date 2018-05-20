package com.ss.editor.ui.event;

import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import javafx.event.Event;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of an event in the javaFX UI.
 *
 * @author JavaSaBr
 */
public class SceneEvent extends Event {

    private static final long serialVersionUID = 6827900349094865635L;

    /**
     * The constant EVENT_TYPE.
     */
    @NotNull
    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.class.getSimpleName());

    /**
     * The parameters.
     */
    @Nullable
    private ObjectDictionary<Object, Object> values;

    public SceneEvent(@Nullable final Object source, @NotNull final EventType<? extends Event> eventType) {
        super(source, null, eventType);
    }

    public SceneEvent(@NotNull final EventType<? extends Event> eventType) {
        super(eventType);
    }

    /**
     * Put the new parameter.
     *
     * @param key   the key.
     * @param value the value.
     */
    public void set(@NotNull final Object key, @NotNull final Object value) {

        if (values == null) {
            values = DictionaryFactory.newObjectDictionary();
        }

        values.put(key, value);
    }

    /**
     * Remove a value by the key.
     *
     * @param key the key.
     */
    public void remove(@NotNull final Object key) {
        if (values == null) return;
        values.remove(key);
    }

    /**
     * Get a value by the key.
     *
     * @param <T> the type parameter
     * @param key the key.
     * @return the value or null.
     */
    public <T> @Nullable T get(@NotNull final Object key) {
        if (values == null) return null;
        final Object object = values.get(key);
        if (object == null) return null;
        return unsafeCast(object);
    }
}
