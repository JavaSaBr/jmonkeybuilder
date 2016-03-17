package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;

import javafx.event.EventType;

/**
 * Событие об изменении статуса фокуса у окна.
 *
 * @author Ronn
 */
public class WindowChangeFocusEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, WindowChangeFocusEvent.class.getSimpleName());

    public static final String FOCUS = "focus";

    public WindowChangeFocusEvent() {
        super(EVENT_TYPE);
    }

    /**
     * @return есть ли фокус на окне.
     */
    public boolean isFocused() {
        return get(FOCUS) == Boolean.TRUE;
    }

    /**
     * @param focused есть ли фокус на окне..
     */
    public void setFocused(final boolean focused) {
        set(FOCUS, focused);
    }
}
