package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that all plugins have already registered all their
 * file preview factories.
 *
 * @author JavaSaBr
 */
public class PluginsFilePreviewFactoriesRegisteredEvent extends SceneEvent {

    public static final EventType<PluginsFilePreviewFactoriesRegisteredEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, PluginsFilePreviewFactoriesRegisteredEvent.class.getSimpleName());
        }
    }

    public PluginsFilePreviewFactoriesRegisteredEvent() {
        super(EVENT_TYPE);
    }
}
