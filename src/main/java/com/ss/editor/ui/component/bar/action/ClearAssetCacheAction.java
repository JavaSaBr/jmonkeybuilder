package com.ss.editor.ui.component.bar.action;

import com.jme3.asset.AssetManager;
import com.ss.editor.Editor;
import com.ss.editor.Messages;
import com.ss.editor.manager.ExecutorManager;
import javafx.scene.control.MenuItem;

/**
 * The action to clean asset cache.
 *
 * @author JavaSaBr
 */
public class ClearAssetCacheAction extends MenuItem {

    /**
     * Instantiates a new ClearAssetCacheAction.
     */
    public ClearAssetCacheAction() {
        super(Messages.EDITOR_MENU_OTHER_CLEAR_ASSET_CACHE);
        setOnAction(event -> process());
    }

    /**
     * Clear asset cache.
     */
    private void process() {
        ExecutorManager.getInstance().addJMETask(() -> {
            final Editor editor = Editor.getInstance();
            final AssetManager assetManager = editor.getAssetManager();
            assetManager.clearCache();
        });
    }
}
