package com.ss.builder.fx.event;

import com.ss.builder.annotation.FxThread;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import javafx.event.Event;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The base implementation of an event in the javaFX UI.
 *
 * @author JavaSaBr
 */
public class SceneEvent extends Event {

    private static final long serialVersionUID = 6827900349094865635L;

    public static final EventType<SceneEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.class.getSimpleName());
        }
    }

    /**
     * The parameters.
     */
    @Nullable
    private ObjectDictionary<Object, Object> values;

    public SceneEvent(@Nullable Object source, @NotNull EventType<? extends Event> eventType) {
        super(source, null, eventType);
    }

    public SceneEvent(@NotNull EventType<? extends Event> eventType) {
        super(eventType);
    }

    /**
     * Get the map of all values of this event.
     *
     * @return the map of all values of this event.
     */
    @FxThread
    private @NotNull Optional<ObjectDictionary<Object, Object>> getValues() {
        return Optional.ofNullable(values);
    }

    /**
     * Put the new parameter.
     *
     * @param key   the key.
     * @param value the value.
     */
    public void set(@NotNull Object key, @NotNull Object value) {

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
    public void remove(@NotNull Object key) {
        getValues().ifPresent(objects -> objects.remove(key));
    }

    /**
     * Get a value by the key.
     *
     * @param <T> the result's type.
     * @param key the key.
     * @return the value or null.
     */
    public <T> @Nullable T get(@NotNull Object key) {
        return getValues()
                .map(objects -> objects.get(key))
                .map(ClassUtils::<T>unsafeCast)
                .orElse(null);
    }

    @Override
    public String toString() {
        return eventType.toString();
    }
}
