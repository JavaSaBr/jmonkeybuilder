package com.ss.builder.fx.event.impl;

import com.ss.builder.manager.ResourceManager;
import com.ss.builder.fx.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that all plugins have already registered all
 * interested resources in {@link ResourceManager}.
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
