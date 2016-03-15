package com.ss.editor.ui.component.asset.tree;

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

import java.nio.file.Path;
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
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayComparator;
import rlib.util.array.ArrayFactory;

import static com.ss.editor.ui.component.asset.tree.ResourceTreeCell.CELL_FACTORY;
import static com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory.createFor;
import static com.ss.editor.ui.css.CSSClasses.MAIN_FONT_13;
import static com.ss.editor.ui.util.UIUtils.findItemForValue;

/**
 * Реализация дерева ресурсов.
 *
 * @author Ronn
 */
public class ResourceTree extends TreeView<ResourceElement> {

    private static final FileConverterRegistry FILE_CONVERTER_REGISTRY = FileConverterRegistry.getInstance();
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    private static final ArrayComparator<ResourceElement> COMPARATOR = ResourceElement::compareTo;
    private static final ArrayComparator<ResourceElement> NAME_COMPARATOR = (first, second) -> {

        final int firstLevel = getLevel(first);
        final int secondLevel = getLevel(second);

        if (firstLevel != secondLevel) {
            return firstLevel - secondLevel;
        }

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

        if (firstLevel != secondLevel) {
            return firstLevel - secondLevel;
        }

        return NAME_COMPARATOR.compare(firstElement, secondElement);
    };

    private static int getLevel(final ResourceElement element) {

        if (element instanceof FolderElement) {
            return 1;
        }

        return 2;
    }

    private static final Consumer<ResourceElement> DEFAULT_FUNCTION = element -> {
        final OpenFileAction action = new OpenFileAction(element);
        final EventHandler<ActionEvent> onAction = action.getOnAction();
        onAction.handle(null);
    };

    /**
     * Развернутые элементы.
     */
    private final Array<ResourceElement> expandedElements;

    /**
     * Выбранные элементы.
     */
    private final Array<ResourceElement> selectedElements;

    /**
     * Функция окрытия файла.
     */
    private final Consumer<ResourceElement> openFunction;

    /**
     * Режим только чтения.
     */
    private final boolean readOnly;

    /**
     * Список фильтруемых расширений.
     */
    private Array<String> extensionFilter;

    /**
     * Пост загрузачный обработчик.
     */
    private Runnable onLoadHandler;

    public ResourceTree(final boolean readOnly) {
        this(DEFAULT_FUNCTION, readOnly);
    }

    public ResourceTree(final Consumer<ResourceElement> openFunction, final boolean readOnly) {
        this.openFunction = openFunction;
        this.readOnly = readOnly;
        this.expandedElements = ArrayFactory.newConcurrentAtomicArray(ResourceElement.class);
        this.selectedElements = ArrayFactory.newConcurrentAtomicArray(ResourceElement.class);

        FXUtils.addClassTo(this, CSSClasses.TRANSPARENT_TREE_VIEW);

        setCellFactory(CELL_FACTORY);
        setOnKeyPressed(this::processKey);
        setShowRoot(true);
        setContextMenu(new ContextMenu());
    }

    /**
     * @param extensionFilter список фильтруемых расширений.
     */
    public void setExtensionFilter(Array<String> extensionFilter) {
        this.extensionFilter = extensionFilter;
    }

    /**
     * @return список фильтруемых расширений.
     */
    private Array<String> getExtensionFilter() {
        return extensionFilter;
    }

    /**
     * @param onLoadHandler пост загрузачный обработчик.
     */
    public void setOnLoadHandler(Runnable onLoadHandler) {
        this.onLoadHandler = onLoadHandler;
    }

    /**
     * @return пост загрузачныый обработчик.
     */
    private Runnable getOnLoadHandler() {
        return onLoadHandler;
    }

    /**
     * @return режим только чтения.
     */
    private boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Обновление контекстного меню под указанный элемент.
     */
    public void updateContextMenu(final ResourceElement element) {

        if (isReadOnly()) {
            return;
        }

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();

        final ContextMenu contextMenu = getContextMenu();
        final ObservableList<MenuItem> items = contextMenu.getItems();
        items.clear();

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

        if (EditorUtil.hasFileInClipboard()) {
            items.add(new PasteFileAction(element));
        }

        if (!currentAsset.equals(file)) {
            items.add(new CopyFileAction(element));
            items.add(new CutFileAction(element));
            items.add(new RenameFileAction(element));
            items.add(new DeleteFileAction(element));
        }

        final Array<MenuItem> allItems = ArrayFactory.newArray(MenuItem.class);
        items.forEach(subItem -> UIUtils.getAllItems(allItems, subItem));
        allItems.forEach(menuItem -> FXUtils.addClassTo(menuItem, MAIN_FONT_13));

        setContextMenu(contextMenu);
    }

    /**
     * Заполнить дерево по новой папке асета.
     *
     * @param assetFolder новая папка ассета.
     */
    public void fill(final Path assetFolder) {

        final TreeItem<ResourceElement> currentRoot = getRoot();

        if (currentRoot != null) {
            setRoot(null);
        }

        showLoading();

        EXECUTOR_MANAGER.addBackgroundTask(() -> startBackgroundFill(assetFolder));
    }

    /**
     * @return развернутые элементы.
     */
    public Array<ResourceElement> getExpandedElements() {
        return expandedElements;
    }

    /**
     * @return выбранные элементы.
     */
    public Array<ResourceElement> getSelectedElements() {
        return selectedElements;
    }

    /**
     * Обновить дерево.
     */
    public void refresh() {

        final EditorConfig config = EditorConfig.getInstance();
        final Path currentAsset = config.getCurrentAsset();

        if (currentAsset == null) {
            setRoot(null);
            return;
        }

        updateSelectedElements();
        updateExpandedElements();

        setRoot(null);
        showLoading();

        EXECUTOR_MANAGER.addBackgroundTask(() -> startBackgroundRefresh(currentAsset));
    }

    /**
     * Обновление развернутых элементов.
     */
    private void updateExpandedElements() {

        final Array<ResourceElement> expandedElements = getExpandedElements();
        expandedElements.writeLock();
        try {

            expandedElements.clear();

            final Array<TreeItem<ResourceElement>> allItems = UIUtils.getAllItems(this);
            allItems.forEach(item -> {

                if (!item.isExpanded()) {
                    return;
                }

                expandedElements.add(item.getValue());
            });

        } finally {
            expandedElements.writeUnlock();
        }
    }

    /**
     * Обновление списка выбранных элементов.
     */
    private void updateSelectedElements() {

        final Array<ResourceElement> selectedElements = getSelectedElements();
        selectedElements.writeLock();
        try {

            selectedElements.clear();

            final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = getSelectionModel();
            final ObservableList<TreeItem<ResourceElement>> selectedItems = selectionModel.getSelectedItems();
            selectedItems.forEach(item -> selectedElements.add(item.getValue()));

        } finally {
            selectedElements.writeUnlock();
        }
    }

    /**
     * Отобразить прогресс прогрузки.
     */
    private void showLoading() {
        setRoot(new TreeItem<>(ResourceLoadingElement.getInstance()));
    }

    /**
     * Запустить фоновое построение дерева.
     */
    private void startBackgroundFill(final Path assetFolder) {

        final ResourceElement rootElement = createFor(assetFolder);
        final TreeItem<ResourceElement> newRoot = new TreeItem<>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot);

        final Array<String> extensionFilter = getExtensionFilter();

        if (extensionFilter != null) {
            cleanup(newRoot);
        }

        EXECUTOR_MANAGER.addFXTask(() -> {

            setRoot(newRoot);

            final Runnable onLoadHandler = getOnLoadHandler();

            if (onLoadHandler != null) {
                onLoadHandler.run();
            }
        });
    }

    /**
     * Запустить фоновое обновление дерева.
     */
    private void startBackgroundRefresh(final Path assetFolder) {

        final ResourceElement rootElement = createFor(assetFolder);
        final TreeItem<ResourceElement> newRoot = new TreeItem<>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot);

        final Array<ResourceElement> expandedElements = getExpandedElements();
        expandedElements.writeLock();
        try {

            expandedElements.sort(COMPARATOR);
            expandedElements.forEach(element -> {

                final TreeItem<ResourceElement> item = findItemForValue(newRoot, element);

                if (item == null) {
                    return;
                }

                item.setExpanded(true);
            });

            expandedElements.clear();

        } finally {
            expandedElements.writeUnlock();
        }

        EXECUTOR_MANAGER.addFXTask(() -> {

            setRoot(newRoot);
            restoreSelection();

            final Runnable onLoadHandler = getOnLoadHandler();

            if (onLoadHandler != null) {
                onLoadHandler.run();
            }
        });
    }

    /**
     * Восстановление выбранных элементов.
     */
    private void restoreSelection() {
        EXECUTOR_MANAGER.addFXTask(() -> {

            final Array<ResourceElement> selectedElements = getSelectedElements();
            selectedElements.writeLock();
            try {

                final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = getSelectionModel();

                selectedElements.forEach(element -> {

                    final TreeItem<ResourceElement> item = findItemForValue(getRoot(), element);

                    if (item == null) {
                        return;
                    }

                    selectionModel.select(item);
                });

                selectedElements.clear();

            } finally {
                selectedElements.writeUnlock();
            }
        });
    }

    /**
     * Заполнить узел.
     */
    private void fill(final TreeItem<ResourceElement> treeItem) {

        final ResourceElement element = treeItem.getValue();
        final Array<String> extensionFilter = getExtensionFilter();

        if (!element.hasChildren(extensionFilter)) {
            return;
        }

        final ObservableList<TreeItem<ResourceElement>> items = treeItem.getChildren();

        final Array<ResourceElement> children = element.getChildren(extensionFilter);
        children.sort(NAME_COMPARATOR);
        children.forEach(child -> items.add(new TreeItem<>(child)));

        items.forEach(this::fill);
    }

    /**
     * Уведомление о созданном файле.
     *
     * @param file созданный файл.
     */
    public void notifyCreated(final Path file) {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        final Path folder = file.getParent();

        if (!folder.startsWith(currentAsset)) {
            return;
        }

        final ResourceElement element = ResourceElementFactory.createFor(folder);

        TreeItem<ResourceElement> folderItem = UIUtils.findItemForValue(getRoot(), element);

        if (folderItem == null) {
            notifyCreated(folder);
            folderItem = UIUtils.findItemForValue(getRoot(), folder);
        }

        if (folderItem == null) {
            return;
        }

        final TreeItem<ResourceElement> newItem = new TreeItem<>(ResourceElementFactory.createFor(file));

        fill(newItem);

        final ObservableList<TreeItem<ResourceElement>> children = folderItem.getChildren();
        children.add(newItem);

        FXCollections.sort(children, ITEM_COMPARATOR);
    }

    /**
     * Уведомление об удаленном файле.
     */
    public void notifyDeleted(final Path file) {

        final ResourceElement element = ResourceElementFactory.createFor(file);
        final TreeItem<ResourceElement> treeItem = UIUtils.findItemForValue(getRoot(), element);

        if (treeItem == null) {
            return;
        }

        final TreeItem<ResourceElement> parent = treeItem.getParent();

        if (parent == null) {
            return;
        }

        final ObservableList<TreeItem<ResourceElement>> children = parent.getChildren();
        children.remove(treeItem);
    }

    /**
     * Уведомление о перемещении файла.
     *
     * @param prevFile старая версия файла.
     * @param newFile  новая версия файла.
     */
    public void notifyMoved(final Path prevFile, final Path newFile) {

        final ResourceElement prevElement = ResourceElementFactory.createFor(prevFile);
        final TreeItem<ResourceElement> prevItem = UIUtils.findItemForValue(getRoot(), prevElement);

        if (prevItem == null) {
            return;
        }

        final ResourceElement newParentElement = ResourceElementFactory.createFor(newFile.getParent());
        final TreeItem<ResourceElement> newParentItem = UIUtils.findItemForValue(getRoot(), newParentElement);

        if (newParentItem == null) {
            return;
        }

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
     * Уведомление о переименовании файла.
     *
     * @param prevFile старая версия файла.
     * @param newFile  новая версия файла.
     */
    public void notifyRenamed(final Path prevFile, final Path newFile) {

        final ResourceElement prevElement = ResourceElementFactory.createFor(prevFile);
        final TreeItem<ResourceElement> prevItem = UIUtils.findItemForValue(getRoot(), prevElement);

        if (prevItem == null) {
            return;
        }

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
     * Обработка нажатий на хоткеи.
     */
    private void processKey(final KeyEvent event) {

        if (isReadOnly()) {
            return;
        }

        final MultipleSelectionModel<TreeItem<ResourceElement>> selectionModel = getSelectionModel();
        final TreeItem<ResourceElement> selectedItem = selectionModel.getSelectedItem();

        if (selectedItem == null) {
            return;
        }

        final ResourceElement item = selectedItem.getValue();

        if (item == null || item instanceof ResourceLoadingElement) {
            return;
        }

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();

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

        } else if(keyCode == KeyCode.DELETE && !currentAsset.equals(item.getFile())) {

            final DeleteFileAction action = new DeleteFileAction(item);
            final EventHandler<ActionEvent> onAction = action.getOnAction();
            onAction.handle(null);
        }
    }

    /**
     * @return функция окрытия файла.
     */
    public Consumer<ResourceElement> getOpenFunction() {
        return openFunction;
    }

    /**
     * Очистка дерева от пустых узлов.
     */
    public boolean cleanup(final TreeItem<ResourceElement> treeItem) {

        final ResourceElement element = treeItem.getValue();

        if (element instanceof FileElement) {
            return false;
        }

        final ObservableList<TreeItem<ResourceElement>> children = treeItem.getChildren();

        for (int i = children.size() - 1; i >= 0; i--) {
            cleanup(children.get(i));
        }

        if (children.isEmpty() && treeItem.getParent() != null) {
            final TreeItem<ResourceElement> parent = treeItem.getParent();
            final ObservableList<TreeItem<ResourceElement>> parentChildren = parent.getChildren();
            parentChildren.remove(treeItem);
            return true;
        }

        return false;
    }

    /**
     * Развернуть девео до указанного файла.
     */
    public void expandTo(final Path file, boolean needSelect) {

        final ResourceElement element = ResourceElementFactory.createFor(file);
        final TreeItem<ResourceElement> treeItem = UIUtils.findItemForValue(getRoot(), element);

        if (treeItem == null) {
            return;
        }

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
