package com.ss.builder.fx.event.impl;

import com.ss.builder.fx.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that Editor has finished loading.
 *
 * @author JavaSaBr
 */
public class EditorFinishedLoadingEvent extends SceneEvent {

    public static final EventType<EditorFinishedLoadingEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, EditorFinishedLoadingEvent.class.getSimpleName());
        }
    }

    public EditorFinishedLoadingEvent() {
        super(EVENT_TYPE);
    }
}
