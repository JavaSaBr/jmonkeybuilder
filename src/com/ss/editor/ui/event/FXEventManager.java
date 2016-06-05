package com.ss.editor.ui.event;

import com.jme3.system.lwjgl.LwjglWindow;
import com.ss.editor.Editor;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.event.impl.WindowChangeFocusEvent;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWWindowFocusCallback;
import org.lwjgl.opengl.GL11;

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

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final FXEventManager INSTANCE = new FXEventManager();

    public static FXEventManager getInstance() {
        return INSTANCE;
    }

    /**
     * Таблица обработчиков событий.
     */
    private final ObjectDictionary<EventType<? extends Event>, Array<EventHandler<? super Event>>> eventHandlers;

    /**
     * Слушатель изменения фокуса окна.
     */
    private GLFWWindowFocusCallback windowFocusCallback;

    public FXEventManager() {
        this.eventHandlers = DictionaryFactory.newObjectDictionary();
        EXECUTOR_MANAGER.addEditorThreadTask(this::initListener);
    }

    private void initListener() {

        final Editor editor = Editor.getInstance();
        final LwjglWindow context = (LwjglWindow) editor.getContext();

        windowFocusCallback = new GLFWWindowFocusCallback() {

            @Override
            public void invoke(final long window, final int focused) {

                final WindowChangeFocusEvent event = new WindowChangeFocusEvent();
                event.setFocused(focused == GL11.GL_TRUE);

                FXEventManager.this.notify(event);
            }
        };

        GLFW.glfwSetWindowFocusCallback(context.getWindowHandle(), windowFocusCallback);
    }

    /**
     * Добавление обработчика событий javaFX UI.
     *
     * @param eventType    тип событий.
     * @param eventHandler обработчик событий.
     */
    public void addEventHandler(final EventType<? extends Event> eventType, final EventHandler<? super Event> eventHandler) {

        final ObjectDictionary<EventType<? extends Event>, Array<EventHandler<? super Event>>> eventHandlers = getEventHandlers();

        final Array<EventHandler<? super Event>> handlers = eventHandlers.get(eventType, () -> ArrayFactory.newArray(EventHandler.class));
        handlers.add(eventHandler);
    }

    /**
     * Удаление обработчика событий javaFX UI.
     *
     * @param eventType    тип событий.
     * @param eventHandler обработчик событий.
     */
    public void removeEventHandler(final EventType<? extends Event> eventType, final EventHandler<? super Event> eventHandler) {

        final ObjectDictionary<EventType<? extends Event>, Array<EventHandler<? super Event>>> eventHandlers = getEventHandlers();

        Array<EventHandler<? super Event>> handlers = eventHandlers.get(eventType);
        if (handlers == null) return;

        handlers.slowRemove(eventHandler);
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
            if (handlers == null || handlers.isEmpty()) continue;

            handlers.forEach(event, (toHandle, handler) -> handler.handle(toHandle));
        }

        if (event instanceof ConsumeableEvent && !event.isConsumed()) {
            final ExecutorManager executorManager = ExecutorManager.getInstance();
            executorManager.addFXTask(() -> notifyImpl(event));
        }
    }
}
