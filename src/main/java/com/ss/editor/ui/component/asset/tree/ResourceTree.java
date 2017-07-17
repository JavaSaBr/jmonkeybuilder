package com.ss.editor.ui.component.asset.tree;

import static com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory.createFor;
import static com.ss.editor.ui.util.UIUtils.findItemForValue;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.file.converter.FileConverterRegistry;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.asset.tree.context.menu.action.*;
import com.ss.editor.ui.component.asset.tree.resource.FileElement;
import com.ss.editor.ui.component.asset.tree.resource.FolderElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceLoadingElement;
import com.ss.editor.ui.util.UIUtils;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.function.IntObjectConsumer;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayComparator;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.array.ConcurrentArray;
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

    @NotNull
    private static final FileConverterRegistry FILE_CONVERTER_REGISTRY = FileConverterRegistry.getInstance();
    
    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    @NotNull
    private static final ArrayComparator<ResourceElement> COMPARATOR = ResourceElement::compareTo;
    
    @NotNull
    private static final ArrayComparator<ResourceElement> NAME_COMPARATOR = (first, second) -> {

        final int firstLevel = getLevel(first);
        final int secondLevel = getLevel(second);

        if (firstLevel != secondLevel) return firstLevel - secondLevel;

        final Path firstFile = notNull(first).getFile();
        final String firstName = firstFile.getFileName().toString();

        final Path secondFile = notNull(second).getFile();
        final String secondName = secondFile.getFileName().toString();

        return StringUtils.compareIgnoreCase(firstName, secondName);
    };

    @NotNull
    private static final ArrayComparator<TreeItem<ResourceElement>> ITEM_COMPARATOR = (first, second) -> {

        final ResourceElement firstElement = notNull(first).getValue();
        final ResourceElement secondElement = notNull(second).getValue();

        final int firstLevel = getLevel(firstElement);
        final int secondLevel = getLevel(secondElement);

        if (firstLevel != secondLevel) return firstLevel - secondLevel;

        return NAME_COMPARATOR.compare(firstElement, secondElement);
    };

    private static int getLevel(@Nullable final ResourceElement element) {
        if (element instanceof FolderElement) return 1;
        return 2;
    }

    @NotNull
    private static final Consumer<ResourceElement> DEFAULT_FUNCTION = element -> {
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
    @Nullable
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
     * Instantiates a new Resource tree.
     *
     * @param readOnly the read only
     */
    public ResourceTree(final boolean readOnly) {
        this(DEFAULT_FUNCTION, readOnly);
        this.extensionFilter = ArrayFactory.newArray(String.class, 0);
    }

    /**
     * Instantiates a new Resource tree.
     *
     * @param openFunction the open function
     * @param readOnly     the read only
     */
    public ResourceTree(@Nullable final Consumer<ResourceElement> openFunction, final boolean readOnly) {
        this.openFunction = openFunction;
        this.readOnly = readOnly;
        this.expandedElements = ArrayFactory.newConcurrentAtomicARSWLockArray(ResourceElement.class);
        this.selectedElements = ArrayFactory.newConcurrentAtomicARSWLockArray(ResourceElement.class);
        this.extensionFilter = ArrayFactory.newArray(String.class, 0);

        expandedItemCountProperty()
                .addListener((observable, oldValue, newValue) -> processChangedExpands(newValue));

        setCellFactory(param -> new ResourceTreeCell());
        setOnKeyPressed(this::processKey);
        setShowRoot(true);
        setContextMenu(new ContextMenu());
        setFocusTraversable(true);
    }

    /**
     * Handle changed count of expanded elements.
     */
    private void processChangedExpands(@NotNull final Number newValue) {
        if (expandHandler == null) return;
        expandHandler.accept(newValue.intValue(), this);
    }

    /**
     * Sets expand handler.
     *
     * @param expandHandler the handler for listening expand items.
     */
    public void setExpandHandler(@Nullable final IntObjectConsumer<ResourceTree> expandHandler) {
        this.expandHandler = expandHandler;
    }

    /**
     * Sets action tester.
     *
     * @param actionTester the action tester.
     */
    public void setActionTester(@Nullable final Predicate<Class<?>> actionTester) {
        this.actionTester = actionTester;
    }

    /**
     * @return the handler for listening expand items.
     */
    @Nullable
    private IntObjectConsumer<ResourceTree> getExpandHandler() {
        return expandHandler;
    }

    /**
     * Sets extension filter.
     *
     * @param extensionFilter the list of filtered extensions.
     */
    public void setExtensionFilter(@NotNull final Array<String> extensionFilter) {
        this.extensionFilter = extensionFilter;
    }

    /**
     * @return the list of filtered extensions.
     */
    @NotNull
    private Array<String> getExtensionFilter() {
        return extensionFilter;
    }

    /**
     * Sets on load handler.
     *
     * @param onLoadHandler the post loading handler.
     */
    public void setOnLoadHandler(@Nullable final Consumer<Boolean> onLoadHandler) {
        this.onLoadHandler = onLoadHandler;
    }

    /**
     * @return the post loading handler.
     */
    @Nullable
    private Consumer<Boolean> getOnLoadHandler() {
        return onLoadHandler;
    }

    /**
     * @return the flag of read only mode.
     */
    private boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @return the action tester.
     */
    @Nullable
    private Predicate<Class<?>> getActionTester() {
        return actionTester;
    }

    /**
     * Gets context menu.
     *
     * @param element the element
     * @return the context menu for the element.
     */
    protected ContextMenu getContextMenu(@NotNull final ResourceElement element) {
        if (isReadOnly()) return null;

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();

        final ContextMenu contextMenu = new ContextMenu();
        final ObservableList<MenuItem> items = contextMenu.getItems();

        final Predicate<Class<?>> actionTester = getActionTester();

        final Path file = element.getFile();

        if(actionTester == null || actionTester.test(NewFileAction.class)) {
            items.add(new NewFileAction(element));
        }

        if (element instanceof FileElement) {

            if(actionTester == null || actionTester.test(OpenFileAction.class)) {
                items.add(new OpenFileAction(element));
            }

            if(actionTester == null || actionTester.test(OpenFileByExternalEditorAction.class)) {
                items.add(new OpenFileByExternalEditorAction(element));
            }

            if(actionTester == null || actionTester.test(OpenWithFileAction.class)) {
                items.add(new OpenWithFileAction(element));
            }

            if(actionTester == null || actionTester.test(ConvertFileAction.class)) {

                final Array<FileConverterDescription> descriptions = FILE_CONVERTER_REGISTRY.getDescriptions(file);

                if (!descriptions.isEmpty()) {
                    items.add(new ConvertFileAction(element, descriptions));
                }
            }
        }

        if (EditorUtil.hasFileInClipboard()) items.add(new PasteFileAction(element));

        if (!Objects.equals(currentAsset, file)) {

            if(actionTester == null || actionTester.test(CopyFileAction.class)) {
                items.add(new CopyFileAction(element));
            }

            if(actionTester == null || actionTester.test(CutFileAction.class)) {
                items.add(new CutFileAction(element));
            }

            if(actionTester == null || actionTester.test(RenameFileAction.class)) {
                items.add(new RenameFileAction(element));
            }

            if(actionTester == null || actionTester.test(DeleteFileAction.class)) {
                items.add(new DeleteFileAction(element));
            }
        }

        if (items.isEmpty()) return null;

        return contextMenu;
    }

    /**
     * Fill the tree using the asset folder.
     *
     * @param assetFolder the asset folder.
     */
    public void fill(@NotNull final Path assetFolder) {

        final Consumer<Boolean> onLoadHandler = getOnLoadHandler();
        if (onLoadHandler != null) onLoadHandler.accept(Boolean.FALSE);

        final TreeItem<ResourceElement> currentRoot = getRoot();
        if (currentRoot != null) setRoot(null);

        showLoading();

        EXECUTOR_MANAGER.addBackgroundTask(() -> startBackgroundFill(assetFolder));
    }

    /**
     * @return the list of expanded elements.
     */
    @NotNull
    private ConcurrentArray<ResourceElement> getExpandedElements() {
        return expandedElements;
    }

    /**
     * @return the list of selected elements.
     */
    @NotNull
    private ConcurrentArray<ResourceElement> getSelectedElements() {
        return selectedElements;
    }

    /**
     * Refresh this tree.
     */
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
    private void updateExpandedElements() {

        final ConcurrentArray<ResourceElement> expandedElements = getExpandedElements();
        final long stamp = expandedElements.writeLock();
        try {

            expandedElements.clear();

            final Array<TreeItem<ResourceElement>> allItems = UIUtils.getAllItems(this);
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
    private void showLoading() {
        setRoot(new TreeItem<>(ResourceLoadingElement.getInstance()));
    }

    /**
     * Start the background process of filling.
     */
    private void startBackgroundFill(@NotNull final Path assetFolder) {

        final ResourceElement rootElement = createFor(assetFolder);
        final TreeItem<ResourceElement> newRoot = new TreeItem<>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot);

        final Array<String> extensionFilter = getExtensionFilter();
        if (!extensionFilter.isEmpty() && !isOnlyFolders()) cleanup(newRoot);

        EXECUTOR_MANAGER.addFXTask(() -> {
            setRoot(newRoot);

            final Consumer<Boolean> onLoadHandler = getOnLoadHandler();
            if (onLoadHandler != null) onLoadHandler.accept(Boolean.TRUE);
        });
    }

    /**
     * Start the background process of loading.
     */
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

        EXECUTOR_MANAGER.addFXTask(() -> {
            setRoot(newRoot);
            restoreSelection();

            final Consumer<Boolean> onLoadHandler = getOnLoadHandler();
            if (onLoadHandler != null) onLoadHandler.accept(Boolean.TRUE);
        });
    }

    /**
     * Restore selection.
     */
    private void restoreSelection() {
        EXECUTOR_MANAGER.addFXTask(() -> {

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
    private void fill(@NotNull final TreeItem<ResourceElement> treeItem) {

        final ResourceElement element = treeItem.getValue();
        final Array<String> extensionFilter = getExtensionFilter();
        if (!element.hasChildren(extensionFilter, isOnlyFolders())) return;

        final ObservableList<TreeItem<ResourceElement>> items = treeItem.getChildren();

        final Array<ResourceElement> children = element.getChildren(extensionFilter, isOnlyFolders());
        children.sort(NAME_COMPARATOR);
        children.forEach(child -> items.add(new TreeItem<>(child)));

        items.forEach(this::fill);
    }

    /**
     * Handle a created file.
     *
     * @param file the created file.
     */
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

        final Array<TreeItem<ResourceElement>> children = ArrayFactory.newArray(TreeItem.class);

        UIUtils.getAllItems(children, prevItem);

        children.fastRemove(prevItem);
        children.forEach(child -> {

            final ResourceElement resourceElement = child.getValue();
            final Path file = resourceElement.getFile();
            final Path relativeFile = file.subpath(prevFile.getNameCount(), file.getNameCount());
            final Path resultFile = newFile.resolve(relativeFile);

            child.setValue(createFor(resultFile));
        });

        final ObservableList<TreeItem<ResourceElement>> newParentChildren = newParentItem.getChildren();
        newParentChildren.add(prevItem);

        FXCollections.sort(newParentChildren, ITEM_COMPARATOR);
    }

    /**
     * Handle a renamed file.
     *
     * @param prevFile the prev version.
     * @param newFile  the new version.
     */
    public void notifyRenamed(@NotNull final Path prevFile, @NotNull final Path newFile) {

        final ResourceElement prevElement = createFor(prevFile);
        final TreeItem<ResourceElement> prevItem = findItemForValue(getRoot(), prevElement);
        if (prevItem == null) return;

        prevItem.setValue(createFor(newFile));

        final Array<TreeItem<ResourceElement>> children = ArrayFactory.newArray(TreeItem.class);

        UIUtils.getAllItems(children, prevItem);

        children.fastRemove(prevItem);
        children.forEach(child -> {

            final ResourceElement resourceElement = child.getValue();
            final Path file = resourceElement.getFile();
            final Path relativeFile = file.subpath(prevFile.getNameCount(), file.getNameCount());
            final Path resultFile = newFile.resolve(relativeFile);

            child.setValue(createFor(resultFile));
        });
    }

    /**
     * Handle pressing on hotkey.
     */
    private void processKey(@NotNull final KeyEvent event) {
        if (isReadOnly()) return;

        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = getSelectionModel();
        final TreeItem<ResourceElement> selectedItem = selectionModel.getSelectedItem();
        if (selectedItem == null) return;

        final ResourceElement item = selectedItem.getValue();
        if (item == null || item instanceof ResourceLoadingElement) return;

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) return;

        final KeyCode keyCode = event.getCode();

        if (event.isControlDown() && keyCode == KeyCode.C && !currentAsset.equals(item.getFile())) {

            final CopyFileAction action = new CopyFileAction(item);
            final EventHandler<ActionEvent> onAction = action.getOnAction();
            onAction.handle(null);

        } else if (event.isControlDown() && keyCode == KeyCode.X && !currentAsset.equals(item.getFile())) {

            final CutFileAction action = new CutFileAction(item);
            final EventHandler<ActionEvent> onAction = action.getOnAction();
            onAction.handle(null);

        } else if (event.isControlDown() && keyCode == KeyCode.V && EditorUtil.hasFileInClipboard()) {

            final PasteFileAction action = new PasteFileAction(item);
            final EventHandler<ActionEvent> onAction = action.getOnAction();
            onAction.handle(null);

        } else if (keyCode == KeyCode.DELETE && !currentAsset.equals(item.getFile())) {

            final DeleteFileAction action = new DeleteFileAction(item);
            final EventHandler<ActionEvent> onAction = action.getOnAction();
            onAction.handle(null);
        }
    }

    /**
     * Gets open function.
     *
     * @return the open resource function.
     */
    @Nullable
    Consumer<ResourceElement> getOpenFunction() {
        return openFunction;
    }

    /**
     * Cleanup the tree.
     */
    private void cleanup(@NotNull final TreeItem<ResourceElement> treeItem) {

        final ResourceElement element = treeItem.getValue();
        if (element instanceof FileElement) return;

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
    public void setOnlyFolders(final boolean onlyFolders) {
        this.onlyFolders = onlyFolders;
    }

    /**
     * @return true if need to show only folders.
     */
    private boolean isOnlyFolders() {
        return onlyFolders;
    }

    /**
     * Expand tree to the file.
     *
     * @param file       the file
     * @param needSelect the need select
     */
    public void expandTo(@NotNull final Path file, final boolean needSelect) {

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

    private void scrollToAndSelect(@NotNull final TreeItem<ResourceElement> treeItem) {
        EXECUTOR_MANAGER.addFXTask(() -> {
            final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = getSelectionModel();
            selectionModel.select(treeItem);
            scrollTo(getRow(treeItem));
        });
    }
}
