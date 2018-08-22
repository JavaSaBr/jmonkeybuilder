package com.ss.builder.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about changed focus of a window.
 *
 * @author JavaSaBr
 */
public class ClasspathReloadedEvent extends SceneEvent {

    public static final EventType<ClasspathReloadedEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, ClasspathReloadedEvent.class.getSimpleName());
        }
    }

    public ClasspathReloadedEvent() {
        super(EVENT_TYPE);
    }
}
