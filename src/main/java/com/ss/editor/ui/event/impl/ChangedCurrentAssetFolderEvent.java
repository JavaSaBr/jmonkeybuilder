package com.ss.editor.ui.event.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.ui.event.SceneEvent;
import javafx.event.Event;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The event about changed an asset folder.
 *
 * @author JavaSaBr
 */
public class ChangedCurrentAssetFolderEvent extends SceneEvent {

    /**
     * The constant EVENT_TYPE.
     */
    @NotNull
    public static final EventType<SceneEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, ChangedCurrentAssetFolderEvent.class.getSimpleName());
        }
    }

    private static final String ASSET = "asset";

    /**
     * Instantiates a new Changed current asset folder event.
     */
    public ChangedCurrentAssetFolderEvent() {
        super(EVENT_TYPE);
    }

    /**
     * Gets new asset folder.
     *
     * @return the new asset.
     */
    @NotNull
    public Path getNewAssetFolder() {
        return notNull(get(ASSET));
    }

    /**
     * Sets new asset folder.
     *
     * @param newAssetFolder the new asset.
     */
    public void setNewAssetFolder(@NotNull final Path newAssetFolder) {
        set(ASSET, newAssetFolder);
    }
}
