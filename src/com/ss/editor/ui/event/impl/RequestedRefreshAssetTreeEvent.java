package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;

import java.nio.file.Path;

import javafx.event.EventType;

/**
 * Событие об изменении текущей папки Asset.
 *
 * @author Ronn
 */
public class RequestedRefreshAssetTreeEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, RequestedRefreshAssetTreeEvent.class.getSimpleName());

    public static final String ASSET = "asset";

    public RequestedRefreshAssetTreeEvent() {
        super(EVENT_TYPE);
    }

    /**
     * @param newAssetFolder новая папка Asset.
     */
    public void setNewAssetFolder(final Path newAssetFolder) {
        set(ASSET, newAssetFolder);
    }

    /**
     * @return новая папка Asset.
     */
    public Path getNewAssetFolder() {
        return get(ASSET);
    }
}
