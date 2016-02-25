package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;

import javafx.event.EventType;

/**
 * Событие запроса об обновлении дерева Asset.
 *
 * @author Ronn
 */
public class RequestedRefreshAssetTreeEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestedRefreshAssetTreeEvent.class.getSimpleName());

    public RequestedRefreshAssetTreeEvent() {
        super(EVENT_TYPE);
    }
}
