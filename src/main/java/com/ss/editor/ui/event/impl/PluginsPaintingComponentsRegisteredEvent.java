package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that all plugins are already registered all their
 * painting components.
 *
 * @author JavaSaBr
 */
public class PluginsPaintingComponentsRegisteredEvent extends SceneEvent {

    public static final EventType<PluginsPaintingComponentsRegisteredEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, PluginsPaintingComponentsRegisteredEvent.class.getSimpleName());
        }
    }

    public PluginsPaintingComponentsRegisteredEvent() {
        super(EVENT_TYPE);
    }
}
