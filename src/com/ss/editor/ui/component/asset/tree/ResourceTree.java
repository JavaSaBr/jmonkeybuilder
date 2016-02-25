package com.ss.editor.ui.component.asset.tree;

import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.component.asset.tree.context.menu.action.OpenFileAction;
import com.ss.editor.ui.component.asset.tree.resource.FileElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceLoadingElement;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.UIUtils;

import java.nio.file.Path;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import rlib.ui.util.FXUtils;
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

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    private static final ArrayComparator<ResourceElement> COMPARATOR = ResourceElement::compareTo;

    /**
     * Развернутые элементы.
     */
    private final Array<ResourceElement> expandedElements;

    /**
     * Выбранные элементы.
     */
    private final Array<ResourceElement> selectedElements;

    public ResourceTree() {
        this.expandedElements = ArrayFactory.newConcurrentAtomicArray(ResourceElement.class);
        this.selectedElements = ArrayFactory.newConcurrentAtomicArray(ResourceElement.class);

        FXUtils.addClassTo(this, CSSClasses.TRANSPARENT_TREE_VIEW);

        setCellFactory(CELL_FACTORY);
        setShowRoot(true);
    }

    /**
     * Обновление контекстного меню под указанный элемент.
     */
    public void updateContextMenu(final ResourceElement element) {

        final ContextMenu contextMenu = new ContextMenu();
        final ObservableList<MenuItem> items = contextMenu.getItems();

        if(element instanceof FileElement) {
            items.add(new OpenFileAction(element));
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

        if(currentRoot != null) {
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

        if(currentAsset == null) {
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

                if(!item.isExpanded()) {
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

    private void showLoading() {
        setRoot(new TreeItem<>(ResourceLoadingElement.getInstance()));
    }

    private void startBackgroundFill(final Path assetFolder) {

        final ResourceElement rootElement = createFor(assetFolder);
        final TreeItem<ResourceElement> newRoot = new TreeItem<>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot);

        EXECUTOR_MANAGER.addFXTask(() -> setRoot(newRoot));
    }

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

                if(item == null) {
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

    private void fill(final TreeItem<ResourceElement> treeItem) {

        final ResourceElement element = treeItem.getValue();

        if(!element.hasChildren()) {
            return;
        }

        final ObservableList<TreeItem<ResourceElement>> items = treeItem.getChildren();

        final Array<ResourceElement> children = element.getChildren();
        children.forEach(child -> items.add(new TreeItem<>(child)));

        items.forEach(this::fill);
    }
}
