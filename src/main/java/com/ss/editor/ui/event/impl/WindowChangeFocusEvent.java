package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about changed focus of a window.
 *
 * @author JavaSaBr
 */
public class WindowChangeFocusEvent extends SceneEvent {

    /**
     * The constant EVENT_TYPE.
     */
    public static final EventType<SceneEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, WindowChangeFocusEvent.class.getSimpleName());
        }
    }

    private static final String FOCUS = "focus";

    /**
     * Instantiates a new Window change focus event.
     */
    public WindowChangeFocusEvent() {
        super(EVENT_TYPE);
    }

    /**
     * Is focused boolean.
     *
     * @return true if a window has focus.
     */
    public boolean isFocused() {
        return get(FOCUS) == Boolean.TRUE;
    }

    /**
     * Sets focused.
     *
     * @param focused true if a window has focus.
     */
    public void setFocused(final boolean focused) {
        set(FOCUS, focused);
    }
}
