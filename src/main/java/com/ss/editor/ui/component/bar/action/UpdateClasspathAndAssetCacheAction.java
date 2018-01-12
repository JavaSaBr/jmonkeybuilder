package com.ss.editor.ui.component.bar.action;

import com.jme3.asset.AssetManager;
import com.ss.editor.JmeApplication;
import com.ss.editor.Messages;
import com.ss.editor.manager.ClasspathManager;
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
        ExecutorManager.getInstance().addJmeTask(() -> {

            final ClasspathManager classpathManager = ClasspathManager.getInstance();
            classpathManager.reload();

            final JmeApplication jmeApplication = JmeApplication.getInstance();
            final AssetManager assetManager = jmeApplication.getAssetManager();
            assetManager.clearCache();
        });
    }
}
