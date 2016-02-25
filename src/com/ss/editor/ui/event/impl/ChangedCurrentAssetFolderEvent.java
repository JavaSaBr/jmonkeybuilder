package com.ss.editor.ui.event.impl;

import com.ss.editor.ui.event.SceneEvent;

import java.nio.file.Path;

import javafx.event.EventType;

/**
 * Событие об изменении текущей папки Asset.
 *
 * @author Ronn
 */
public class ChangedCurrentAssetFolderEvent extends SceneEvent {

    public static final EventType<SceneEvent> EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, ChangedCurrentAssetFolderEvent.class.getSimpleName());

    public static final String ASSET = "asset";

    public ChangedCurrentAssetFolderEvent() {
        super(EVENT_TYPE);
    }

    public void setNewAssetFolder(final Path newAssetFolder) {
        set(ASSET, newAssetFolder);
    }

    public Path getNewAssetFolder() {
        return get(ASSET);
    }
}
