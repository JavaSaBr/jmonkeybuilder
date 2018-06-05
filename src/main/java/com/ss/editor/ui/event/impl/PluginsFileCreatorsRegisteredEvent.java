package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that all plugins are already registered all
 * their file creators.
 *
 * @author JavaSaBr
 */
public class PluginsFileCreatorsRegisteredEvent extends SceneEvent {

    public static final EventType<PluginsFileCreatorsRegisteredEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, PluginsFileCreatorsRegisteredEvent.class.getSimpleName());
        }
    }

    public PluginsFileCreatorsRegisteredEvent() {
        super(EVENT_TYPE);
    }
}
