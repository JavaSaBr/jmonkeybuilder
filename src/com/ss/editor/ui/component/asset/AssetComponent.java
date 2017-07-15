package com.ss.editor.ui.component.asset;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.WorkspaceManager;
import com.ss.editor.model.workspace.Workspace;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.asset.tree.ResourceTree;
import com.ss.editor.ui.component.asset.tree.resource.FolderElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.*;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
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

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    @NotNull
    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    /**
     * The list of waited files to select.
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
        setId(CSSIds.ASSET_COMPONENT);
        createComponents();
        FX_EVENT_MANAGER.addEventHandler(RequestedRefreshAssetEvent.EVENT_TYPE, event -> processRefresh());
        FX_EVENT_MANAGER.addEventHandler(ChangedCurrentAssetFolderEvent.EVENT_TYPE, event -> processChangeAsset());
        FX_EVENT_MANAGER.addEventHandler(CreatedFileEvent.EVENT_TYPE, event -> processEvent((CreatedFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(RequestSelectFileEvent.EVENT_TYPE, event -> processEvent((RequestSelectFileEvent) event));
        FX_EVENT_MANAGER.addEventHandler(DeletedFileEvent.EVENT_TYPE, event -> processEvent((DeletedFileEvent) event));
    }

    /**
     * Gets waited files to select.
     *
     * @return the list of waited files to select.
     */
    @NotNull
    private Array<Path> getWaitedFilesToSelect() {
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
        //FXUtils.bindFixedHeight(resourceTree, heightProperty().subtract(barComponent.heightProperty()));

        FXUtils.addToPane(resourceTree, this);
        FXUtils.bindFixedHeight(resourceTree, heightProperty());
        FXUtils.addClassTo(resourceTree, CSSClasses.TRANSPARENT_LIST_VIEW);
    }

    /**
     * Handle changing loading state of the tree.
     */
    private void handleTreeLoading(@NotNull final Boolean finished) {

        final WorkspaceManager workspaceManager = WorkspaceManager.getInstance();
        final Workspace workspace = workspaceManager.getCurrentWorkspace();

        if (finished && workspace != null) {
            final Array<Path> expandedFolders = workspace.getExpandedAbsoluteFolders();
            expandedFolders.forEach(getResourceTree()::markExpand);
        }

        if (finished) {
            EXECUTOR_MANAGER.addFXTask(() -> setIgnoreExpanded(false));
        } else {
            setIgnoreExpanded(true);
        }
    }

    /**
     * Is ignore expanded boolean.
     *
     * @return true if the expand listener is ignored.
     */
    private boolean isIgnoreExpanded() {
        return ignoreExpanded;
    }

    /**
     * Sets ignore expanded.
     *
     * @param ignoreExpanded the flag for ignoring expand changes.
     */
    private void setIgnoreExpanded(final boolean ignoreExpanded) {
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
                .filter(FolderElement.class::isInstance)
                .map(ResourceElement::getFile)
                .forEach(expanded::add);

        workspace.updateExpandedFolders(expanded);
    }

    /**
     * @return the toolbar of this component.
     */
    @NotNull
    private AssetBarComponent getBarComponent() {
        return notNull(barComponent);
    }

    /**
     * @return the resource tree.
     */
    @NotNull
    private ResourceTree getResourceTree() {
        return notNull(resourceTree);
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
