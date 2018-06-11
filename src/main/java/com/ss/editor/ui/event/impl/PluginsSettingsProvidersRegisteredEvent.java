package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that all plugins are already registered all their
 * settings providers.
 *
 * @author JavaSaBr
 */
public class PluginsSettingsProvidersRegisteredEvent extends SceneEvent {

    public static final EventType<PluginsSettingsProvidersRegisteredEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, PluginsSettingsProvidersRegisteredEvent.class.getSimpleName());
        }
    }

    public PluginsSettingsProvidersRegisteredEvent() {
        super(EVENT_TYPE);
    }
}
