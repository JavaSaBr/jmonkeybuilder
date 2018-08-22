package com.ss.builder.fx.event.impl;

import com.ss.builder.fx.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that the JavaFX scene was attached to the main window.
 *
 * @author JavaSaBr
 */
public class FxSceneAttachedEvent extends SceneEvent {

    public static final EventType<FxSceneAttachedEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, FxSceneAttachedEvent.class.getSimpleName());
        }
    }

    public FxSceneAttachedEvent() {
        super(EVENT_TYPE);
    }
}
