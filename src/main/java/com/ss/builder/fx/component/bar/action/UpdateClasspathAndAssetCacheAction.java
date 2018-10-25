package com.ss.builder.fx.component.bar.action;

import com.jme3.asset.AssetManager;
import com.ss.builder.Messages;
import com.ss.builder.manager.ClasspathManager;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.Messages;
import com.ss.builder.manager.ClasspathManager;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.util.EditorUtils;
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

            final AssetManager assetManager = EditorUtils.getAssetManager();
            assetManager.clearCache();
        });
    }
}