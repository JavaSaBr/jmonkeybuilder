package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that all plugins have already registered all their
 * property builders.
 *
 * @author JavaSaBr
 */
public class PluginsPropertyBuildersRegisteredEvent extends SceneEvent {

    public static final EventType<PluginsPropertyBuildersRegisteredEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, PluginsPropertyBuildersRegisteredEvent.class.getSimpleName());
        }
    }

    public PluginsPropertyBuildersRegisteredEvent() {
        super(EVENT_TYPE);
    }
}
