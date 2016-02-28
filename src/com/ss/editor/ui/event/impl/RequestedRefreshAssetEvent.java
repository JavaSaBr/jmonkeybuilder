package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;

import javafx.event.EventType;

/**
 * Событие запроса об обновлении Asset.
 *
 * @author Ronn
 */
public class RequestedRefreshAssetEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestedRefreshAssetEvent.class.getSimpleName());

    public RequestedRefreshAssetEvent() {
        super(EVENT_TYPE);
    }
}
