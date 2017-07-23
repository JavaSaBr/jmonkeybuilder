package com.ss.editor.ui.component.bar.action;

import com.jme3.asset.AssetManager;
import com.ss.editor.Editor;
import com.ss.editor.Messages;
import javafx.scene.control.MenuItem;

/**
 * The action for opening the dialog with settings.
 *
 * @author JavaSaBr
 */
public class ClearAssetCacheAction extends MenuItem {

    /**
     * Instantiates a new Open settings action.
     */
    public ClearAssetCacheAction() {
        super(Messages.EDITOR_MENU_OTHER_CLEAR_ASSET_CACHE);
        setOnAction(event -> process());
    }

    /**
     * The process of opening.
     */
    private void process() {
        final Editor editor = Editor.getInstance();
        final AssetManager assetManager = editor.getAssetManager();
        assetManager.clearCache();
    }
}
