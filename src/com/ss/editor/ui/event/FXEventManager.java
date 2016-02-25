package com.ss.editor.ui.event;

import com.ss.editor.manager.ExecutorManager;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

/**
 * Менеджер слушателей событий UI JavaFX.
 *
 * @author Ronn
 */
public class FXEventManager {

    private static final FXEventManager INSTANCE = new FXEventManager();

    public static FXEventManager getInstance() {
        return INSTANCE;
    }

    /**
     * Таблица обработчиков событий.
     */
    private final ObjectDictionary<EventType<? extends Event>, Array<EventHandler<? super Event>>> eventHandlers;

    public FXEventManager() {
        this.eventHandlers = DictionaryFactory.newObjectDictionary();
    }

    /**
     * Добавление обработчика событий javaFX UI.
     *
     * @param eventType    тип событий.
     * @param eventHandler обработчик событий.
     */
    public void addEventHandler(final EventType<? extends Event> eventType, final EventHandler<? super Event> eventHandler) {

        final ObjectDictionary<EventType<? extends Event>, Array<EventHandler<? super Event>>> eventHandlers = getEventHandlers();

        Array<EventHandler<? super Event>> handlers = eventHandlers.get(eventType);

        if (handlers == null) {
            handlers = ArrayFactory.newArray(EventHandler.class);
            eventHandlers.put(eventType, handlers);
        }

        handlers.add(eventHandler);
    }

    /**
     * @return словарь обработчиков событий.
     */
    protected ObjectDictionary<EventType<? extends Event>, Array<EventHandler<? super Event>>> getEventHandlers() {
        return eventHandlers;
    }

    /**
     * Уведомление о событии javaFX UI.
     *
     * @param event событие.
     */
    public void notify(final Event event) {
        if (Platform.isFxApplicationThread()) {
            notifyImpl(event);
        } else {
            final ExecutorManager executorManager = ExecutorManager.getInstance();
            executorManager.addFXTask(() -> notifyImpl(event));
        }
    }

    /**
     * Реализация отправки события компонентам.
     */
    protected void notifyImpl(final Event event) {

        final ObjectDictionary<EventType<? extends Event>, Array<EventHandler<? super Event>>> eventHandlers = getEventHandlers();

        for (EventType<? extends Event> eventType = event.getEventType(); eventType != null; eventType = (EventType<? extends Event>) eventType.getSuperType()) {

            final Array<EventHandler<? super Event>> handlers = eventHandlers.get(eventType);

            if (handlers == null || handlers.isEmpty()) {
                continue;
            }

            for (final EventHandler<? super Event> handler : handlers.array()) {

                if (handler == null) {
                    break;
                }

                handler.handle(event);
            }
        }

        if (event instanceof ConsumeableEvent && !event.isConsumed()) {
            final ExecutorManager executorManager = ExecutorManager.getInstance();
            executorManager.addFXTask(() -> notifyImpl(event));
        }
    }
}
