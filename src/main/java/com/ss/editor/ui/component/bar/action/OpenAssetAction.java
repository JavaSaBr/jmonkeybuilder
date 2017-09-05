package com.ss.editor.ui.component.bar.action;

import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.analytics.google.GAEvent;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.dialog.folder.OpenExternalFolderEditorDialog;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.ChangedCurrentAssetFolderEvent;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The action to open a new asset folder.
 *
 * @author JavaSaBr
 */
public class OpenAssetAction extends MenuItem {

    @NotNull
    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    @NotNull
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

        final EditorConfig config = EditorConfig.getInstance();

        if (config.isNativeFileChooser()) {
            openAssetByNative();
        } else {
            openAsset();
        }
    }

    /**
     * Open asset folder using native file chooser.
     */
    private void openAssetByNative() {

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

        final File folder = chooser.showDialog(JFX_APPLICATION.getLastWindow());

        GAnalytics.sendEvent(GAEvent.Category.DIALOG, GAEvent.Action.DIALOG_CLOSED, "AssetChooseDialog");

        if (folder == null) return;

        openAssetFolder(folder.toPath());
    }

    /**
     * Open asset folder use custom file chooser.
     */
    private void openAsset() {

        final OpenExternalFolderEditorDialog dialog = new OpenExternalFolderEditorDialog(this::openAssetFolder);
        dialog.setTitleText(Messages.EDITOR_MENU_FILE_OPEN_ASSET_DIRECTORY_CHOOSER);

        final EditorConfig config = EditorConfig.getInstance();
        final Path currentAsset = config.getCurrentAsset();

        if (currentAsset == null) {
            dialog.setInitDirectory(Paths.get(System.getProperty("user.home")));
        } else {
            dialog.setInitDirectory(currentAsset);
        }

        dialog.show();
    }

    private void openAssetFolder(@NotNull final Path newAsset) {

        final EditorConfig config = EditorConfig.getInstance();
        final Path currentAsset = config.getCurrentAsset();
        if (newAsset.equals(currentAsset)) return;

        config.addOpenedAsset(newAsset);
        config.setCurrentAsset(newAsset);
        config.save();

        final ChangedCurrentAssetFolderEvent event = new ChangedCurrentAssetFolderEvent();
        event.setNewAssetFolder(newAsset);

        FX_EVENT_MANAGER.notify(event);
    }
}
