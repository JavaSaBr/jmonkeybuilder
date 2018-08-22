package com.ss.builder.fx.component.bar.action;

import com.ss.builder.config.EditorConfig;
import com.ss.builder.fx.event.FxEventManager;
import com.ss.builder.fx.event.impl.ChangedCurrentAssetFolderEvent;
import com.ss.builder.config.EditorConfig;
import com.ss.builder.fx.event.FxEventManager;
import com.ss.builder.fx.event.impl.ChangedCurrentAssetFolderEvent;
import javafx.scene.control.MenuItem;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The action to reopen previous asset folder.
 *
 * @author JavaSaBr
 */
public class ReopenAssetAction extends MenuItem {

    private static final FxEventManager FX_EVENT_MANAGER = FxEventManager.getInstance();

    /**
     * The asset folder.
     */
    @NotNull
    private final Path assetFolder;

    /**
     * Instantiates a new Reopen asset action.
     *
     * @param assetFolder the asset folder
     */
    public ReopenAssetAction(@NotNull final Path assetFolder) {
        super(assetFolder.toString());
        this.assetFolder = assetFolder;
        setOnAction(event -> process());
    }

    /**
     * @return the asset folder.
     */
    @NotNull
    private Path getAssetFolder() {
        return assetFolder;
    }

    /**
     * Open the selected asset folder.
     */
    private void process() {

        final Path assetFolder = getAssetFolder();

        final EditorConfig config = EditorConfig.getInstance();
        config.addOpenedAsset(assetFolder);
        config.setCurrentAsset(assetFolder);
        config.save();

        final ChangedCurrentAssetFolderEvent event = new ChangedCurrentAssetFolderEvent();
        event.setNewAssetFolder(assetFolder);

        FX_EVENT_MANAGER.notify(event);
    }
}
