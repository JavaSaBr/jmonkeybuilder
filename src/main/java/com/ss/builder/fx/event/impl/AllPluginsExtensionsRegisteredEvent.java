package com.ss.builder.fx.event.impl;

import com.ss.builder.fx.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that all plugins have registered all their
 * extensions.
 *
 * @author JavaSaBr
 */
public class AllPluginsExtensionsRegisteredEvent extends SceneEvent {

    public static final EventType<AllPluginsExtensionsRegisteredEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, AllPluginsExtensionsRegisteredEvent.class.getSimpleName());
        }
    }

    public AllPluginsExtensionsRegisteredEvent() {
        super(EVENT_TYPE);
    }
}
