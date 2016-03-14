package com.ss.editor.ui.component.bar.action;

import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.ChangedCurrentAssetFolderEvent;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 * Реализация действия по переоткрытию папки с Asset.
 *
 * @author Ronn
 */
public class ReopenAssetMenu extends Menu {

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    public ReopenAssetMenu() {
        super(Messages.EDITOR_BAR_ASSET_REOPEN_ASSET_FOLDER);
        update();
        FX_EVENT_MANAGER.addEventHandler(ChangedCurrentAssetFolderEvent.EVENT_TYPE, event -> update());
    }

    /**
     * Обновление списка для переоткрытия.
     */
    public void update() {

        final ObservableList<MenuItem> items = getItems();
        items.clear();

        setDisable(true);

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final List<String> assets = editorConfig.getLastOpenedAssets();

        if (assets.size() < 2) {
            return;
        }

        for (int i = 1; i < assets.size(); i++) {

            final String filePath = assets.get(i);
            final Path assetFolder = Paths.get(filePath);

            items.add(new ReopenAssetAction(assetFolder));
        }

        setDisable(items.isEmpty());
    }
}
