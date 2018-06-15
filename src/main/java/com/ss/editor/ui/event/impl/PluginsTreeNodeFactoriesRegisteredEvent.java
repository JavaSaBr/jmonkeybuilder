package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that all plugins have already registered all their
 * tree node factories.
 *
 * @author JavaSaBr
 */
public class PluginsTreeNodeFactoriesRegisteredEvent extends SceneEvent {

    public static final EventType<PluginsTreeNodeFactoriesRegisteredEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, PluginsTreeNodeFactoriesRegisteredEvent.class.getSimpleName());
        }
    }

    public PluginsTreeNodeFactoriesRegisteredEvent() {
        super(EVENT_TYPE);
    }
}
