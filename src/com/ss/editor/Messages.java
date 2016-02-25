package com.ss.editor;

import java.util.ResourceBundle;

/**
 * Набор констант с локализованными сообщениями.
 *
 * @author Ronn
 */
public class Messages {

    public static final String BUNDLE_NAME = "messages/messages";

    public static final String EDITOR_BAR_ASSET;
    public static final String EDITOR_BAR_ASSET_OPEN_ASSET;
    public static final String EDITOR_BAR_ASSET_OPEN_ASSET_DIRECTORY_CHOOSER;

    public static final String ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE;

    public static final String FILE_EDITOR_ACTION_SAVE;
    public static final String FILE_EDITOR_TEXT_EDITOR;

    static {

        final ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, ResourceControl.getInstance());

        EDITOR_BAR_ASSET = bundle.getString("EditorBarComponent.asset");
        EDITOR_BAR_ASSET_OPEN_ASSET = bundle.getString("EditorBarComponent.asset.openAsset");
        EDITOR_BAR_ASSET_OPEN_ASSET_DIRECTORY_CHOOSER = bundle.getString("EditorBarComponent.asset.openAsset.DirectoryChooser");

        ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE = bundle.getString("AssetComponentResourceTreeContextMenuOpenFile");

        FILE_EDITOR_ACTION_SAVE = bundle.getString("FileEditorActionSave");
        FILE_EDITOR_TEXT_EDITOR = bundle.getString("FileEditorTextEditor");
    }
}
