package com.ss.builder.fx.event.impl;

import com.ss.builder.fx.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that the JavaFX context was created.
 *
 * @author JavaSaBr
 */
public class FxContextCreatedEvent extends SceneEvent {

    public static final EventType<FxContextCreatedEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, FxContextCreatedEvent.class.getSimpleName());
        }
    }

    public FxContextCreatedEvent() {
        super(EVENT_TYPE);
    }
}
