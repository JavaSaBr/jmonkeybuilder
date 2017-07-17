package com.ss.editor.ui.component.bar.action;

import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.analytics.google.GAEvent;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.ChangedCurrentAssetFolderEvent;
import com.ss.editor.ui.scene.EditorFXScene;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.nio.file.Path;

/**
 * The action for opening a new asset folder.
 *
 * @author JavaSaBr
 */
public class OpenAssetAction extends MenuItem {

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    /**
     * Instantiates a new Open asset action.
     */
    public OpenAssetAction() {
        super(Messages.EDITOR_MENU_FILE_OPEN_ASSET);
        setOnAction(event -> process());
    }

    /**
     * The process of selecting an asset folder.
     */
    private void process() {

        final DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(Messages.EDITOR_MENU_FILE_OPEN_ASSET_DIRECTORY_CHOOSER);

        final EditorConfig config = EditorConfig.getInstance();
        final Path currentAsset = config.getCurrentAsset();
        final File currentFolder = currentAsset == null ? null : currentAsset.toFile();

        if (currentFolder == null) {
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        } else {
            chooser.setInitialDirectory(currentFolder);
        }

        GAnalytics.sendPageView("AssetChooseDialog", null, "/dialog/AssetChooseDialog");
        GAnalytics.sendEvent(GAEvent.Category.DIALOG, GAEvent.Action.DIALOG_OPENED, "AssetChooseDialog");

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final File folder = chooser.showDialog(scene.getWindow());

        GAnalytics.sendEvent(GAEvent.Category.DIALOG, GAEvent.Action.DIALOG_CLOSED, "AssetChooseDialog");

        if (folder == null) return;

        final Path newAsset = folder.toPath();
        if (newAsset.equals(currentAsset)) return;

        config.addOpenedAsset(newAsset);
        config.setCurrentAsset(newAsset);
        config.save();

        final ChangedCurrentAssetFolderEvent event = new ChangedCurrentAssetFolderEvent();
        event.setNewAssetFolder(newAsset);

        FX_EVENT_MANAGER.notify(event);
    }
}
