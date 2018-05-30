package com.ss.editor.manager;

import static com.ss.rlib.common.util.array.ArrayFactory.newArray;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.event.ConsumableEvent;
import com.ss.rlib.common.manager.InitializeManager;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.dictionary.ConcurrentObjectDictionary;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;

/**
 * The class to manage async events.
 *
 * @author JavaSaBr
 */
public class AsyncEventManager {

    private static final AsyncEventManager INSTANCE = new AsyncEventManager();

    @FromAnyThread
    public static @NotNull AsyncEventManager getInstance() {
        return INSTANCE;
    }

    /**
     * The table of event handlers.
     */
    @NotNull
    private final ConcurrentObjectDictionary<EventType<? extends Event>,
            Array<EventHandler<? extends Event>>> eventHandlers;

    public AsyncEventManager() {
        InitializeManager.valid(getClass());
        this.eventHandlers = DictionaryFactory.newConcurrentAtomicObjectDictionary();
    }

    /**
     * Add a new event handler.
     *
     * @param eventType    the event type.
     * @param eventHandler the event handler.
     */
    @FromAnyThread
    public <T extends Event> void addEventHandler(
            @NotNull EventType<T> eventType,
            @NotNull EventHandler<T> eventHandler
    ) {
        var eventHandlers = getEventHandlers();
        var stamp = eventHandlers.writeLock();
        try {

            eventHandlers.get(eventType, () -> newArray(EventHandler.class))
                    .add(eventHandler);

        } finally {
            eventHandlers.writeUnlock(stamp);
        }
    }

    /**
     * Remove an old event handler.
     *
     * @param eventType    the event type.
     * @param eventHandler the event handler.
     */
    @FromAnyThread
    public void removeEventHandler(
            @NotNull EventType<?> eventType,
            @NotNull EventHandler<?> eventHandler
    ) {
        var eventHandlers = getEventHandlers();
        var stamp = eventHandlers.writeLock();
        try {

            eventHandlers.getOptional(eventType)
                    .ifPresent(handlers -> handlers.slowRemove(eventHandler));

        } finally {
            eventHandlers.writeUnlock(stamp);
        }
    }

    /**
     * Get the table of event handlers.
     *
     * @return the table of event handlers.
     */
    @FromAnyThread
    private @NotNull ConcurrentObjectDictionary<EventType<? extends Event>,
            Array<EventHandler<? extends Event>>> getEventHandlers() {
        return eventHandlers;
    }

    /**
     * Notify about a new event.
     *
     * @param event the new event.
     */
    @FromAnyThread
    public void notify(@NotNull Event event) {
        ExecutorManager.getInstance()
                .addBackgroundTask(() -> notifyImpl(event));
    }

    /**
     * The process of handling a new event.
     */
    @BackgroundThread
    private void notifyImpl(@NotNull Event event) {

        var eventHandlers = getEventHandlers();
        var executorManager = ExecutorManager.getInstance();

        for (EventType<? extends Event> eventType = event.getEventType(); eventType != null; eventType = eventType.getSuperType()) {

            var handlers = eventHandlers.get(eventType);
            if (handlers == null || handlers.isEmpty()) {
                continue;
            }

            handlers.forEach(handler ->
                    executorManager.addBackgroundTask(() ->
                            handler.handle(ClassUtils.unsafeCast(event))));
        }

        if (event instanceof ConsumableEvent && !event.isConsumed()) {
            executorManager.addBackgroundTask(() -> notifyImpl(event));
        }
    }
}
