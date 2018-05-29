package com.ss.editor.ui.component.asset;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.component.asset.tree.resource.FolderResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.css.CssIds;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.*;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The component to work with asset tree.
 *
 * @author JavaSaBr
 */
public class AssetComponent extends VBox implements ScreenComponent {

    @NotNull
    private static final String COMPONENT_ID = "AssetComponent";

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final FxEventManager FX_EVENT_MANAGER = FxEventManager.getInstance();

    /**
     * The list of waited files to be selected.
     */
    @NotNull
    private final Array<Path> waitedFilesToSelect;

    /**
     * The toolbar of this component.
     */
    @Nullable
    private AssetBarComponent barComponent;

    /**
     * The resource tree.
     */
    @Nullable
    private ResourceTree resourceTree;

    /**
     * The flag for ignoring expand changes.
     */
    private boolean ignoreExpanded;

    /**
     * Instantiates a new Asset component.
     */
    public AssetComponent() {
        this.waitedFilesToSelect = ArrayFactory.newArray(Path.class);
        setId(CssIds.ASSET_COMPONENT);
        createComponents();
        FX_EVENT_MANAGER.addEventHandler(RequestedRefreshAssetEvent.EVENT_TYPE, event -> refreshAssetFolder());
        FX_EVENT_MANAGER.addEventHandler(ChangedCurrentAssetFolderEvent.EVENT_TYPE, event -> switchAssetFolder());
        FX_EVENT_MANAGER.addEventHandler(CreatedFileEvent.EVENT_TYPE, this::handleCreatedFile);
        FX_EVENT_MANAGER.addEventHandler(RequestSelectFileEvent.EVENT_TYPE, this::handleRequestToSelectFile);
        FX_EVENT_MANAGER.addEventHandler(DeletedFileEvent.EVENT_TYPE, this::handleDeletedFile);
    }

    /**
     * Get the list of waited files to be selected.
     *
     * @return the list of waited files to be selected.
     */
    @FromAnyThread
    private @NotNull Array<Path> getWaitedFilesToSelect() {
        return waitedFilesToSelect;
    }

    /**
     * Handle of the request to select a file.
     */
    @FxThread
    private void handleRequestToSelectFile(@NotNull RequestSelectFileEvent event) {

        var file = event.getFile();

        var resourceTree = getResourceTree();
        var element = ResourceElementFactory.createFor(file);
        var treeItem = UiUtils.findItemForValue(resourceTree.getRoot(), element);

        if (treeItem == null) {
            getWaitedFilesToSelect().add(file);
            return;
        }

        resourceTree.expandTo(treeItem, true);
    }

    /**
     * Handle of the created file.
     */
    @FxThread
    private void handleCreatedFile(@NotNull CreatedFileEvent event) {

        var file = event.getFile();

        var waitedFilesToSelect = getWaitedFilesToSelect();
        var wasWaitedToSelect = waitedFilesToSelect.contains(file);

        var resourceTree = getResourceTree();
        resourceTree.notifyCreated(file);

        if (wasWaitedToSelect) {
            waitedFilesToSelect.fastRemove(file);
        }

        if (wasWaitedToSelect || event.isNeedSelect()) {
            resourceTree.expandTo(file, true);
        }
    }

    /**
     * Handle of the deleted file.
     */
    @FxThread
    private void handleDeletedFile(@NotNull DeletedFileEvent event) {

        var file = event.getFile();

        var resourceTree = getResourceTree();
        resourceTree.notifyDeleted(file);

        var workspaceManager = WorkspaceManager.getInstance();
        var workspace = workspaceManager.getCurrentWorkspace();

        if (workspace != null) {
            workspace.removeEditorState(file);
        }
    }

    /**
     * Switch to a new asset folder.
     */
    @FxThread
    private void switchAssetFolder() {
        loadAssetFolder();
    }

    /**
     * Refresh the current asset folder.
     */
    @FxThread
    private void refreshAssetFolder() {
        getResourceTree().refresh();
    }

    /**
     * Create components.
     */
    @FxThread
    private void createComponents() {
        setIgnoreExpanded(true);

        barComponent = new AssetBarComponent();

        resourceTree = new ResourceTree(false);
        resourceTree.setExpandHandler(this::updateExpanded);
        resourceTree.setOnLoadHandler(this::handleTreeLoading);
        resourceTree.prefHeightProperty().bind(heightProperty());

        //FIXME пока он не нужен
        //FXUtils.addToPane(barComponent, this);
        //FXUtils.bindFixedHeight(resourceTree, heightProperty().subtract(barComponent.heightProperty()));

        FxUtils.addClass(resourceTree, CssClasses.TRANSPARENT_LIST_VIEW);
        FxUtils.addChild(this, resourceTree);
    }

    /**
     * Handle of changing loading state of the tree.
     */
    @FxThread
    private void handleTreeLoading(@NotNull Boolean finished) {

        var workspaceManager = WorkspaceManager.getInstance();
        var workspace = workspaceManager.getCurrentWorkspace();

        if (finished && workspace != null) {
            workspace.getExpandedAbsoluteFolders()
                    .forEach(getResourceTree()::markExpand);
        }

        if (finished) {

            var editorConfig = EditorConfig.getInstance();
            var currentAsset = editorConfig.getCurrentAsset();

            EXECUTOR_MANAGER.addFxTask(() -> setIgnoreExpanded(false));

            if (currentAsset != null) {
                FX_EVENT_MANAGER.notify(new AssetComponentLoadedEvent(currentAsset));
            }

        } else {
            setIgnoreExpanded(true);
        }
    }

    /**
     * Return true if the expand listener is ignored.
     *
     * @return true if the expand listener is ignored.
     */
    @FromAnyThread
    private boolean isIgnoreExpanded() {
        return ignoreExpanded;
    }

    /**
     * Set true if the expand listener is ignored.
     *
     * @param ignoreExpanded true if the expand listener is ignored.
     */
    @FromAnyThread
    private void setIgnoreExpanded(boolean ignoreExpanded) {
        this.ignoreExpanded = ignoreExpanded;
    }

    /**
     * Handle changes count of expanded folders.
     */
    @FxThread
    private void updateExpanded(int count, ResourceTree tree) {

        if (isIgnoreExpanded()) {
            return;
        }

        var workspaceManager = WorkspaceManager.getInstance();
        var workspace = workspaceManager.getCurrentWorkspace();
        if (workspace == null) {
            return;
        }

        var expanded = ArrayFactory.<Path>newArray(Path.class);
        var allItems = UiUtils.getAllItems(tree);
        allItems.stream().filter(TreeItem::isExpanded)
                .filter(treeItem -> !treeItem.isLeaf())
                .map(TreeItem::getValue)
                .filter(FolderResourceElement.class::isInstance)
                .map(ResourceElement::getFile)
                .forEach(expanded::add);

        workspace.updateExpandedFolders(expanded);
    }

    /**
     * Get the toolbar of this component.
     *
     * @return the toolbar of this component.
     */
    @FxThread
    private @NotNull AssetBarComponent getBarComponent() {
        return notNull(barComponent);
    }

    /**
     * Get the resource tree.
     *
     * @return the resource tree.
     */
    @FxThread
    private @NotNull ResourceTree getResourceTree() {
        return notNull(resourceTree);
    }

    @Override
    @FromAnyThread
    public String getComponentId() {
        return COMPONENT_ID;
    }

    @Override
    @FxThread
    public void notifyFinishBuild() {
        loadAssetFolder();
    }

    @FxThread
    private void loadAssetFolder() {

        var editorConfig = EditorConfig.getInstance();
        var currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) {
            return;
        }

        getResourceTree().fill(currentAsset);
    }
}
