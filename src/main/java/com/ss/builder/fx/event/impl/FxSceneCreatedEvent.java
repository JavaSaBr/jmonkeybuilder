package com.ss.builder.fx.event.impl;

import com.ss.builder.fx.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that the JavaFX scene was created.
 *
 * @author JavaSaBr
 */
public class FxSceneCreatedEvent extends SceneEvent {

    public static final EventType<FxSceneCreatedEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, FxSceneCreatedEvent.class.getSimpleName());
        }
    }

    public FxSceneCreatedEvent() {
        super(EVENT_TYPE);
    }
}
