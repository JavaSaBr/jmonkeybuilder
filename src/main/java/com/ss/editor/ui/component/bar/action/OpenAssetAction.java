package com.ss.editor.ui.component.bar.action;

import static com.ss.editor.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_NATIVE_FILE_CHOOSER;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.PREF_NATIVE_FILE_CHOOSER;
import com.ss.editor.Messages;
import com.ss.editor.analytics.google.GAEvent;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.dialog.file.chooser.OpenExternalFolderEditorDialog;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.ChangedCurrentAssetFolderEvent;
import com.ss.editor.util.EditorUtils;
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
    private static final FxEventManager FX_EVENT_MANAGER = FxEventManager.getInstance();

    public OpenAssetAction() {
        super(Messages.EDITOR_MENU_FILE_OPEN_ASSET);
        setOnAction(event -> process());
    }

    /**
     * The process of selecting an asset folder.
     */
    @FxThread
    private void process() {

        final EditorConfig config = EditorConfig.getInstance();

        if (config.getBoolean(PREF_NATIVE_FILE_CHOOSER, PREF_DEFAULT_NATIVE_FILE_CHOOSER)) {
            openAssetByNative();
        } else {
            openAsset();
        }
    }

    /**
     * Open asset folder using native file chooser.
     */
    @FxThread
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

        final File folder = chooser.showDialog(EditorUtils.getFxLastWindow());

        GAnalytics.sendEvent(GAEvent.Category.DIALOG, GAEvent.Action.DIALOG_CLOSED, "AssetChooseDialog");

        if (folder == null) {
            return;
        }

        openAssetFolder(folder.toPath());
    }

    /**
     * Open an asset folder using custom file chooser.
     */
    @FxThread
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

    /**
     * Open the asset folder.
     *
     * @param newAsset the asset folder.
     */
    @FxThread
    public void openAssetFolder(@NotNull final Path newAsset) {

        final EditorConfig config = EditorConfig.getInstance();
        final Path currentAsset = config.getCurrentAsset();
        if (newAsset.equals(currentAsset)) return;

        config.addOpenedAsset(newAsset);
        config.setCurrentAsset(newAsset);
        config.save();

        FX_EVENT_MANAGER.notify(new ChangedCurrentAssetFolderEvent(newAsset));
    }
}
