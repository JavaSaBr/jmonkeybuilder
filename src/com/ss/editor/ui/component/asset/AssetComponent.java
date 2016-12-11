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

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The component for working with asset tree.
 *
 * @author JavaSaBr.
 */
public class AssetComponent extends VBox implements ScreenComponent {

    public static final String COMPONENT_ID = "AssetComponent";

    private static final Insets TREE_OFFSET = new Insets(6, 3, 0, 0);

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The toolbar of this component.
     */
    private AssetBarComponent barComponent;

    /**
     * The resource tree.
     */
    private ResourceTree resourceTree;

    public AssetComponent() {
        setId(CSSIds.ASSET_COMPONENT);
        createComponents();
        FX_EVENT_MANAGER.addEventHandler(RequestedRefreshAssetEvent.EVENT_TYPE, event -> processRefresh());
        FX_EVENT_MANAGER.addEventHandler(ChangedCurrentAssetFolderEvent.EVENT_TYPE, event -> processChangeAsset());
        FX_EVENT_MANAGER.addEventHandler(CreatedFileEvent.EVENT_TYPE, event -> processEvent((CreatedFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(DeletedFileEvent.EVENT_TYPE, event -> processEvent((DeletedFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(RenamedFileEvent.EVENT_TYPE, event -> processEvent((RenamedFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(MovedFileEvent.EVENT_TYPE, event -> processEvent((MovedFileEvent) event));
    }

    /**
     * Handle a moved file.
     */
    private void processEvent(final MovedFileEvent event) {

        final Path newFile = event.getNewFile();
        final Path prevFile = event.getPrevFile();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyMoved(prevFile, newFile);
    }

    /**
     * Handle a renamed file.
     */
    private void processEvent(final RenamedFileEvent event) {

        final Path newFile = event.getNewFile();
        final Path prevFile = event.getPrevFile();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyRenamed(prevFile, newFile);
    }

    /**
     * Handle a created file.
     */
    private void processEvent(final CreatedFileEvent event) {

        final Path file = event.getFile();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyCreated(file);

        if (event.isNeedSelect()) resourceTree.expandTo(file, true);
    }

    /**
     * Handle a deleted file.
     */
    private void processEvent(final DeletedFileEvent event) {

        final Path file = event.getFile();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyDeleted(file);
    }

    /**
     * Handle changing an asset folder.
     */
    private void processChangeAsset() {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) return;

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.fill(currentAsset);
    }

    /**
     * Handle refreshing.
     */
    private void processRefresh() {
        final ResourceTree resourceTree = getResourceTree();
        resourceTree.refresh();
    }

    /**
     * Create components.
     */
    private void createComponents() {

        this.barComponent = new AssetBarComponent();
        this.resourceTree = new ResourceTree(false);

        //FIXME пока он не нужен
        //FXUtils.addToPane(barComponent, this);
        FXUtils.addToPane(resourceTree, this);
        FXUtils.bindFixedHeight(resourceTree, heightProperty().subtract(TREE_OFFSET.getTop()));
        //FXUtils.bindFixedHeight(resourceTree, heightProperty().subtract(barComponent.heightProperty()));

        VBox.setMargin(resourceTree, TREE_OFFSET);
    }

    /**
     * @return the toolbar of this component.
     */
    private AssetBarComponent getBarComponent() {
        return barComponent;
    }

    /**
     * @return the resource tree.
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
        if (currentAsset == null) return;

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.fill(currentAsset);
    }
}
