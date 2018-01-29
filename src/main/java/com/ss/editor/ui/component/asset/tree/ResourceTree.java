package com.ss.editor.ui.component.asset.tree;

import static com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory.createFor;
import static com.ss.editor.ui.util.UiUtils.findItemForValue;
import static com.ss.editor.ui.util.UiUtils.hasFileInClipboard;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.FXConstants;
import com.ss.editor.ui.component.asset.tree.context.menu.action.*;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.AssetTreeMultiContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.AssetTreeSingleContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.resource.*;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.function.IntObjectConsumer;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayComparator;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.array.ConcurrentArray;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Objects;
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
    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The resource elements comparator.
     */
    @NotNull
    private static final ArrayComparator<ResourceElement> COMPARATOR = ResourceElement::compareTo;

    /**
     * The name comparator.
     */
    @NotNull
    private static final ArrayComparator<ResourceElement> NAME_COMPARATOR = (first, second) -> {

        final int firstLevel = getLevel(first);
        final int secondLevel = getLevel(second);

        if (firstLevel != secondLevel) {
            return firstLevel - secondLevel;
        }

        final Path firstFile = notNull(first).getFile();
        final Path firstFileFileName = firstFile.getFileName();
        final String firstName = firstFileFileName == null ? firstFile.toString() : firstFileFileName.toString();

        final Path secondFile = notNull(second).getFile();
        final Path secondFileName = secondFile.getFileName();
        final String secondName = secondFileName == null ? secondFile.toString() : secondFileName.toString();

        return StringUtils.compareIgnoreCase(firstName, secondName);
    };

    /**
     * The tree items comparator.
     */
    @NotNull
    private static final ArrayComparator<TreeItem<ResourceElement>> ITEM_COMPARATOR = (first, second) -> {
        final ResourceElement firstElement = notNull(first).getValue();
        final ResourceElement secondElement = notNull(second).getValue();
        return NAME_COMPARATOR.compare(firstElement, secondElement);
    };

    /**
     * The context menu filler registry.
     */
    @NotNull
    private static final AssetTreeContextMenuFillerRegistry CONTEXT_MENU_FILLER_REGISTRY = AssetTreeContextMenuFillerRegistry.getInstance();

    @FromAnyThread
    private static int getLevel(@Nullable final ResourceElement element) {
        if (element instanceof FolderResourceElement) return 1;
        return 2;
    }

    /**
     * The default open function.
     */
    @NotNull
    private static final Consumer<ResourceElement> DEFAULT_OPEN_FUNCTION = element -> {
        final OpenFileAction action = new OpenFileAction(element);
        final EventHandler<ActionEvent> onAction = action.getOnAction();
        onAction.handle(null);
    };

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

    public ResourceTree(final boolean readOnly) {
        this(DEFAULT_OPEN_FUNCTION, readOnly);
    }

    public ResourceTree(@Nullable final Consumer<ResourceElement> openFunction, final boolean readOnly) {
        this.openFunction = openFunction;
        this.readOnly = readOnly;
        this.expandedElements = ArrayFactory.newConcurrentAtomicARSWLockArray(ResourceElement.class);
        this.selectedElements = ArrayFactory.newConcurrentAtomicARSWLockArray(ResourceElement.class);
        this.extensionFilter = ArrayFactory.newArray(String.class, 0);
        this.actionTester = actionClass -> true;

        expandedItemCountProperty()
                .addListener((observable, oldValue, newValue) -> processChangedExpands(newValue));

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setFixedCellSize(FXConstants.RESOURCE_TREE_CELL_HEIGHT);
        setCellFactory(param -> new ResourceTreeCell());
        setOnKeyPressed(this::processKey);
        setShowRoot(true);
        setContextMenu(new ContextMenu());
        setFocusTraversable(true);
    }

    /**
     * @param lazyMode true if need to use lazy mode.
     */
    @FromAnyThread
    public void setLazyMode(final boolean lazyMode) {
        this.lazyMode = lazyMode;
    }

    /**
     * @return true if need to use lazy mode.
     */
    @FromAnyThread
    private boolean isLazyMode() {
        return lazyMode;
    }

    /**
     * @param needCleanup true of need to cleanup this tree.
     */
    @FromAnyThread
    public void setNeedCleanup(final boolean needCleanup) {
        this.needCleanup = needCleanup;
    }

    /**
     * @return true of need to cleanup this tree.
     */
    @FromAnyThread
    private boolean isNeedCleanup() {
        return needCleanup;
    }

    /**
     * Handle changed count of expanded elements.
     */
    @FxThread
    private void processChangedExpands(@NotNull final Number newValue) {

        if (isLazyMode()) {
            EXECUTOR_MANAGER.addFxTask(this::lazyLoadChildren);
        }

        final IntObjectConsumer<ResourceTree> expandHandler = getExpandHandler();
        if (expandHandler == null) {
            return;
        }

        expandHandler.accept(newValue.intValue(), this);
    }

    /**
     * Start the process of loading children of the tree item in the background.
     */
    @FxThread
    private void lazyLoadChildren() {

        final Array<TreeItem<ResourceElement>> expanded = ArrayFactory.newArray(TreeItem.class);
        final Array<TreeItem<ResourceElement>> allItems = UiUtils.getAllItems(getRoot());
        allItems.stream().filter(TreeItem::isExpanded)
                .filter(treeItem -> !treeItem.isLeaf())
                .filter(item -> item.getChildren().size() == 1)
                .filter(item -> item.getChildren().get(0).getValue() == LoadingResourceElement.getInstance())
                .forEach(expanded::add);

        for (final TreeItem<ResourceElement> treeItem : expanded) {
            EXECUTOR_MANAGER.addBackgroundTask(() -> lazyLoadChildren(treeItem, null));
        }
    }

    /**
     * Load children of the tree item in the background.
     *
     * @param treeItem the tree item.
     */
    @BackgroundThread
    private void lazyLoadChildren(@NotNull final TreeItem<ResourceElement> treeItem,
                                  @Nullable final Consumer<TreeItem<ResourceElement>> callback) {

        final ResourceElement element = treeItem.getValue();
        final Array<ResourceElement> children = element.getChildren(extensionFilter, isOnlyFolders());
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
    private void lazyLoadChildren(@NotNull final TreeItem<ResourceElement> treeItem,
                                  @NotNull final Array<ResourceElement> children,
                                  @Nullable final Consumer<TreeItem<ResourceElement>> callback) {

        final ObservableList<TreeItem<ResourceElement>> items = treeItem.getChildren();
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
     * Sets expand handler.
     *
     * @param expandHandler the handler for listening expand items.
     */
    @FromAnyThread
    public void setExpandHandler(@Nullable final IntObjectConsumer<ResourceTree> expandHandler) {
        this.expandHandler = expandHandler;
    }

    /**
     * Sets action tester.
     *
     * @param actionTester the action tester.
     */
    @FromAnyThread
    public void setActionTester(@NotNull final Predicate<Class<?>> actionTester) {
        this.actionTester = actionTester;
    }

    /**
     * @return the handler for listening expand items.
     */
    @FromAnyThread
    private @Nullable IntObjectConsumer<ResourceTree> getExpandHandler() {
        return expandHandler;
    }

    /**
     * Sets extension filter.
     *
     * @param extensionFilter the list of filtered extensions.
     */
    @FromAnyThread
    public void setExtensionFilter(@NotNull final Array<String> extensionFilter) {
        this.extensionFilter = extensionFilter;
    }

    /**
     * @return the list of filtered extensions.
     */
    @FromAnyThread
    private @NotNull Array<String> getExtensionFilter() {
        return extensionFilter;
    }

    /**
     * Sets on load handler.
     *
     * @param onLoadHandler the post loading handler.
     */
    @FromAnyThread
    public void setOnLoadHandler(@Nullable final Consumer<Boolean> onLoadHandler) {
        this.onLoadHandler = onLoadHandler;
    }

    /**
     * @return the post loading handler.
     */
    @FromAnyThread
    private @Nullable Consumer<Boolean> getOnLoadHandler() {
        return onLoadHandler;
    }

    /**
     * @return the flag of read only mode.
     */
    @FromAnyThread
    private boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @return the action tester.
     */
    @FromAnyThread
    private @NotNull Predicate<Class<?>> getActionTester() {
        return actionTester;
    }

    /**
     * Gets context menu.
     *
     * @param element the element
     * @return the context menu for the element.
     */
    @FxThread
    protected @Nullable ContextMenu getContextMenu(@NotNull final ResourceElement element) {
        if (isReadOnly()) return null;

        final ContextMenu contextMenu = new ContextMenu();
        final ObservableList<MenuItem> items = contextMenu.getItems();

        final Predicate<Class<?>> actionTester = getActionTester();

        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = getSelectionModel();
        final ObservableList<TreeItem<ResourceElement>> selectedItems = selectionModel.getSelectedItems();

        if (selectedItems.size() == 1) {
            final Array<AssetTreeSingleContextMenuFiller> fillers = CONTEXT_MENU_FILLER_REGISTRY.getSingleFillers();
            for (final AssetTreeSingleContextMenuFiller filler : fillers) {
                filler.fill(element, items, actionTester);
            }
        }

        if (selectedItems.size() >= 1) {
            updateSelectedElements();

            final ConcurrentArray<ResourceElement> selectedElements = getSelectedElements();

            final long stamp = selectedElements.readLock();
            try {

                final Array<AssetTreeMultiContextMenuFiller> fillers = CONTEXT_MENU_FILLER_REGISTRY.getMultiFillers();
                for (final AssetTreeMultiContextMenuFiller filler : fillers) {
                    filler.fill(selectedElements, items, actionTester);
                }

            } finally {
                selectedElements.readUnlock(stamp);
            }
        }

        if (items.isEmpty()) return null;

        return contextMenu;
    }

    /**
     * Fill the tree using the root folder.
     *
     * @param rootFolder the root folder.
     */
    @FxThread
    public void fill(@NotNull final Path rootFolder) {

        final Consumer<Boolean> onLoadHandler = getOnLoadHandler();
        if (onLoadHandler != null) {
            onLoadHandler.accept(Boolean.FALSE);
        }

        final TreeItem<ResourceElement> currentRoot = getRoot();
        if (currentRoot != null) {
            setRoot(null);
        }

        showLoading();

        EXECUTOR_MANAGER.addBackgroundTask(() -> startBackgroundFill(rootFolder));
    }

    /**
     * Fill the tree using the list of root folders.
     *
     * @param rootFolders the list of root folder.
     */
    @FxThread
    public void fill(@NotNull final Array<Path> rootFolders) {

        final Consumer<Boolean> onLoadHandler = getOnLoadHandler();
        if (onLoadHandler != null) {
            onLoadHandler.accept(Boolean.FALSE);
        }

        final TreeItem<ResourceElement> currentRoot = getRoot();
        if (currentRoot != null) {
            setRoot(null);
        }

        showLoading();

        EXECUTOR_MANAGER.addBackgroundTask(() -> startBackgroundFill(rootFolders));
    }

    /**
     * @return the list of expanded elements.
     */
    @FromAnyThread
    private @NotNull ConcurrentArray<ResourceElement> getExpandedElements() {
        return expandedElements;
    }

    /**
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

        final EditorConfig config = EditorConfig.getInstance();
        final Path currentAsset = config.getCurrentAsset();

        if (currentAsset == null) {
            setRoot(null);
            return;
        }

        final Consumer<Boolean> onLoadHandler = getOnLoadHandler();
        if (onLoadHandler != null) onLoadHandler.accept(Boolean.FALSE);

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

        final ConcurrentArray<ResourceElement> expandedElements = getExpandedElements();
        final long stamp = expandedElements.writeLock();
        try {

            expandedElements.clear();

            final Array<TreeItem<ResourceElement>> allItems = UiUtils.getAllItems(this);
            allItems.forEach(item -> {
                if (!item.isExpanded()) return;
                expandedElements.add(item.getValue());
            });

        } finally {
            expandedElements.writeUnlock(stamp);
        }
    }

    /**
     * Update the list of selected elements.
     */
    @FxThread
    private void updateSelectedElements() {

        final ConcurrentArray<ResourceElement> selectedElements = getSelectedElements();
        final long stamp = selectedElements.writeLock();
        try {

            selectedElements.clear();

            final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = getSelectionModel();
            final ObservableList<TreeItem<ResourceElement>> selectedItems = selectionModel.getSelectedItems();
            selectedItems.forEach(item -> selectedElements.add(item.getValue()));

        } finally {
            selectedElements.writeUnlock(stamp);
        }
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
    private void startBackgroundFill(@NotNull final Path path) {

        final ResourceElement rootElement = createFor(path);
        final TreeItem<ResourceElement> newRoot = new TreeItem<>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot);

        if (!isLazyMode() && isNeedCleanup()) {
            cleanup(newRoot);
        }

        EXECUTOR_MANAGER.addFxTask(() -> {
            setRoot(newRoot);

            final Consumer<Boolean> onLoadHandler = getOnLoadHandler();
            if (onLoadHandler != null) {
                onLoadHandler.accept(Boolean.TRUE);
            }
        });
    }

    /**
     * Start the background process of filling.
     */
    @BackgroundThread
    private void startBackgroundFill(@NotNull final Array<Path> paths) {

        final ResourceElement rootElement = new FoldersResourceElement(paths);
        final TreeItem<ResourceElement> newRoot = new TreeItem<>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot);

        if (!isLazyMode() && isNeedCleanup()) {
            cleanup(newRoot);
        }

        EXECUTOR_MANAGER.addFxTask(() -> {
            setRoot(newRoot);
            final Consumer<Boolean> onLoadHandler = getOnLoadHandler();
            if (onLoadHandler != null) {
                Platform.runLater(() -> onLoadHandler.accept(Boolean.TRUE));
            }
        });
    }

    /**
     * Start the background process of loading.
     */
    @BackgroundThread
    private void startBackgroundRefresh(@NotNull final Path assetFolder) {

        final ResourceElement rootElement = createFor(assetFolder);
        final TreeItem<ResourceElement> newRoot = new TreeItem<>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot);

        final ConcurrentArray<ResourceElement> expandedElements = getExpandedElements();
        final long stamp = expandedElements.writeLock();
        try {

            expandedElements.sort(COMPARATOR);
            expandedElements.forEach(element -> {

                final TreeItem<ResourceElement> item = findItemForValue(newRoot, element);
                if (item == null) return;

                item.setExpanded(true);
            });

            expandedElements.clear();

        } finally {
            expandedElements.writeUnlock(stamp);
        }

        EXECUTOR_MANAGER.addFxTask(() -> {
            setRoot(newRoot);
            restoreSelection();

            final Consumer<Boolean> onLoadHandler = getOnLoadHandler();
            if (onLoadHandler != null) onLoadHandler.accept(Boolean.TRUE);
        });
    }

    /**
     * Restore selection.
     */
    @FromAnyThread
    private void restoreSelection() {
        EXECUTOR_MANAGER.addFxTask(() -> {

            final ConcurrentArray<ResourceElement> selectedElements = getSelectedElements();
            final long stamp = selectedElements.writeLock();
            try {

                final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = getSelectionModel();

                selectedElements.forEach(element -> {
                    final TreeItem<ResourceElement> item = findItemForValue(getRoot(), element);
                    if (item == null) return;
                    selectionModel.select(item);
                });

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
    private void fill(@NotNull final TreeItem<ResourceElement> treeItem) {

        final ResourceElement element = treeItem.getValue();
        final Array<String> extensionFilter = getExtensionFilter();
        if (!element.hasChildren(extensionFilter, isOnlyFolders())) {
            return;
        }

        final ObservableList<TreeItem<ResourceElement>> items = treeItem.getChildren();

        if (isLazyMode()) {
            items.add(new TreeItem<>(LoadingResourceElement.getInstance()));
        } else {

            final Array<ResourceElement> children = element.getChildren(extensionFilter, isOnlyFolders());
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
    public void notifyCreated(@NotNull final Path file) {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        final Path folder = file.getParent();
        if (!folder.startsWith(currentAsset)) return;

        final ResourceElement element = createFor(folder);

        TreeItem<ResourceElement> folderItem = findItemForValue(getRoot(), element);

        if (folderItem == null) {
            notifyCreated(folder);
            folderItem = findItemForValue(getRoot(), folder);
        }

        if (folderItem == null) return;

        final TreeItem<ResourceElement> newItem = new TreeItem<>(createFor(file));

        fill(newItem);

        final ObservableList<TreeItem<ResourceElement>> children = folderItem.getChildren();
        children.add(newItem);

        FXCollections.sort(children, ITEM_COMPARATOR);
    }

    /**
     * Handle a removed file.
     *
     * @param file the file
     */
    @FxThread
    public void notifyDeleted(@NotNull final Path file) {

        final ResourceElement element = createFor(file);
        final TreeItem<ResourceElement> treeItem = findItemForValue(getRoot(), element);
        if (treeItem == null) return;

        final TreeItem<ResourceElement> parent = treeItem.getParent();
        if (parent == null) return;

        final ObservableList<TreeItem<ResourceElement>> children = parent.getChildren();
        children.remove(treeItem);
    }

    /**
     * Handle a moved file.
     *
     * @param prevFile the prev version.
     * @param newFile  the new version.
     */
    @FxThread
    public void notifyMoved(@NotNull final Path prevFile, @NotNull final Path newFile) {

        final ResourceElement prevElement = createFor(prevFile);
        final TreeItem<ResourceElement> prevItem = findItemForValue(getRoot(), prevElement);
        if (prevItem == null) return;

        final ResourceElement newParentElement = createFor(newFile.getParent());
        final TreeItem<ResourceElement> newParentItem = findItemForValue(getRoot(), newParentElement);
        if (newParentItem == null) return;

        final TreeItem<ResourceElement> prevParentItem = prevItem.getParent();
        final ObservableList<TreeItem<ResourceElement>> prevParentChildren = prevParentItem.getChildren();
        prevParentChildren.remove(prevItem);

        prevItem.setValue(createFor(newFile));

        final Array<TreeItem<ResourceElement>> children = UiUtils.getAllItems(prevItem);
        children.fastRemove(prevItem);

        fillChildren(prevFile, newFile, children);

        final ObservableList<TreeItem<ResourceElement>> newParentChildren = newParentItem.getChildren();
        newParentChildren.add(prevItem);

        FXCollections.sort(newParentChildren, ITEM_COMPARATOR);
    }

    @FxThread
    private void fillChildren(@NotNull final Path prevFile, @NotNull final Path newFile,
                              @NotNull final Array<TreeItem<ResourceElement>> children) {
        for (final TreeItem<ResourceElement> child : children) {

            final ResourceElement resourceElement = child.getValue();
            final Path file = resourceElement.getFile();
            final Path relativeFile = file.subpath(prevFile.getNameCount(), file.getNameCount());
            final Path resultFile = newFile.resolve(relativeFile);

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
    public void notifyRenamed(@NotNull final Path prevFile, @NotNull final Path newFile) {

        final ResourceElement prevElement = createFor(prevFile);
        final TreeItem<ResourceElement> prevItem = findItemForValue(getRoot(), prevElement);
        if (prevItem == null) return;

        prevItem.setValue(createFor(newFile));

        final Array<TreeItem<ResourceElement>> children = UiUtils.getAllItems(prevItem);
        children.fastRemove(prevItem);

        fillChildren(prevFile, newFile, children);
    }

    /**
     * Handle hotkeys.
     */
    @FxThread
    private void processKey(@NotNull final KeyEvent event) {
        if (isReadOnly()) return;

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) return;

        updateSelectedElements();

        final ConcurrentArray<ResourceElement> selectedElements = getSelectedElements();
        if (selectedElements.isEmpty()) return;

        final ResourceElement firstElement = selectedElements.first();
        if (firstElement instanceof LoadingResourceElement) return;

        boolean onlyFiles = true;
        boolean onlyFolders = true;
        boolean selectedAsset = false;

        for (final ResourceElement element : selectedElements.array()) {
            if (element == null) break;

            if (element instanceof FileResourceElement) {
                onlyFolders = false;
            } else if (element instanceof FolderResourceElement) {
                onlyFiles = false;
            }

            if (Objects.equals(currentAsset, element.getFile())) {
                selectedAsset = true;
            }
        }

        final Predicate<Class<?>> actionTester = getActionTester();
        final KeyCode keyCode = event.getCode();
        final boolean controlDown = event.isControlDown();

        if (!currentAsset.equals(firstElement.getFile())) {
            if (controlDown && keyCode == KeyCode.C && actionTester.test(CopyFileAction.class) && !selectedAsset &&
                    (onlyFiles || selectedElements.size() == 1)) {

                final CopyFileAction action = new CopyFileAction(selectedElements);
                final EventHandler<ActionEvent> onAction = action.getOnAction();
                onAction.handle(null);

            } else if (controlDown && keyCode == KeyCode.X && actionTester.test(CutFileAction.class) && !selectedAsset &&
                    (onlyFiles || selectedElements.size() == 1)) {

                final CutFileAction action = new CutFileAction(selectedElements);
                final EventHandler<ActionEvent> onAction = action.getOnAction();
                onAction.handle(null);

            } else if (keyCode == KeyCode.DELETE && actionTester.test(DeleteFileAction.class) && !selectedAsset &&
                    (onlyFiles || selectedElements.size() == 1)) {

                final DeleteFileAction action = new DeleteFileAction(selectedElements);
                final EventHandler<ActionEvent> onAction = action.getOnAction();
                onAction.handle(null);
            }
        }

        if (controlDown && keyCode == KeyCode.V && hasFileInClipboard() && actionTester.test(PasteFileAction.class)) {
            final PasteFileAction action = new PasteFileAction(firstElement);
            final EventHandler<ActionEvent> onAction = action.getOnAction();
            onAction.handle(null);
        }
    }

    /**
     * Gets open function.
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
    private void cleanup(@NotNull final TreeItem<ResourceElement> treeItem) {

        final ResourceElement element = treeItem.getValue();
        if (element instanceof FileResourceElement || element instanceof LoadingResourceElement) return;

        final ObservableList<TreeItem<ResourceElement>> children = treeItem.getChildren();

        for (int i = children.size() - 1; i >= 0; i--) {
            cleanup(children.get(i));
        }

        if (children.isEmpty() && treeItem.getParent() != null) {
            final TreeItem<ResourceElement> parent = treeItem.getParent();
            final ObservableList<TreeItem<ResourceElement>> parentChildren = parent.getChildren();
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
    public void expandTo(@NotNull final TreeItem<ResourceElement> treeItem, final boolean needSelect) {

        TreeItem<ResourceElement> parent = treeItem;

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
    public void markExpand(@NotNull final Path file) {

        final ResourceElement element = createFor(file);
        final TreeItem<ResourceElement> treeItem = findItemForValue(getRoot(), element);
        if (treeItem == null) return;

        treeItem.setExpanded(true);
    }

    /**
     * Sets only folders.
     *
     * @param onlyFolders true if need to show only folders.
     */
    @FromAnyThread
    public void setOnlyFolders(final boolean onlyFolders) {
        this.onlyFolders = onlyFolders;
    }

    /**
     * @return true if need to show only folders.
     */
    @FromAnyThread
    public boolean isOnlyFolders() {
        return onlyFolders;
    }

    /**
     * Expand tree to the file.
     *
     * @param file       the file
     * @param needSelect the need select
     */
    @FxThread
    public void expandTo(@NotNull final Path file, final boolean needSelect) {

        if (isLazyMode()) {

            final TreeItem<ResourceElement> targetItem = findItemForValue(getRoot(), file);
            if (targetItem == null) {

                TreeItem<ResourceElement> parentItem = null;
                Path parent = file.getParent();

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

                final TreeItem<ResourceElement> toLoad = parentItem;
                EXECUTOR_MANAGER.addBackgroundTask(() -> lazyLoadChildren(toLoad, item -> expandTo(file, needSelect)));
                return;
            }

            final ObservableList<TreeItem<ResourceElement>> children = targetItem.getChildren();
            if (children.size() == 1 && children.get(0).getValue() == LoadingResourceElement.getInstance()) {
                EXECUTOR_MANAGER.addBackgroundTask(() -> lazyLoadChildren(targetItem, item -> expandTo(file, needSelect)));
                return;
            }
        }

        final ResourceElement element = createFor(file);
        final TreeItem<ResourceElement> treeItem = findItemForValue(getRoot(), element);
        if (treeItem == null) return;

        TreeItem<ResourceElement> parent = treeItem;

        while (parent != null) {
            parent.setExpanded(true);
            parent = parent.getParent();
        }

        if (needSelect) {
            scrollToAndSelect(treeItem);
        }
    }

    @FromAnyThread
    private void scrollToAndSelect(@NotNull final TreeItem<ResourceElement> treeItem) {
        EXECUTOR_MANAGER.addFxTask(() -> {
            final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = getSelectionModel();
            selectionModel.clearSelection();
            selectionModel.select(treeItem);
            scrollTo(getRow(treeItem));
        });
    }
}
