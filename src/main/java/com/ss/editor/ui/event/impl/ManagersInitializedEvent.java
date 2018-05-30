package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that all managers were initialized.
 *
 * @author JavaSaBr
 */
public class ManagersInitializedEvent extends SceneEvent {

    public static final EventType<ManagersInitializedEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, ManagersInitializedEvent.class.getSimpleName());
        }
    }

    public ManagersInitializedEvent() {
        super(EVENT_TYPE);
    }
}
