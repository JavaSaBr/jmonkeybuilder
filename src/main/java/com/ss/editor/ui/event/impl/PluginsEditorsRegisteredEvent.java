package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that all plugins are already registered all their
 * editors.
 *
 * @author JavaSaBr
 */
public class PluginsEditorsRegisteredEvent extends SceneEvent {

    public static final EventType<PluginsEditorsRegisteredEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, PluginsEditorsRegisteredEvent.class.getSimpleName());
        }
    }

    public PluginsEditorsRegisteredEvent() {
        super(EVENT_TYPE);
    }
}
