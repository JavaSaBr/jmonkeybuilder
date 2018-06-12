package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that all plugins are already registered all their
 * file converters.
 *
 * @author JavaSaBr
 */
public class PluginsFileConvertersRegisteredEvent extends SceneEvent {

    public static final EventType<PluginsFileConvertersRegisteredEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, PluginsFileConvertersRegisteredEvent.class.getSimpleName());
        }
    }

    public PluginsFileConvertersRegisteredEvent() {
        super(EVENT_TYPE);
    }
}
