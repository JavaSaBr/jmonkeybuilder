package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that all plugins are already registered all
 * interested resources in {@link com.ss.editor.manager.ResourceManager}.
 *
 * @author JavaSaBr
 */
public class PluginsRegisteredResourcesEvent extends SceneEvent {

    public static final EventType<PluginsRegisteredResourcesEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, PluginsRegisteredResourcesEvent.class.getSimpleName());
        }
    }

    public PluginsRegisteredResourcesEvent() {
        super(EVENT_TYPE);
    }
}
