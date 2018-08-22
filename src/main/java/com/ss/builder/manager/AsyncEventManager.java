package com.ss.builder.manager;

import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.fx.event.ConsumableEvent;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.ArrayUtils;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.array.ConcurrentArray;
import com.ss.rlib.common.util.dictionary.ConcurrentObjectDictionary;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The class to manage async events.
 *
 * @author JavaSaBr
 */
public class AsyncEventManager {

    private static final Logger LOGGER = LoggerManager.getLogger(AsyncEventManager.class);

    /**
     * Builder to build a combined async events handler.
     *
     * @author JavaSaBr
     */
    public static class SingleAsyncEventHandlerBuilder {

        /**
         * Create a builder for the event type.
         *
         * @param eventType the event type.
         * @return the new builder.
         */
        @FromAnyThread
        public static @NotNull SingleAsyncEventHandlerBuilder of(@NotNull EventType<? extends Event> eventType) {
            return new SingleAsyncEventHandlerBuilder(eventType);
        }

        /**
         * The list of listened event types.
         */
        @NotNull
        private final EventType<Event> eventType;

        /**
         * The result handler.
         */
        @NotNull
        private final Array<Runnable> handlers;

        public SingleAsyncEventHandlerBuilder(@NotNull EventType<? extends Event> eventType) {
            this.handlers = ArrayFactory.newArray(Runnable.class);
            this.eventType = ClassUtils.unsafeCast(eventType);
        }

        /**
         * Add the handler.
         *
         * @param handler the handler.
         * @return this builder.
         */
        @FromAnyThread
        public @NotNull SingleAsyncEventHandlerBuilder add(@NotNull Runnable handler) {
            handlers.add(handler);
            return this;
        }

        @FromAnyThread
        public void buildAndRegister() {

            if (handlers.isEmpty()) {
                throw new IllegalStateException("The list of handlers should not be empty.");
            }

            var resultHandler = new CombinedEventHandler(Array.of(eventType), handlers);
            var eventManager = getInstance();

            var eventTypesSet = resultHandler.eventTypesSet;
            eventTypesSet.forEach(eventType ->
                eventManager.addEventHandler(eventType, resultHandler));
        }
    }

    /**
     * Builder to build a combined async events handler.
     *
     * @author JavaSaBr
     */
    public static class CombinedAsyncEventHandlerBuilder {

        /**
         * Create a builder for the handler.
         *
         * @param handler the result handler.
         * @return the new builder.
         */
        @FromAnyThread
        public static @NotNull CombinedAsyncEventHandlerBuilder of(@NotNull Runnable handler) {
            return new CombinedAsyncEventHandlerBuilder(handler);
        }

        /**
         * The list of listened event types.
         */
        @NotNull
        private final Array<EventType<Event>> eventTypes;

        /**
         * The result handler.
         */
        @NotNull
        private final Runnable handler;

        public CombinedAsyncEventHandlerBuilder(@NotNull Runnable handler) {
            this.handler = handler;
            this.eventTypes = ArrayFactory.newArray(EventType.class);
        }

        /**
         * Add listening the additional event type.
         *
         * @param eventType the additional event type.
         * @return this builder.
         */
        @FromAnyThread
        public @NotNull CombinedAsyncEventHandlerBuilder add(@NotNull EventType<? extends Event> eventType) {
            return add(eventType, 1);
        }

        /**
         * Add listening the additional event type.
         *
         * @param eventType the additional event type.
         * @param count     the count of expected events of the type.
         * @return this builder.
         */
        @FromAnyThread
        public @NotNull CombinedAsyncEventHandlerBuilder add(@NotNull EventType<? extends Event> eventType, int count) {

            for (int i = 0; i < count; i++) {
                eventTypes.add(ClassUtils.unsafeCast(eventType));
            }

            return this;
        }

        @FromAnyThread
        public void buildAndRegister() {

            if (eventTypes.isEmpty()) {
                throw new IllegalStateException("The list of listened events should not be empty.");
            }

            var resultHandler = new CombinedEventHandler(eventTypes, Array.of(handler));
            var eventManager = getInstance();

            var eventTypesSet = resultHandler.eventTypesSet;
            eventTypesSet.forEach(eventType ->
                    eventManager.addEventHandler(eventType, resultHandler));
        }
    }

    private static class CombinedEventHandler implements EventHandler<Event> {

        @NotNull
        private final ConcurrentArray<EventType<Event>> eventTypes;

        @NotNull
        private final Array<EventType<Event>> eventTypesSet;

        @NotNull
        private final Array<Runnable> handlers;

        public CombinedEventHandler(@NotNull Array<EventType<Event>> eventTypes, @NotNull Array<Runnable> handlers) {
            this.eventTypes = ArrayFactory.newConcurrentStampedLockArray(EventType.class);
            this.eventTypes.addAll(eventTypes);
            this.eventTypesSet = ArrayFactory.newArraySet(EventType.class);
            this.eventTypesSet.addAll(eventTypes);
            this.handlers = handlers;
        }

        @Override
        public void handle(@NotNull Event event) {

            ArrayUtils.runInWriteLock(eventTypes, event.getEventType(), Array::remove);

            if (!eventTypes.isEmpty()) {
                return;
            }

            handlers.forEach(Runnable::run);

            var eventManager = getInstance();

            eventTypesSet.forEach(eventType ->
                    eventManager.removeEventHandler(eventType, this));
        }

        @Override
        public String toString() {

            if (handlers.size() == 1) {
                return handlers.first().getClass().getSimpleName();
            }

            return handlers.toString();
        }
    }

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
            ConcurrentArray<EventHandler<? extends Event>>> eventHandlers;

    public AsyncEventManager() {
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

        LOGGER.debug(eventType, eventHandler,
                (type, handler) -> "Register the handler " + handler + " for the event type " + type);

        var allHandlers = getEventHandlers();
        var handlers = allHandlers.getInReadLock(eventType, ObjectDictionary::get);

        if (handlers != null) {
            handlers.runInWriteLock(eventHandler, Collection::add);
            return;
        }

        allHandlers.runInWriteLock(dictionary -> {
            var newHandlers = dictionary.getOrCompute(eventType, ConcurrentArray.supplier(EventHandler.class));
            newHandlers.runInWriteLock(eventHandler, Collection::add);
        });
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

        LOGGER.debug(eventType, eventHandler,
                (type, handler) -> "Remove the handler " + handler + " for the event type " + type);

        var handlers = getEventHandlers()
                .getInReadLock(eventType, ObjectDictionary::get);

        if (handlers != null) {
            handlers.runInWriteLock(eventHandler, Collection::remove);
        }
    }

    /**
     * Get the table of event handlers.
     *
     * @return the table of event handlers.
     */
    @FromAnyThread
    private @NotNull ConcurrentObjectDictionary<EventType<? extends Event>,
            ConcurrentArray<EventHandler<? extends Event>>> getEventHandlers() {
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

        LOGGER.debug(event, e -> "Received the event " + e);

        var eventHandlers = getEventHandlers();

        for (EventType<? extends Event> eventType = event.getEventType();
                 eventType != null;
                 eventType = eventType.getSuperType()) {

            var handlers = eventHandlers.getInReadLock(eventType, ObjectDictionary::get);


            if (handlers == null || handlers.isEmpty()) {
                continue;
            }

            handlers.runInReadLock(event, (array, toHandle) -> {
                for (var eventHandler : array) {
                    ExecutorManager.getInstance()
                            .addBackgroundTask(() -> notifyImpl(event, eventHandler));
                }
            });
        }

        if (event instanceof ConsumableEvent && !event.isConsumed()) {
            ExecutorManager.getInstance()
                    .addBackgroundTask(() -> notifyImpl(event));
        }
    }

    @BackgroundThread
    private void notifyImpl(@NotNull Event event, @NotNull EventHandler<? extends Event> eventHandler) {

        LOGGER.debug(event, eventHandler,
                (e, handler) -> e + " -> " + handler);

        eventHandler.handle(ClassUtils.unsafeCast(event));
    }
}
