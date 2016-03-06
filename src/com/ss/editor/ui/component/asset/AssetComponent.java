package com.ss.editor.ui.component.asset;

import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.ChangedCurrentAssetFolderEvent;
import com.ss.editor.ui.event.impl.CreatedFileEvent;
import com.ss.editor.ui.event.impl.DeletedFileEvent;
import com.ss.editor.ui.event.impl.MovedFileEvent;
import com.ss.editor.ui.event.impl.RenamedFileEvent;
import com.ss.editor.ui.event.impl.RequestedRefreshAssetEvent;

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

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

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
        FX_EVENT_MANAGER.addEventHandler(RequestedRefreshAssetEvent.EVENT_TYPE, event -> processRefresh());
        FX_EVENT_MANAGER.addEventHandler(ChangedCurrentAssetFolderEvent.EVENT_TYPE, event -> processChangeAsset());
        FX_EVENT_MANAGER.addEventHandler(CreatedFileEvent.EVENT_TYPE, event -> processEvent((CreatedFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(DeletedFileEvent.EVENT_TYPE, event -> processEvent((DeletedFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(RenamedFileEvent.EVENT_TYPE, event -> processEvent((RenamedFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(MovedFileEvent.EVENT_TYPE, event -> processEvent((MovedFileEvent) event));
    }

    /**
     * Обработка перемещения файла.
     */
    private void processEvent(final MovedFileEvent event) {

        final Path newFile = event.getNewFile();
        final Path prevFile = event.getPrevFile();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyMoved(prevFile, newFile);
    }

    /**
     * Обработка переименования файла.
     */
    private void processEvent(final RenamedFileEvent event) {

        final Path newFile = event.getNewFile();
        final Path prevFile = event.getPrevFile();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyRenamed(prevFile, newFile);
    }

    /**
     * Обработка появления нового файла в Asset.
     */
    private void processEvent(final CreatedFileEvent event) {

        final Path file = event.getFile();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyCreated(file);

        if (event.isNeedSelect()) {
            resourceTree.expandTo(file, true);
        }
    }

    /**
     * Обработка удаления файла из Asset.
     */
    private void processEvent(final DeletedFileEvent event) {

        final Path file = event.getFile();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyDeleted(file);
    }

    /**
     * Обработка смены дериктории Asset.
     */
    private void processChangeAsset() {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();

        if (currentAsset == null) {
            return;
        }

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.fill(currentAsset);
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
        this.resourceTree = new ResourceTree(false);

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

        if (currentAsset == null) {
            return;
        }

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.fill(currentAsset);
    }
}
