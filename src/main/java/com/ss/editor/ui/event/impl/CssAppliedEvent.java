package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that css registry applied the current set of CSS files.
 *
 * @author JavaSaBr
 */
public class CssAppliedEvent extends SceneEvent {

    public static final EventType<CssAppliedEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, CssAppliedEvent.class.getSimpleName());
        }
    }

    public CssAppliedEvent() {
        super(EVENT_TYPE);
    }
}
