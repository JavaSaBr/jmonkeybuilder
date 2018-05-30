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

    public static final EventType<RequestedRefreshAssetEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestedRefreshAssetEvent.class.getSimpleName());
        }
    }

    public RequestedRefreshAssetEvent() {
        super(EVENT_TYPE);
    }
}
