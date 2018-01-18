package com.ss.editor.ui.event;

import static com.ss.rlib.util.array.ArrayFactory.newArray;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;

/**
 * The class to manage javaFX events.
 *
 * @author JavaSaBr
 */
public class FxEventManager {

    @NotNull
    private static final FxEventManager INSTANCE = new FxEventManager();

    @FromAnyThread
    public static @NotNull FxEventManager getInstance() {
        return INSTANCE;
    }

    /**
     * The table of event handlers.
     */
    @NotNull
    private final ObjectDictionary<EventType<? extends Event>, Array<EventHandler<? super Event>>> eventHandlers;

    public FxEventManager() {
        this.eventHandlers = DictionaryFactory.newObjectDictionary();
    }

    /**
     * Add a new event handler.
     *
     * @param eventType    the event type.
     * @param eventHandler the event handler.
     */
    @FxThread
    public void addEventHandler(@NotNull final EventType<? extends Event> eventType,
                                @NotNull final EventHandler<? super Event> eventHandler) {
        getEventHandlers().get(eventType, () -> newArray(EventHandler.class))
                .add(eventHandler);
    }

    /**
     * Remove an old event handler.
     *
     * @param eventType    the event type.
     * @param eventHandler the event handler.
     */
    @FxThread
    public void removeEventHandler(@NotNull final EventType<? extends Event> eventType,
                                   @NotNull final EventHandler<? super Event> eventHandler) {

        final Array<EventHandler<? super Event>> handlers = getEventHandlers().get(eventType);
        if (handlers != null) {
            handlers.slowRemove(eventHandler);
        }
    }

    /**
     * @return the table of event handlers.
     */
    @FxThread
    private @NotNull ObjectDictionary<EventType<? extends Event>, Array<EventHandler<? super Event>>> getEventHandlers() {
        return eventHandlers;
    }

    /**
     * Notify about a new event.
     *
     * @param event the new event.
     */
    @FromAnyThread
    public void notify(@NotNull final Event event) {
        if (Platform.isFxApplicationThread()) {
            notifyImpl(event);
        } else {
            final ExecutorManager executorManager = ExecutorManager.getInstance();
            executorManager.addFxTask(() -> notifyImpl(event));
        }
    }

    /**
     * The process of handling a new event.
     */
    @FxThread
    private void notifyImpl(@NotNull final Event event) {

        final ObjectDictionary<EventType<? extends Event>, Array<EventHandler<? super Event>>> eventHandlers = getEventHandlers();

        for (EventType<? extends Event> eventType = event.getEventType(); eventType != null; eventType = eventType.getSuperType()) {

            final Array<EventHandler<? super Event>> handlers = eventHandlers.get(eventType);
            if (handlers == null || handlers.isEmpty()) {
                continue;
            }

            handlers.forEach(event, EventHandler::handle);
        }

        if (event instanceof ConsumableEvent && !event.isConsumed()) {
            final ExecutorManager executorManager = ExecutorManager.getInstance();
            executorManager.addFxTask(() -> notifyImpl(event));
        }
    }
}
