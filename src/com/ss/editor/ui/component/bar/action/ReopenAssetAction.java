package com.ss.editor.ui.component.bar.action;

import com.ss.editor.Editor;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.ChangedCurrentAssetFolderEvent;

import java.nio.file.Path;

import javafx.scene.control.MenuItem;

/**
 * Реализация действия по открытию папки с Asset.
 *
 * @author Ronn
 */
public class ReopenAssetAction extends MenuItem {

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    private static final Editor EDITOR = Editor.getInstance();

    /**
     * Открываемый Asset.
     */
    private final Path assetFolder;

    public ReopenAssetAction(final Path assetFolder) {
        super(assetFolder.toString());
        this.assetFolder = assetFolder;
        setOnAction(event -> process());
    }

    /**
     * @return открываемый Asset.
     */
    private Path getAssetFolder() {
        return assetFolder;
    }

    /**
     * Процесс выбора папки Asset.
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
