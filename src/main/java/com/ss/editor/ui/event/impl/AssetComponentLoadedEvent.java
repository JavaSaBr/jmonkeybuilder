package com.ss.editor.ui.event.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.ui.event.SceneEvent;
import javafx.event.Event;
import javafx.event.EventType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The event about that AssetComponent loaded an asset folder.
 *
 * @author JavaSaBr
 */
public class AssetComponentLoadedEvent extends SceneEvent {

    /**
     * The constant EVENT_TYPE.
     */
    @NotNull
    public static final EventType<SceneEvent> EVENT_TYPE;

    static {
        synchronized (Event.class) {
            EVENT_TYPE = new EventType<>(SceneEvent.EVENT_TYPE, AssetComponentLoadedEvent.class.getSimpleName());
        }
    }

    private static final String ASSET = "asset";

    public AssetComponentLoadedEvent(@NotNull final Path assetFolder) {
        super(EVENT_TYPE);
        setAssetFolder(assetFolder);
    }

    /**
     * Get the asset folder.
     *
     * @return the asset folder.
     */
    public @NotNull Path getAssetFolder() {
        return notNull(get(ASSET));
    }

    /**
     * Set the asset folder.
     *
     * @param assetFolder the asset folder.
     */
    public void setAssetFolder(@NotNull final Path assetFolder) {
        set(ASSET, assetFolder);
    }
}
