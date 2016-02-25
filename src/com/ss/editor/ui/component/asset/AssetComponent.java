package com.ss.editor.ui.component.asset;

import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedRefreshAssetTreeEvent;

import java.nio.file.Path;

import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * Компонент для реализации файлового дерева.
 *
 * @author Ronn
 */
public class AssetComponent extends VBox implements ScreenComponent {

    public static final String COMPONENT_ID = "AssetComponent";

    public static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * Компонент тулбара дерева.
     */
    private AssetBarComponent barComponent;

    /**
     * Компонент файлового дерева.
     */
    private ResourceTree resourceTree;

    public AssetComponent() {
        setId(CSSIds.ASSET_COMPONENT);
        createContent();
        FX_EVENT_MANAGER.addEventHandler(RequestedRefreshAssetTreeEvent.EVENT_TYPE, event -> processRefresh());
    }

    /**
     * Обработка обновления компонента.
     */
    private void processRefresh() {
        final ResourceTree resourceTree = getResourceTree();
        resourceTree.refresh();
    }

    private void createContent() {

        this.barComponent = new AssetBarComponent();
        this.resourceTree = new ResourceTree();

        FXUtils.addToPane(barComponent, this);
        FXUtils.addToPane(resourceTree, this);
        FXUtils.bindFixedHeight(resourceTree, heightProperty().subtract(barComponent.heightProperty()));
    }

    /**
     * @return компонент тулбара дерева.
     */
    private AssetBarComponent getBarComponent() {
        return barComponent;
    }

    /**
     * @return компонент файлового дерева.
     */
    private ResourceTree getResourceTree() {
        return resourceTree;
    }

    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }

    @Override
    public void notifyFinishBuild() {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();

        if(currentAsset == null) {
            return;
        }

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.fill(currentAsset);
    }
}
