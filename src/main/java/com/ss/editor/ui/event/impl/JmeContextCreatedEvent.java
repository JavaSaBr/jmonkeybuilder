package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that the jME context was created.
 *
 * @author JavaSaBr
 */
public class JmeContextCreatedEvent extends SceneEvent {

    public static final EventType<JmeContextCreatedEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, JmeContextCreatedEvent.class.getSimpleName());
        }
    }

    public JmeContextCreatedEvent() {
        super(EVENT_TYPE);
    }
}
