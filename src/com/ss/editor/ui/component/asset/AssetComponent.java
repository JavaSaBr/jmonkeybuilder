package com.ss.editor.ui.component.asset;

import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.model.workspace.Workspace;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.component.asset.tree.resource.FolderElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.ChangedCurrentAssetFolderEvent;
import com.ss.editor.ui.event.impl.CreatedFileEvent;
import com.ss.editor.ui.event.impl.DeletedFileEvent;
import com.ss.editor.ui.event.impl.RequestSelectFileEvent;
import com.ss.editor.ui.event.impl.RequestedRefreshAssetEvent;
import com.ss.editor.ui.util.UIUtils;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

import javafx.geometry.Insets;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The component for working with asset tree.
 *
 * @author JavaSaBr.
 */
public class AssetComponent extends VBox implements ScreenComponent {

    public static final String COMPONENT_ID = "AssetComponent";

    private static final Insets TREE_OFFSET = new Insets(6, 3, 0, 0);

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The list of waited files to select.
     */
    @NotNull
    private final Array<Path> waitedFilesToSelect;

    /**
     * The toolbar of this component.
     */
    private AssetBarComponent barComponent;

    /**
     * The resource tree.
     */
    private ResourceTree resourceTree;

    /**
     * The flag for ignoring expand changes.
     */
    private boolean ignoreExpanded;

    public AssetComponent() {
        this.waitedFilesToSelect = ArrayFactory.newArray(Path.class);
        setId(CSSIds.ASSET_COMPONENT);
        createComponents();
        FX_EVENT_MANAGER.addEventHandler(RequestedRefreshAssetEvent.EVENT_TYPE, event -> processRefresh());
        FX_EVENT_MANAGER.addEventHandler(ChangedCurrentAssetFolderEvent.EVENT_TYPE, event -> processChangeAsset());
        FX_EVENT_MANAGER.addEventHandler(CreatedFileEvent.EVENT_TYPE, event -> processEvent((CreatedFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(RequestSelectFileEvent.EVENT_TYPE, event -> processEvent((RequestSelectFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(DeletedFileEvent.EVENT_TYPE, event -> processEvent((DeletedFileEvent) event));
    }

    /**
     * @return the list of waited files to select.
     */
    @NotNull
    public Array<Path> getWaitedFilesToSelect() {
        return waitedFilesToSelect;
    }

    /**
     * Handle request for selection a file.
     */
    private void processEvent(@NotNull final RequestSelectFileEvent event) {

        final Path file = event.getFile();

        final ResourceTree resourceTree = getResourceTree();
        final ResourceElement element = ResourceElementFactory.createFor(file);
        final TreeItem<ResourceElement> treeItem = UIUtils.findItemForValue(resourceTree.getRoot(), element);

        if (treeItem == null) {
            getWaitedFilesToSelect().add(file);
            return;
        }

        resourceTree.expandTo(treeItem, true);
    }

    /**
     * Handle a created file.
     */
    private void processEvent(@NotNull final CreatedFileEvent event) {

        final Path file = event.getFile();

        final Array<Path> waitedFilesToSelect = getWaitedFilesToSelect();
        final boolean waitedSelect = waitedFilesToSelect.contains(file);

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyCreated(file);

        if (waitedSelect) waitedFilesToSelect.fastRemove(file);
        if (waitedSelect || event.isNeedSelect()) resourceTree.expandTo(file, true);
    }

    /**
     * Handle a deleted file.
     */
    private void processEvent(@NotNull final DeletedFileEvent event) {

        final Path file = event.getFile();

        final ResourceTree resourceTree = getResourceTree();
        resourceTree.notifyDeleted(file);

        final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
        final Workspace workspace = workspaceManager.getCurrentWorkspace();
        if (workspace == null) return;
        workspace.removeEditorState(file);
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
        setIgnoreExpanded(true);

        this.barComponent = new AssetBarComponent();
        this.resourceTree = new ResourceTree(false);
        this.resourceTree.setExpandHandler(this::updateExpanded);
        this.resourceTree.setOnLoadHandler(this::handleTreeLoading);

        //FIXME пока он не нужен
        //FXUtils.addToPane(barComponent, this);
        FXUtils.addToPane(resourceTree, this);
        FXUtils.bindFixedHeight(resourceTree, heightProperty().subtract(TREE_OFFSET.getTop()));
        //FXUtils.bindFixedHeight(resourceTree, heightProperty().subtract(barComponent.heightProperty()));

        VBox.setMargin(resourceTree, TREE_OFFSET);
    }

    /**
     * Handle changing loading state of the tree.
     */
    private void handleTreeLoading(@NotNull final Boolean finished) {

        final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
        final Workspace workspace = workspaceManager.getCurrentWorkspace();

        if (finished && workspace != null) {
            final Array<Path> expandedFolders = workspace.getExpandedFolders();
            expandedFolders.forEach(resourceTree::markExpand);
        }

        if (finished) {
            EXECUTOR_MANAGER.addFXTask(() -> setIgnoreExpanded(false));
        } else {
            setIgnoreExpanded(true);
        }
    }

    /**
     * @return true if the expand listener is ignored.
     */
    public boolean isIgnoreExpanded() {
        return ignoreExpanded;
    }

    /**
     * @param ignoreExpanded the flag for ignoring expand changes.
     */
    public void setIgnoreExpanded(final boolean ignoreExpanded) {
        this.ignoreExpanded = ignoreExpanded;
    }

    /**
     * Handle changes count of expanded folders.
     */
    private void updateExpanded(final int count, final ResourceTree tree) {
        if (isIgnoreExpanded()) return;

        final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
        final Workspace workspace = workspaceManager.getCurrentWorkspace();
        if (workspace == null) return;

        final Array<Path> expanded = ArrayFactory.newArray(Path.class);
        final Array<TreeItem<ResourceElement>> allItems = UIUtils.getAllItems(tree);
        allItems.stream().filter(TreeItem::isExpanded)
                .filter(treeItem -> !treeItem.isLeaf())
                .map(TreeItem::getValue)
                .filter(element -> element instanceof FolderElement)
                .map(ResourceElement::getFile)
                .forEach(expanded::add);

        workspace.updateExpandedFolders(expanded);
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
