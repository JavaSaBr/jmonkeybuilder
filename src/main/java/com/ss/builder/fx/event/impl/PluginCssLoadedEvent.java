package com.ss.builder.fx.event.impl;

import com.ss.builder.fx.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that css registry has loaded plugin's css files.
 *
 * @author JavaSaBr
 */
public class PluginCssLoadedEvent extends SceneEvent {

    public static final EventType<PluginCssLoadedEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, PluginCssLoadedEvent.class.getSimpleName());
        }
    }

    public PluginCssLoadedEvent() {
        super(EVENT_TYPE);
    }
}
