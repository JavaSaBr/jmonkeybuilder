package com.ss.editor.ui.component.asset.tree;

import static com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory.createFor;
import static com.ss.editor.ui.util.UiUtils.findItemForValue;
import static com.ss.editor.ui.util.UiUtils.hasFileInClipboard;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.FxConstants;
import com.ss.editor.ui.component.asset.tree.context.menu.action.*;
import com.ss.editor.ui.component.asset.tree.resource.*;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.common.function.IntObjectConsumer;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayComparator;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.array.ConcurrentArray;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * THe implementation of a tree with resources of an asset folder.
 *
 * @author JavaSaBr
 */
public class ResourceTree extends TreeView<ResourceElement> {

    /**
     * The executor manager.
     */
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The resource elements comparator.
     */
    private static final ArrayComparator<ResourceElement> COMPARATOR = ResourceElement::compareTo;

    /**
     * The name comparator.
     */
    private static final ArrayComparator<ResourceElement> NAME_COMPARATOR = (first, second) -> {

        var firstLevel = getLevel(first);
        var secondLevel = getLevel(second);

        if (firstLevel != secondLevel) {
            return firstLevel - secondLevel;
        }

        return StringUtils.compareIgnoreCase(getNameToSort(first), getNameToSort(second));
    };

    private static @NotNull String getNameToSort(@NotNull ResourceElement element) {
        var file = notNull(element).getFile();
        var fileName = file.getFileName();
        return fileName == null ? file.toString() : fileName.toString();
    }

    /**
     * The tree items comparator.
     */
    private static final ArrayComparator<TreeItem<ResourceElement>> ITEM_COMPARATOR = (first, second) -> {
        var firstElement = notNull(first).getValue();
        var secondElement = notNull(second).getValue();
        return NAME_COMPARATOR.compare(firstElement, secondElement);
    };

    /**
     * The context menu filler registry.
     */
    private static final AssetTreeContextMenuFillerRegistry CONTEXT_MENU_FILLER_REGISTRY =
            AssetTreeContextMenuFillerRegistry.getInstance();

    @FromAnyThread
    private static int getLevel(@Nullable ResourceElement element) {
        return element instanceof FolderResourceElement ? 1 : 2;
    }

    /**
     * The default open function.
     */
    @NotNull
    private static final Consumer<ResourceElement> DEFAULT_OPEN_FUNCTION =
            element -> new OpenFileAction(element)
                    .getOnAction()
                    .handle(null);

    /**
     * The list of expanded elements.
     */
    @NotNull
    private final ConcurrentArray<ResourceElement> expandedElements;

    /**
     * The list of selected elements.
     */
    @NotNull
    private final ConcurrentArray<ResourceElement> selectedElements;

    /**
     * The open resource function.
     */
    @Nullable
    private final Consumer<ResourceElement> openFunction;

    /**
     * The tree item event handler.
     */
    @NotNull
    private final EventHandler<TreeModificationEvent<ResourceElement>> treeItemEventHandler;

    /**
     * The action tester.
     */
    @NotNull
    private Predicate<Class<?>> actionTester;

    /**
     * The list of filtered extensions.
     */
    @NotNull
    private Array<String> extensionFilter;

    /**
     * The post loading handler.
     */
    @Nullable
    private Consumer<Boolean> onLoadHandler;

    /**
     * The handler for listening expand items.
     */
    @Nullable
    private IntObjectConsumer<ResourceTree> expandHandler;

    /**
     * The flag of read only mode.
     */
    private final boolean readOnly;

    /**
     * The flag of showing only folders.
     */
    private boolean onlyFolders;

    /**
     * The flag of using lazy mode.
     */
    private boolean lazyMode;

    /**
     * The flag of using cleanup resource tree.
     */
    private boolean needCleanup;

    public ResourceTree(boolean readOnly) {
        this(DEFAULT_OPEN_FUNCTION, readOnly);
    }

    public ResourceTree(@Nullable Consumer<ResourceElement> openFunction, boolean readOnly) {
        this.openFunction = openFunction;
        this.readOnly = readOnly;
        this.expandedElements = ArrayFactory.newConcurrentAtomicARSWLockArray(ResourceElement.class);
        this.selectedElements = ArrayFactory.newConcurrentAtomicARSWLockArray(ResourceElement.class);
        this.extensionFilter = ArrayFactory.newArray(String.class, 0);
        this.actionTester = actionClass -> true;
        this.treeItemEventHandler = this::processChangedExpands;

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setFixedCellSize(FxConstants.RESOURCE_TREE_CELL_HEIGHT);
        setCellFactory(param -> new ResourceTreeCell());
        setOnKeyPressed(this::processKey);
        setShowRoot(true);
        setContextMenu(new ContextMenu());
        setFocusTraversable(true);

        rootProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.removeEventHandler(TreeItem.treeNotificationEvent(), treeItemEventHandler);
            }
            if (newValue != null) {
                newValue.addEventHandler(TreeItem.treeNotificationEvent(), treeItemEventHandler);
            }
        });
    }

    /**
     * Set true if need to use lazy mode.
     *
     * @param lazyMode true if need to use lazy mode.
     */
    @FromAnyThread
    public void setLazyMode(boolean lazyMode) {
        this.lazyMode = lazyMode;
    }

    /**
     * Return true if need to use lazy mode.
     *
     * @return true if need to use lazy mode.
     */
    @FromAnyThread
    private boolean isLazyMode() {
        return lazyMode;
    }

    /**
     * Set true if need to cleanup this tree.
     *
     * @param needCleanup true if need to cleanup this tree.
     */
    @FromAnyThread
    public void setNeedCleanup(boolean needCleanup) {
        this.needCleanup = needCleanup;
    }

    /**
     * Return true if need to cleanup this tree.
     *
     * @return true if need to cleanup this tree.
     */
    @FromAnyThread
    private boolean isNeedCleanup() {
        return needCleanup;
    }

    /**
     * Handle changed count of expanded elements.
     */
    @FxThread
    private void processChangedExpands(@NotNull TreeModificationEvent<?> event) {

        if (!(event.wasExpanded() || event.wasCollapsed())) {
            return;
        }

        if (isLazyMode()) {
            EXECUTOR_MANAGER.addFxTask(this::lazyLoadChildren);
        }

        getExpandHandler().ifPresent(handler ->
                handler.accept(getExpandedItemCount(), this));
    }

    /**
     * Start the process of loading children of the tree item in the background.
     */
    @FxThread
    private void lazyLoadChildren() {

        Array<TreeItem<ResourceElement>> expanded = ArrayFactory.newArray(TreeItem.class);

        UiUtils.allItems(getRoot())
                .filter(TreeItem::isExpanded)
                .filter(treeItem -> !treeItem.isLeaf())
                .filter(item -> item.getChildren().size() == 1)
                .filter(item -> item.getChildren().get(0).getValue() == LoadingResourceElement.getInstance())
                .forEach(expanded::add);

        for (var treeItem : expanded) {
            EXECUTOR_MANAGER.addBackgroundTask(() -> lazyLoadChildren(treeItem, null));
        }
    }

    /**
     * Load children of the tree item in the background.
     *
     * @param treeItem the tree item.
     */
    @BackgroundThread
    private void lazyLoadChildren(
            @NotNull TreeItem<ResourceElement> treeItem,
            @Nullable Consumer<TreeItem<ResourceElement>> callback
    ) {

        var element = treeItem.getValue();
        var children = element.getChildren(extensionFilter, isOnlyFolders());
        if (children == null) {
            return;
        }

        children.sort(NAME_COMPARATOR);

        EXECUTOR_MANAGER.addFxTask(() -> lazyLoadChildren(treeItem, children, callback));
    }

    /**
     * Show loaded children in the tree.
     *
     * @param treeItem the tree item.
     * @param children the loaded children.
     * @param callback the loading callback.
     */
    @FxThread
    private void lazyLoadChildren(
            @NotNull TreeItem<ResourceElement> treeItem,
            @NotNull Array<ResourceElement> children,
            @Nullable Consumer<TreeItem<ResourceElement>> callback
    ) {

        var items = treeItem.getChildren();
        if (items.size() != 1 || items.get(0).getValue() != LoadingResourceElement.getInstance()) {
            if (callback != null) callback.accept(treeItem);
            return;
        }

        children.forEach(child -> items.add(new TreeItem<>(child)));

        items.remove(0);
        items.forEach(this::fill);

        if (isNeedCleanup()) {
            cleanup(treeItem);
        }

        if (callback != null) {
            callback.accept(treeItem);
        }
    }

    /**
     * Set the expand handler.
     *
     * @param expandHandler the expand handler.
     */
    @FromAnyThread
    public void setExpandHandler(@Nullable IntObjectConsumer<ResourceTree> expandHandler) {
        this.expandHandler = expandHandler;
    }

    /**
     * Set the action tester.
     *
     * @param actionTester the action tester.
     */
    @FromAnyThread
    public void setActionTester(@NotNull Predicate<Class<?>> actionTester) {
        this.actionTester = actionTester;
    }

    /**
     * Get the expand handler.
     *
     * @return the expand handler.
     */
    @FromAnyThread
    private @NotNull Optional<IntObjectConsumer<ResourceTree>> getExpandHandler() {
        return Optional.ofNullable(expandHandler);
    }

    /**
     * Set the list of filtered extensions.
     *
     * @param extensionFilter the list of filtered extensions.
     */
    @FromAnyThread
    public void setExtensionFilter(@NotNull Array<String> extensionFilter) {
        this.extensionFilter = extensionFilter;
    }

    /**
     * Get the list of filtered extensions.
     *
     * @return the list of filtered extensions.
     */
    @FromAnyThread
    private @NotNull Array<String> getExtensionFilter() {
        return extensionFilter;
    }

    /**
     * Set the on load handler.
     *
     * @param onLoadHandler the on load handler.
     */
    @FromAnyThread
    public void setOnLoadHandler(@Nullable Consumer<Boolean> onLoadHandler) {
        this.onLoadHandler = onLoadHandler;
    }

    /**
     * Get the post loading handler.
     *
     * @return the post loading handler.
     */
    @FromAnyThread
    private @Nullable Consumer<Boolean> getOnLoadHandler() {
        return onLoadHandler;
    }

    /**
     * Return  true if this tree is read only.
     *
     * @return true if this tree is read only.
     */
    @FromAnyThread
    private boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Get the action tester.
     *
     * @return the action tester.
     */
    @FromAnyThread
    private @NotNull Predicate<Class<?>> getActionTester() {
        return actionTester;
    }

    /**
     * Get a new context menu.
     *
     * @param element the current selected element.
     * @return the context menu for the element.
     */
    @FxThread
    protected @Nullable ContextMenu getContextMenu(@NotNull ResourceElement element) {

        if (isReadOnly()) {
            return null;
        }

        var contextMenu = new ContextMenu();
        var items = contextMenu.getItems();
        var actionTester = getActionTester();

        var selectionModel = getSelectionModel();
        var selectedItems = selectionModel.getSelectedItems();

        if (selectedItems.size() == 1) {
            for (var filler : CONTEXT_MENU_FILLER_REGISTRY.getSingleFillers()) {
                filler.fill(element, items, actionTester);
            }
        }

        if (selectedItems.size() >= 1) {
            updateSelectedElements().runInReadLock(resourceElements -> {
                for (var filler : CONTEXT_MENU_FILLER_REGISTRY.getMultiFillers()) {
                    filler.fill(resourceElements, items, actionTester);
                }
            });
        }

        if (items.isEmpty()) {
            return null;
        }

        return contextMenu;
    }

    /**
     * Fill the tree using the root folder.
     *
     * @param rootFolder the root folder.
     */
    @FxThread
    public void fill(@NotNull Path rootFolder) {
        prepareToFill();
        EXECUTOR_MANAGER.addBackgroundTask(() -> startBackgroundFill(rootFolder));
    }

    /**
     * Fill the tree using the list of root folders.
     *
     * @param rootFolders the list of root folder.
     */
    @FxThread
    public void fill(@NotNull Array<Path> rootFolders) {
        prepareToFill();
        EXECUTOR_MANAGER.addBackgroundTask(() -> startBackgroundFill(rootFolders));
    }

    /**
     * Prepare this component to fill again.
     */
    @FxThread
    protected void prepareToFill() {

        var onLoadHandler = getOnLoadHandler();
        if (onLoadHandler != null) {
            onLoadHandler.accept(Boolean.FALSE);
        }

        var currentRoot = getRoot();
        if (currentRoot != null) {
            setRoot(null);
        }

        showLoading();
    }

    /**
     * Get the list of expanded elements.
     *
     * @return the list of expanded elements.
     */
    @FromAnyThread
    private @NotNull ConcurrentArray<ResourceElement> getExpandedElements() {
        return expandedElements;
    }

    /**
     * Get the list of selected elements.
     *
     * @return the list of selected elements.
     */
    @FromAnyThread
    private @NotNull ConcurrentArray<ResourceElement> getSelectedElements() {
        return selectedElements;
    }

    /**
     * Refresh this tree.
     */
    @FxThread
    public void refresh() {

        var config = EditorConfig.getInstance();
        var currentAsset = config.getCurrentAsset();

        if (currentAsset == null) {
            setRoot(null);
            return;
        }

        var onLoadHandler = getOnLoadHandler();
        if (onLoadHandler != null) {
            onLoadHandler.accept(Boolean.FALSE);
        }

        updateSelectedElements();
        updateExpandedElements();

        setRoot(null);
        showLoading();

        EXECUTOR_MANAGER.addBackgroundTask(() -> startBackgroundRefresh(currentAsset));
    }

    /**
     * Update the list of expanded elements.
     */
    @FxThread
    private void updateExpandedElements() {

        var elements = getExpandedElements();
        var stamp = elements.writeLock();
        try {

            elements.clear();

            UiUtils.allItems(getRoot())
                    .filter(TreeItem::isExpanded)
                    .forEach(item -> elements.add(item.getValue()));

        } finally {
            elements.writeUnlock(stamp);
        }
    }

    /**
     * Update the list of selected elements.
     */
    @FxThread
    private @NotNull ConcurrentArray<ResourceElement> updateSelectedElements() {

        var elements = getSelectedElements();
        var stamp = elements.writeLock();
        try {

            elements.clear();

            getSelectionModel()
                    .getSelectedItems()
                    .forEach(item -> elements.add(item.getValue()));

        } finally {
            elements.writeUnlock(stamp);
        }

        return elements;
    }

    /**
     * Show the process of loading.
     */
    @FxThread
    private void showLoading() {
        setRoot(new TreeItem<>(LoadingResourceElement.getInstance()));
    }

    /**
     * Start the background process of filling.
     */
    @BackgroundThread
    private void startBackgroundFill(@NotNull Path path) {

        var rootElement = createFor(path);
        var newRoot = new TreeItem<ResourceElement>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot);

        if (!isLazyMode() && isNeedCleanup()) {
            cleanup(newRoot);
        }

        EXECUTOR_MANAGER.addFxTask(() -> applyNewRoot(newRoot));
    }

    /**
     * Applies the new root.
     *
     * @param newRoot the new root,
     */
    @FxThread
    private void applyNewRoot(@NotNull TreeItem<ResourceElement> newRoot) {
        setRoot(newRoot);

        var onLoadHandler = getOnLoadHandler();
        if (onLoadHandler != null) {
            onLoadHandler.accept(Boolean.TRUE);
        }
    }

    /**
     * Start the background process of filling.
     */
    @BackgroundThread
    private void startBackgroundFill(@NotNull Array<Path> paths) {

        var rootElement = new FoldersResourceElement(paths);
        var newRoot = new TreeItem<ResourceElement>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot);

        if (!isLazyMode() && isNeedCleanup()) {
            cleanup(newRoot);
        }

        EXECUTOR_MANAGER.addFxTask(() -> applyNewRoot(newRoot));
    }

    /**
     * Start the background process of loading.
     */
    @BackgroundThread
    private void startBackgroundRefresh(@NotNull Path assetFolder) {

        var rootElement = createFor(assetFolder);
        var newRoot = new TreeItem<ResourceElement>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot);

        var expandedElements = getExpandedElements();
        var stamp = expandedElements.writeLock();
        try {

            expandedElements.sort(COMPARATOR);
            expandedElements.forEach(element -> {

                var item = findItemForValue(newRoot, element);
                if (item == null) {
                    return;
                }

                item.setExpanded(true);
            });

            expandedElements.clear();

        } finally {
            expandedElements.writeUnlock(stamp);
        }

        EXECUTOR_MANAGER.addFxTask(() -> {

            setRoot(newRoot);
            restoreSelection();

            var onLoadHandler = getOnLoadHandler();
            if (onLoadHandler != null) {
                onLoadHandler.accept(Boolean.TRUE);
            }
        });
    }

    /**
     * Restore selection.
     */
    @FromAnyThread
    private void restoreSelection() {
        EXECUTOR_MANAGER.addFxTask(() -> {

            var selectedElements = getSelectedElements();
            var stamp = selectedElements.writeLock();
            try {

                var selectionModel = getSelectionModel();

                selectedElements.stream()
                        .map(resourceElement -> findItemForValue(getRoot(), resourceElement))
                        .filter(Objects::nonNull)
                        .forEach(selectionModel::select);

                selectedElements.clear();

            } finally {
                selectedElements.writeUnlock(stamp);
            }
        });
    }

    /**
     * Fill the node.
     */
    @FxThread
    private void fill(@NotNull TreeItem<ResourceElement> treeItem) {

        var element = treeItem.getValue();
        var extensionFilter = getExtensionFilter();

        if (!element.hasChildren(extensionFilter, isOnlyFolders())) {
            return;
        }

        var items = treeItem.getChildren();

        if (isLazyMode()) {
            items.add(new TreeItem<>(LoadingResourceElement.getInstance()));
        } else {

            var children = element.getChildren(extensionFilter, isOnlyFolders());
            if (children == null) {
                return;
            }

            children.sort(NAME_COMPARATOR);
            children.forEach(child -> items.add(new TreeItem<>(child)));

            items.forEach(this::fill);
        }
    }

    /**
     * Handle a created file.
     *
     * @param file the created file.
     */
    @FxThread
    public void notifyCreated(@NotNull Path file) {

        var editorConfig = EditorConfig.getInstance();
        var currentAsset = editorConfig.getCurrentAsset();
        var folder = file.getParent();

        if (!folder.startsWith(currentAsset)) {
            return;
        }

        var fileElement = createFor(file);

        var fileItem = findItemForValue(getRoot(), fileElement);
        if (fileItem != null) {
            return;
        }

        var element = createFor(folder);
        var folderItem = findItemForValue(getRoot(), element);

        if (folderItem == null) {
            notifyCreated(folder);
            folderItem = findItemForValue(getRoot(), folder);
        }

        if (folderItem == null) {
            return;
        }

        var newItem = new TreeItem<ResourceElement>(createFor(file));

        fill(newItem);

        var children = folderItem.getChildren();
        children.add(newItem);

        FXCollections.sort(children, ITEM_COMPARATOR);
    }

    /**
     * Handle the removed file.
     *
     * @param file the removed file.
     */
    @FxThread
    public void notifyDeleted(@NotNull Path file) {

        var element = createFor(file);
        var treeItem = findItemForValue(getRoot(), element);
        if (treeItem == null) {
            return;
        }

        var parent = treeItem.getParent();
        if (parent == null) {
            return;
        }

        var children = parent.getChildren();
        children.remove(treeItem);
    }

    /**
     * Handle the moved file.
     *
     * @param prevFile the prev version.
     * @param newFile  the new version.
     */
    @FxThread
    public void notifyMoved(@NotNull Path prevFile, @NotNull Path newFile) {

        var prevElement = createFor(prevFile);
        var prevItem = findItemForValue(getRoot(), prevElement);
        if (prevItem == null) {
            return;
        }

        var newParentElement = createFor(newFile.getParent());
        var newParentItem = findItemForValue(getRoot(), newParentElement);
        if (newParentItem == null) {
            return;
        }

        var prevParentItem = prevItem.getParent();
        var prevParentChildren = prevParentItem.getChildren();
        prevParentChildren.remove(prevItem);

        prevItem.setValue(createFor(newFile));

        var children = UiUtils.getAllItems(prevItem);
        children.fastRemove(prevItem);

        fillChildren(prevFile, newFile, children);

        var newParentChildren = newParentItem.getChildren();
        newParentChildren.add(prevItem);

        FXCollections.sort(newParentChildren, ITEM_COMPARATOR);
    }

    @FxThread
    private void fillChildren(
            @NotNull Path prevFile,
            @NotNull Path newFile,
            @NotNull Array<TreeItem<ResourceElement>> children
    ) {

        for (var child : children) {

            var resourceElement = child.getValue();
            var file = resourceElement.getFile();
            var relativeFile = file.subpath(prevFile.getNameCount(), file.getNameCount());
            var resultFile = newFile.resolve(relativeFile);

            child.setValue(createFor(resultFile));
        }
    }

    /**
     * Handle a renamed file.
     *
     * @param prevFile the prev version.
     * @param newFile  the new version.
     */
    @FxThread
    public void notifyRenamed(@NotNull Path prevFile, @NotNull Path newFile) {

        var prevElement = createFor(prevFile);
        var prevItem = findItemForValue(getRoot(), prevElement);
        if (prevItem == null) {
            return;
        }

        prevItem.setValue(createFor(newFile));

        var children = UiUtils.getAllItems(prevItem);
        children.fastRemove(prevItem);

        fillChildren(prevFile, newFile, children);
    }

    /**
     * Handle hotkeys.
     */
    @FxThread
    private void processKey(@NotNull KeyEvent event) {

        if (isReadOnly()) {
            return;
        }

        var editorConfig = EditorConfig.getInstance();
        var currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) {
            return;
        }

        updateSelectedElements();

        var selectedElements = getSelectedElements();
        if (selectedElements.isEmpty()) {
            return;
        }

        var firstElement = selectedElements.first();
        if (firstElement instanceof LoadingResourceElement) {
            return;
        }

        boolean onlyFiles = true;
        boolean onlyFolders = true;
        boolean selectedAsset = false;

        for (var element : selectedElements.array()) {

            if (element == null) {
                break;
            }

            if (element instanceof FileResourceElement) {
                onlyFolders = false;
            } else if (element instanceof FolderResourceElement) {
                onlyFiles = false;
            }

            if (Objects.equals(currentAsset, element.getFile())) {
                selectedAsset = true;
            }
        }

        var actionTester = getActionTester();
        var keyCode = event.getCode();
        var controlDown = event.isControlDown();

        if (!currentAsset.equals(firstElement.getFile())) {

            if (controlDown) {

                if (keyCode == KeyCode.C && actionTester.test(CopyFileAction.class) &&
                        !selectedAsset && (onlyFiles || selectedElements.size() == 1)) {

                    CopyFileAction.applyFor(selectedElements);

                } else if (keyCode == KeyCode.X && actionTester.test(CutFileAction.class) &&
                        !selectedAsset && (onlyFiles || selectedElements.size() == 1)) {

                    CutFileAction.applyFor(selectedElements);
                }

            } else if (keyCode == KeyCode.DELETE && actionTester.test(DeleteFileAction.class) &&
                    !selectedAsset && (onlyFiles || selectedElements.size() == 1)) {

                DeleteFileAction.applyFor(selectedElements);
            }
        }

        if (controlDown && keyCode == KeyCode.V && hasFileInClipboard() &&
                actionTester.test(PasteFileAction.class)) {

            PasteFileAction.applyFor(firstElement);
        }
    }

    /**
     * Get the open resource function.
     *
     * @return the open resource function.
     */
    @FromAnyThread
    protected @Nullable Consumer<ResourceElement> getOpenFunction() {
        return openFunction;
    }

    /**
     * Cleanup the tree.
     */
    @FxThread
    private void cleanup(@NotNull TreeItem<ResourceElement> treeItem) {

        var element = treeItem.getValue();
        if (element instanceof FileResourceElement || element instanceof LoadingResourceElement) {
            return;
        }

        var children = treeItem.getChildren();

        for (int i = children.size() - 1; i >= 0; i--) {
            cleanup(children.get(i));
        }

        if (children.isEmpty() && treeItem.getParent() != null) {
            var parent = treeItem.getParent();
            var parentChildren = parent.getChildren();
            parentChildren.remove(treeItem);
        }
    }

    /**
     * Expand tree to the file.
     *
     * @param treeItem   the tree item
     * @param needSelect the need select
     */
    @FxThread
    public void expandTo(@NotNull TreeItem<ResourceElement> treeItem, boolean needSelect) {

        var parent = treeItem;

        while (parent != null) {
            parent.setExpanded(true);
            parent = parent.getParent();
        }

        if (needSelect) {
            scrollToAndSelect(treeItem);
        }
    }

    /**
     * Mark the element as expanded.
     *
     * @param file the file
     */
    @FxThread
    public void markExpand(@NotNull Path file) {

        var element = createFor(file);
        var treeItem = findItemForValue(getRoot(), element);
        if (treeItem == null) {
            return;
        }

        treeItem.setExpanded(true);
    }

    /**
     * Set true if need to show only folders.
     *
     * @param onlyFolders true if need to show only folders.
     */
    @FromAnyThread
    public void setOnlyFolders(boolean onlyFolders) {
        this.onlyFolders = onlyFolders;
    }

    /**
     * Return true if need to show only folders.
     *
     * @return true if need to show only folders.
     */
    @FromAnyThread
    public boolean isOnlyFolders() {
        return onlyFolders;
    }

    /**
     * Expand the file in the tree.
     *
     * @param file       the file.
     * @param needSelect the need select.
     */
    @FxThread
    public void expandTo(@NotNull Path file, boolean needSelect) {

        if (isLazyMode()) {

            var targetItem = findItemForValue(getRoot(), file);
            if (targetItem == null) {

                TreeItem<ResourceElement> parentItem = null;

                var parent = file.getParent();

                while (parent != null) {
                    parentItem = findItemForValue(getRoot(), parent);
                    if (parentItem != null) {
                        break;
                    }

                    parent = parent.getParent();
                }

                if (parentItem == null) {
                    parentItem = getRoot();
                }

                var toLoad = parentItem;
                EXECUTOR_MANAGER.addBackgroundTask(() -> lazyLoadChildren(toLoad, item -> expandTo(file, needSelect)));
                return;
            }

            var children = targetItem.getChildren();
            if (children.size() == 1 && children.get(0).getValue() == LoadingResourceElement.getInstance()) {
                EXECUTOR_MANAGER.addBackgroundTask(() -> lazyLoadChildren(targetItem, item -> expandTo(file, needSelect)));
                return;
            }
        }

        var element = createFor(file);
        var treeItem = findItemForValue(getRoot(), element);
        if (treeItem == null) {
            return;
        }

        var parent = treeItem;

        while (parent != null) {
            parent.setExpanded(true);
            parent = parent.getParent();
        }

        if (needSelect) {
            scrollToAndSelect(treeItem);
        }
    }

    @FromAnyThread
    private void scrollToAndSelect(@NotNull TreeItem<ResourceElement> treeItem) {
        EXECUTOR_MANAGER.addFxTask(() -> {
            var selectionModel = getSelectionModel();
            selectionModel.clearSelection();
            selectionModel.select(treeItem);
            scrollTo(getRow(treeItem));
        });
    }
}
