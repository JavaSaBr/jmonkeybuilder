package com.ss.editor.ui.component.asset.tree;

import static com.ss.editor.ui.component.asset.tree.ResourceTreeCell.CELL_FACTORY;
import static com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory.createFor;
import static com.ss.editor.ui.util.UIUtils.findItemForValue;

import com.ss.editor.config.EditorConfig;
import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.file.converter.FileConverterRegistry;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.asset.tree.context.menu.action.ConvertFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.CopyFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.CutFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.DeleteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.NewFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.OpenFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.OpenFileByExternalEditorAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.OpenWithFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.PasteFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.RenameFileAction;
import com.ss.editor.ui.component.asset.tree.resource.FileElement;
import com.ss.editor.ui.component.asset.tree.resource.FolderElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory;
import com.ss.editor.ui.component.asset.tree.resource.ResourceLoadingElement;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.UIUtils;
import com.ss.editor.util.EditorUtil;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import rlib.function.IntObjectConsumer;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayComparator;
import rlib.util.array.ArrayFactory;
import rlib.util.array.ConcurrentArray;

/**
 * THe implementation of a tree with resources of an asset folder.
 *
 * @author JavaSaBr.
 */
public class ResourceTree extends TreeView<ResourceElement> {

    private static final FileConverterRegistry FILE_CONVERTER_REGISTRY = FileConverterRegistry.getInstance();
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    private static final ArrayComparator<ResourceElement> COMPARATOR = ResourceElement::compareTo;
    private static final ArrayComparator<ResourceElement> NAME_COMPARATOR = (first, second) -> {

        final int firstLevel = getLevel(first);
        final int secondLevel = getLevel(second);

        if (firstLevel != secondLevel) return firstLevel - secondLevel;

        final Path firstFile = first.getFile();
        final String firstName = firstFile.getFileName().toString();

        final Path secondFile = second.getFile();
        final String secondName = secondFile.getFileName().toString();

        return StringUtils.compareIgnoreCase(firstName, secondName);
    };

    private static final ArrayComparator<TreeItem<ResourceElement>> ITEM_COMPARATOR = (first, second) -> {

        final ResourceElement firstElement = first.getValue();
        final ResourceElement secondElement = second.getValue();

        final int firstLevel = getLevel(firstElement);
        final int secondLevel = getLevel(secondElement);

        if (firstLevel != secondLevel) return firstLevel - secondLevel;

        return NAME_COMPARATOR.compare(firstElement, secondElement);
    };

    private static int getLevel(final ResourceElement element) {
        if (element instanceof FolderElement) return 1;
        return 2;
    }

    private static final Consumer<ResourceElement> DEFAULT_FUNCTION = element -> {
        final OpenFileAction action = new OpenFileAction(element);
        final EventHandler<ActionEvent> onAction = action.getOnAction();
        onAction.handle(null);
    };

    /**
     * The list of expanded elements.
     */
    private final ConcurrentArray<ResourceElement> expandedElements;

    /**
     * The list of selected elements.
     */
    private final ConcurrentArray<ResourceElement> selectedElements;

    /**
     * The open resource function.
     */
    private final Consumer<ResourceElement> openFunction;

    /**
     * The flag of read only mode.
     */
    private final boolean readOnly;

    /**
     * The list of filtered extensions.
     */
    @NotNull
    private Array<String> extensionFilter;

    /**
     * The post loading handler.
     */
    private Consumer<Boolean> onLoadHandler;

    /**
     * The handler for listening expand items.
     */
    private IntObjectConsumer<ResourceTree> expandHandler;

    public ResourceTree(final boolean readOnly) {
        this(DEFAULT_FUNCTION, readOnly);
        this.extensionFilter = ArrayFactory.newArray(String.class, 0);
    }

    public ResourceTree(final Consumer<ResourceElement> openFunction, final boolean readOnly) {
        this.openFunction = openFunction;
        this.readOnly = readOnly;
        this.expandedElements = ArrayFactory.newConcurrentAtomicARSWLockArray(ResourceElement.class);
        this.selectedElements = ArrayFactory.newConcurrentAtomicARSWLockArray(ResourceElement.class);
        this.extensionFilter = ArrayFactory.newArray(String.class, 0);

        FXUtils.addClassTo(this, CSSClasses.TRANSPARENT_TREE_VIEW);

        expandedItemCountProperty().addListener((observable, oldValue, newValue) -> processChangedExpands(newValue));

        setCellFactory(CELL_FACTORY);
        setOnKeyPressed(this::processKey);
        setShowRoot(true);
        setContextMenu(new ContextMenu());
        setFocusTraversable(true);
    }

    /**
     * Handle changed count of expanded elements.
     */
    private void processChangedExpands(final Number newValue) {
        if (expandHandler == null) return;
        expandHandler.accept(newValue.intValue(), this);
    }

    /**
     * @param expandHandler the handler for listening expand items.
     */
    public void setExpandHandler(final IntObjectConsumer<ResourceTree> expandHandler) {
        this.expandHandler = expandHandler;
    }

    /**
     * @return the handler for listening expand items.
     */
    private IntObjectConsumer<ResourceTree> getExpandHandler() {
        return expandHandler;
    }

    /**
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
     * @param onLoadHandler the post loading handler.
     */
    public void setOnLoadHandler(final Consumer<Boolean> onLoadHandler) {
        this.onLoadHandler = onLoadHandler;
    }

    /**
     * @return the post loading handler.
     */
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
     * @return the context menu for the element.
     */
    public ContextMenu getContextMenu(@NotNull final ResourceElement element) {
        if (isReadOnly()) return null;

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();

        final ContextMenu contextMenu = new ContextMenu();
        final ObservableList<MenuItem> items = contextMenu.getItems();

        final Path file = element.getFile();

        items.add(new NewFileAction(element));

        if (element instanceof FileElement) {

            items.add(new OpenFileAction(element));
            items.add(new OpenFileByExternalEditorAction(element));
            items.add(new OpenWithFileAction(element));

            final Array<FileConverterDescription> descriptions = FILE_CONVERTER_REGISTRY.getDescriptions(file);

            if (!descriptions.isEmpty()) {
                items.add(new ConvertFileAction(element, descriptions));
            }
        }

        if (EditorUtil.hasFileInClipboard()) items.add(new PasteFileAction(element));

        if (!Objects.equals(currentAsset, file)) {
            items.add(new CopyFileAction(element));
            items.add(new CutFileAction(element));
            items.add(new RenameFileAction(element));
            items.add(new DeleteFileAction(element));
        }

        if (items.isEmpty()) return null;

        return contextMenu;
    }

    /**
     * Fill the tree using the asset folder.
     *
     * @param assetFolder the asset folder.
     */
    public void fill(final Path assetFolder) {

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
    public ConcurrentArray<ResourceElement> getExpandedElements() {
        return expandedElements;
    }

    /**
     * @return the list of selected elements.
     */
    public ConcurrentArray<ResourceElement> getSelectedElements() {
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
    private void startBackgroundFill(final Path assetFolder) {

        final ResourceElement rootElement = createFor(assetFolder);
        final TreeItem<ResourceElement> newRoot = new TreeItem<>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot);

        final Array<String> extensionFilter = getExtensionFilter();
        if (!extensionFilter.isEmpty()) cleanup(newRoot);

        EXECUTOR_MANAGER.addFXTask(() -> {
            setRoot(newRoot);

            final Consumer<Boolean> onLoadHandler = getOnLoadHandler();
            if (onLoadHandler != null) onLoadHandler.accept(Boolean.TRUE);
        });
    }

    /**
     * Start the background process of loading.
     */
    private void startBackgroundRefresh(final Path assetFolder) {

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
    private void fill(final TreeItem<ResourceElement> treeItem) {

        final ResourceElement element = treeItem.getValue();
        final Array<String> extensionFilter = getExtensionFilter();
        if (!element.hasChildren(extensionFilter)) return;

        final ObservableList<TreeItem<ResourceElement>> items = treeItem.getChildren();

        final Array<ResourceElement> children = element.getChildren(extensionFilter);
        children.sort(NAME_COMPARATOR);
        children.forEach(child -> items.add(new TreeItem<>(child)));

        items.forEach(this::fill);
    }

    /**
     * Handle a created file.
     *
     * @param file the created file.
     */
    public void notifyCreated(final Path file) {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        final Path folder = file.getParent();
        if (!folder.startsWith(currentAsset)) return;

        final ResourceElement element = ResourceElementFactory.createFor(folder);

        TreeItem<ResourceElement> folderItem = UIUtils.findItemForValue(getRoot(), element);

        if (folderItem == null) {
            notifyCreated(folder);
            folderItem = UIUtils.findItemForValue(getRoot(), folder);
        }

        if (folderItem == null) return;

        final TreeItem<ResourceElement> newItem = new TreeItem<>(ResourceElementFactory.createFor(file));

        fill(newItem);

        final ObservableList<TreeItem<ResourceElement>> children = folderItem.getChildren();
        children.add(newItem);

        FXCollections.sort(children, ITEM_COMPARATOR);
    }

    /**
     * Handle a removed file.
     */
    public void notifyDeleted(final Path file) {

        final ResourceElement element = ResourceElementFactory.createFor(file);
        final TreeItem<ResourceElement> treeItem = UIUtils.findItemForValue(getRoot(), element);
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
    public void notifyMoved(final Path prevFile, final Path newFile) {

        final ResourceElement prevElement = ResourceElementFactory.createFor(prevFile);
        final TreeItem<ResourceElement> prevItem = UIUtils.findItemForValue(getRoot(), prevElement);
        if (prevItem == null) return;

        final ResourceElement newParentElement = ResourceElementFactory.createFor(newFile.getParent());
        final TreeItem<ResourceElement> newParentItem = UIUtils.findItemForValue(getRoot(), newParentElement);
        if (newParentItem == null) return;

        final TreeItem<ResourceElement> prevParentItem = prevItem.getParent();
        final ObservableList<TreeItem<ResourceElement>> prevParentChildren = prevParentItem.getChildren();
        prevParentChildren.remove(prevItem);

        prevItem.setValue(ResourceElementFactory.createFor(newFile));

        final Array<TreeItem<ResourceElement>> children = ArrayFactory.newArray(TreeItem.class);

        UIUtils.getAllItems(children, prevItem);

        children.fastRemove(prevItem);
        children.forEach(child -> {

            final ResourceElement resourceElement = child.getValue();
            final Path file = resourceElement.getFile();
            final Path relativeFile = file.subpath(prevFile.getNameCount(), file.getNameCount());
            final Path resultFile = newFile.resolve(relativeFile);

            child.setValue(ResourceElementFactory.createFor(resultFile));
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
    public void notifyRenamed(final Path prevFile, final Path newFile) {

        final ResourceElement prevElement = ResourceElementFactory.createFor(prevFile);
        final TreeItem<ResourceElement> prevItem = UIUtils.findItemForValue(getRoot(), prevElement);
        if (prevItem == null) return;

        prevItem.setValue(ResourceElementFactory.createFor(newFile));

        final Array<TreeItem<ResourceElement>> children = ArrayFactory.newArray(TreeItem.class);

        UIUtils.getAllItems(children, prevItem);

        children.fastRemove(prevItem);
        children.forEach(child -> {

            final ResourceElement resourceElement = child.getValue();
            final Path file = resourceElement.getFile();
            final Path relativeFile = file.subpath(prevFile.getNameCount(), file.getNameCount());
            final Path resultFile = newFile.resolve(relativeFile);

            child.setValue(ResourceElementFactory.createFor(resultFile));
        });
    }

    /**
     * Handle pressing on hotkey.
     */
    private void processKey(final KeyEvent event) {
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
     * @return the open resource function.
     */
    public Consumer<ResourceElement> getOpenFunction() {
        return openFunction;
    }

    /**
     * Cleanup the tree.
     */
    public void cleanup(final TreeItem<ResourceElement> treeItem) {

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
     */
    public void expandTo(@NotNull final TreeItem<ResourceElement> treeItem, final boolean needSelect) {

        TreeItem<ResourceElement> parent = treeItem;

        while (parent != null) {
            parent.setExpanded(true);
            parent = parent.getParent();
        }

        if (needSelect) {
            EXECUTOR_MANAGER.addFXTask(() -> {
                final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = getSelectionModel();
                selectionModel.select(treeItem);
                scrollTo(getRow(treeItem));
            });
        }
    }

    /**
     * Mark the element as expanded.
     */
    public void markExpand(@NotNull final Path file) {

        final ResourceElement element = ResourceElementFactory.createFor(file);
        final TreeItem<ResourceElement> treeItem = UIUtils.findItemForValue(getRoot(), element);
        if (treeItem == null) return;

        treeItem.setExpanded(true);
    }

    /**
     * Expand tree to the file.
     */
    public void expandTo(@NotNull final Path file, final boolean needSelect) {

        final ResourceElement element = ResourceElementFactory.createFor(file);
        final TreeItem<ResourceElement> treeItem = UIUtils.findItemForValue(getRoot(), element);
        if (treeItem == null) return;

        TreeItem<ResourceElement> parent = treeItem;

        while (parent != null) {
            parent.setExpanded(true);
            parent = parent.getParent();
        }

        if (needSelect) {
            EXECUTOR_MANAGER.addFXTask(() -> {
                final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = getSelectionModel();
                selectionModel.select(treeItem);
                scrollTo(getRow(treeItem));
            });
        }
    }
}
