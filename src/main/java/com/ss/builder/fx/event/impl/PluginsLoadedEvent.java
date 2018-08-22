package com.ss.builder.fx.event.impl;

import com.ss.builder.fx.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that all plugins were loaded.
 *
 * @author JavaSaBr
 */
public class PluginsLoadedEvent extends SceneEvent {

    public static final EventType<PluginsLoadedEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, PluginsLoadedEvent.class.getSimpleName());
        }
    }

    public PluginsLoadedEvent() {
        super(EVENT_TYPE);
    }
}
