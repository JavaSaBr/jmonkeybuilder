package com.ss.builder.fx.event;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.rlib.common.manager.InitializeManager;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
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

    private static final FxEventManager INSTANCE = new FxEventManager();

    @FromAnyThread
    public static @NotNull FxEventManager getInstance() {
        return INSTANCE;
    }

    /**
     * The table of event handlers.
     */
    @NotNull
    private final ObjectDictionary<EventType<? extends Event>,
            Array<EventHandler<? extends Event>>> eventHandlers;

    public FxEventManager() {
        InitializeManager.valid(getClass());
        this.eventHandlers = DictionaryFactory.newObjectDictionary();
    }

    /**
     * Add a new event handler.
     *
     * @param eventType    the event type.
     * @param eventHandler the event handler.
     */
    @FxThread
    public <T extends Event> @NotNull FxEventManager addEventHandler(
            @NotNull EventType<T> eventType,
            @NotNull EventHandler<T> eventHandler
    ) {

        if (!Platform.isFxApplicationThread()) {

            ExecutorManager.getInstance()
                    .addFxTask(() -> addEventHandler(eventType, eventHandler));

            return this;
        }

        getEventHandlers().get(eventType, Array.supplier(EventHandler.class))
                .add(eventHandler);

        return this;
    }

    /**
     * Remove an old event handler.
     *
     * @param eventType    the event type.
     * @param eventHandler the event handler.
     */
    @FxThread
    public @NotNull FxEventManager removeEventHandler(
            @NotNull EventType<?> eventType,
            @NotNull EventHandler<?> eventHandler
    ) {

        getEventHandlers().getOptional(eventType)
                .ifPresent(handlers -> handlers.slowRemove(eventHandler));

        return this;
    }

    /**
     * Get the table of event handlers.
     *
     * @return the table of event handlers.
     */
    @FxThread
    private @NotNull ObjectDictionary<EventType<? extends Event>,
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
        if (Platform.isFxApplicationThread()) {
            notifyImpl(event);
        } else {
            var executorManager = ExecutorManager.getInstance();
            executorManager.addFxTask(() -> notifyImpl(event));
        }
    }

    /**
     * The process of handling a new event.
     */
    @FxThread
    private void notifyImpl(@NotNull Event event) {

        var eventHandlers = getEventHandlers();

        for (EventType<? extends Event> eventType = event.getEventType(); eventType != null;
             eventType = eventType.getSuperType()) {

            var handlers = eventHandlers.get(eventType);
            if (handlers == null || handlers.isEmpty()) {
                continue;
            }

            handlers.forEach(event, (handler, toHandle) ->
                    handler.handle(ClassUtils.unsafeCast(event)));
        }

        if (event instanceof ConsumableEvent && !event.isConsumed()) {
            var executorManager = ExecutorManager.getInstance();
            executorManager.addFxTask(() -> notifyImpl(event));
        }
    }
}
