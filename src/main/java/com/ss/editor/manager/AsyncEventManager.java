package com.ss.editor.manager;

import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.event.ConsumableEvent;
import com.ss.rlib.common.manager.InitializeManager;
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
                throw new IllegalStateException("The list of listened events should not be null.");
            }

            var resultHandler = new CombinedEventHandler(eventTypes, handler);
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
        private final Runnable handler;

        public CombinedEventHandler(@NotNull Array<EventType<Event>> eventTypes, @NotNull Runnable handler) {
            this.eventTypes = ArrayFactory.newConcurrentStampedLockArray(EventType.class);
            this.eventTypes.addAll(eventTypes);
            this.eventTypesSet = ArrayFactory.newArraySet(EventType.class);
            this.eventTypesSet.addAll(eventTypes);
            this.handler = handler;
        }

        @Override
        public void handle(@NotNull Event event) {

            ArrayUtils.runInWriteLock(eventTypes, event.getEventType(), Array::remove);

            if (!eventTypes.isEmpty()) {
                return;
            }

            handler.run();

            var eventManager = getInstance();

            eventTypesSet.forEach(eventType ->
                    eventManager.removeEventHandler(eventType, this));
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
        var handlers = eventHandlers.getInReadLock(eventType, ObjectDictionary::get);

        if (handlers != null) {
            handlers.runInWriteLock(eventHandler, Collection::add);
            return;
        }

        eventHandlers.runInWriteLock(dictionary -> {

            var newHandlers = dictionary.get(eventType,
                    () -> ArrayFactory.newConcurrentStampedLockArray(EventHandler.class));

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

        var eventHandlers = getEventHandlers();
        var handlers = eventHandlers.getInReadLock(eventType, ObjectDictionary::get);

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

        var eventHandlers = getEventHandlers();

        for (EventType<? extends Event> eventType = event.getEventType(); eventType != null;
             eventType = eventType.getSuperType()) {

            var handlers = eventHandlers.getInReadLock(eventType, ObjectDictionary::get);
            if (handlers == null || handlers.isEmpty()) {
                continue;
            }

            handlers.runInReadLock(event, (array, toHandle) -> {

                var executorManager = ExecutorManager.getInstance();

                for (var eventHandler : array) {
                    executorManager.addBackgroundTask(() ->
                            eventHandler.handle(ClassUtils.unsafeCast(event)));
                }
            });
        }

        if (event instanceof ConsumableEvent && !event.isConsumed()) {
            var executorManager = ExecutorManager.getInstance();
            executorManager.addBackgroundTask(() -> notifyImpl(event));
        }
    }
}
