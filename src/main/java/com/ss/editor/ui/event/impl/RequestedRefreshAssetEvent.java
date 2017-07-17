package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * The event about request to refresh the current asset folder.
 *
 * @author JavaSaBr
 */
public class RequestedRefreshAssetEvent extends SceneEvent {

    /**
     * The constant EVENT_TYPE.
     */
    public static final EventType<SceneEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestedRefreshAssetEvent.class.getSimpleName());
        }
    }

    /**
     * Instantiates a new Requested refresh asset event.
     */
    public RequestedRefreshAssetEvent() {
        super(EVENT_TYPE);
    }
}
