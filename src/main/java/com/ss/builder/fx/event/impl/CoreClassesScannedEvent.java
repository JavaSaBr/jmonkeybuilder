package com.ss.builder.fx.event.impl;

import com.ss.builder.fx.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that core classes were scanned.
 *
 * @author JavaSaBr
 */
public class CoreClassesScannedEvent extends SceneEvent {

    public static final EventType<CoreClassesScannedEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, CoreClassesScannedEvent.class.getSimpleName());
        }
    }

    public CoreClassesScannedEvent() {
        super(EVENT_TYPE);
    }
}
