package com.ss.editor.ui.component.bar.action;

import com.jme3.asset.AssetManager;
import com.ss.editor.Editor;
import com.ss.editor.Messages;
import com.ss.editor.manager.CustomClasspathManager;
import com.ss.editor.manager.ExecutorManager;
import javafx.scene.control.MenuItem;

/**
 * The action to update a user classpath and to clear asset cache.
 *
 * @author JavaSaBr
 */
public class UpdateClasspathAndAssetCacheAction extends MenuItem {

    /**
     * Instantiates a new UpdateClasspathAndAssetCacheAction.
     */
    public UpdateClasspathAndAssetCacheAction() {
        super(Messages.EDITOR_MENU_OTHER_UPDATE_CLASSPATH_AND_ASSET_CACHE);
        setOnAction(event -> process());
    }

    /**
     * Update classpath and clear asset cache.
     */
    private void process() {
        ExecutorManager.getInstance().addJMETask(() -> {
            final Editor editor = Editor.getInstance();
            final AssetManager assetManager = editor.getAssetManager();
            assetManager.clearCache();

            final CustomClasspathManager classpathManager = CustomClasspathManager.getInstance();
            classpathManager.updateLibraries();
            classpathManager.updateClasses();
        });
    }
}
