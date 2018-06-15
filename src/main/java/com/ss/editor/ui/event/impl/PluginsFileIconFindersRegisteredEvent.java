package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that all plugins have already registered all their
 * file icon finders.
 *
 * @author JavaSaBr
 */
public class PluginsFileIconFindersRegisteredEvent extends SceneEvent {

    public static final EventType<PluginsFileIconFindersRegisteredEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, PluginsFileIconFindersRegisteredEvent.class.getSimpleName());
        }
    }

    public PluginsFileIconFindersRegisteredEvent() {
        super(EVENT_TYPE);
    }
}
