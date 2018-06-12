package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.EventType;

/**
 * The event about that all plugins are already registered all their
 * asset tree context menu fillers.
 *
 * @author JavaSaBr
 */
public class PluginsAssetTreeContextMenuFillersRegisteredEvent extends SceneEvent {

    public static final EventType<PluginsAssetTreeContextMenuFillersRegisteredEvent> EVENT_TYPE;

    static {
        synchronized (EventType.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, PluginsAssetTreeContextMenuFillersRegisteredEvent.class.getSimpleName());
        }
    }

    public PluginsAssetTreeContextMenuFillersRegisteredEvent() {
        super(EVENT_TYPE);
    }
}
