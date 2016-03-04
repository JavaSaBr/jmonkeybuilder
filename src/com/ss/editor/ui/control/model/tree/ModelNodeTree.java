package com.ss.editor.ui.control.model.tree;

import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.ModelNodeFactory;
import com.ss.editor.ui.css.CSSClasses;

import java.util.function.Consumer;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;

import static com.ss.editor.ui.util.UIUtils.findItemForValue;

/**
 * Компонент реализации дерево узлов модели.
 *
 * @author Ronn
 */
public class ModelNodeTree extends TitledPane {

    /**
     * Обработчик выделения элемента в дереве.
     */
    private final Consumer<Object> selectionHandler;

    /**
     * Дерево со структурой модели.
     */
    private TreeView<ModelNode<?>> treeView;

    public ModelNodeTree(final Consumer<Object> selectionHandler) {
        this.selectionHandler = selectionHandler;
        setText(Messages.MODEL_FILE_EDITOR_NODE_TREE);
        createContent();
    }

    /**
     * Создание создаержания компонента.
     */
    private void createContent() {

        setStyle("-fx-background-color: green;");

        final VBox container = new VBox();
        container.setStyle("-fx-background-color: blue;");

        treeView = new TreeView<>();
        treeView.setCellFactory(param -> new ModelNodeTreeCell(this));
        treeView.setEditable(true);

        final MultipleSelectionModel<TreeItem<ModelNode<?>>> selectionModel = treeView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> processSelect(newValue));

        FXUtils.addToPane(treeView, container);
        FXUtils.addClassTo(treeView, CSSClasses.TRANSPARENT_TREE_VIEW);
        FXUtils.bindFixedHeight(treeView, container.heightProperty().subtract(20));
        FXUtils.bindFixedWidth(treeView, container.widthProperty().subtract(30));

        setContent(container);
        setContextMenu(new ContextMenu());
    }

    /**
     * @return обработчик выделения элемента в дереве.
     */
    private Consumer<Object> getSelectionHandler() {
        return selectionHandler;
    }

    /**
     * Процесс выбора элемента.
     */
    private void processSelect(final TreeItem<ModelNode<?>> treeItem) {

        final ModelNode<?> value = treeItem.getValue();
        final Object element = value == null? null : value.getElement();

        Object toSelect = null;

        if(element instanceof Spatial) {
            toSelect = element;
        } else if(element instanceof Mesh) {

            final TreeItem<ModelNode<?>> parent = treeItem.getParent();
            final ModelNode<?> parentElement = parent.getValue();

            toSelect = parentElement.getElement();
        }

        final Consumer<Object> selectionHandler = getSelectionHandler();
        selectionHandler.accept(toSelect);
    }

    /**
     * @return дерево со структурой модели.
     */
    private TreeView<ModelNode<?>> getTreeView() {
        return treeView;
    }

    public void fill(final Spatial model) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> currentRoot = treeView.getRoot();

        if (currentRoot != null) {
            treeView.setRoot(null);
        }

        final ModelNode<?> rootElement = ModelNodeFactory.createFor(model);
        final TreeItem<ModelNode<?>> newRoot = new TreeItem<>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot);

        treeView.setRoot(newRoot);
    }

    /**
     * Заполнить узел.
     */
    private void fill(final TreeItem<ModelNode<?>> treeItem) {

        final ModelNode<?> element = treeItem.getValue();

        if(!element.hasChildren()) {
            return;
        }

        final ObservableList<TreeItem<ModelNode<?>>> items = treeItem.getChildren();

        final Array<ModelNode<?>> children = element.getChildren();
        children.forEach(child -> items.add(new TreeItem<>(child)));

        items.forEach(item -> item.setExpanded(true));
        items.forEach(this::fill);
    }

    /**
     * Заполнить узел.
     */
    public void refresh(final ModelNode<?> modelNode) {

        final TreeItem<ModelNode<?>> treeItem = findItemForValue(getTreeView(), modelNode);

        if(treeItem == null) {
            return;
        }

        final ObservableList<TreeItem<ModelNode<?>>> items = treeItem.getChildren();
        items.clear();

        final ModelNode<?> element = treeItem.getValue();

        if(!element.hasChildren()) {
            return;
        }

        final Array<ModelNode<?>> children = element.getChildren();
        children.forEach(child -> items.add(new TreeItem<>(child)));

        items.forEach(item -> item.setExpanded(true));
        items.forEach(this::fill);
    }

    /**
     * Получение контекстного меню для этого элемента.
     */
    public ContextMenu getContextMenu(final ModelNode<?> modelNode) {

        final ContextMenu contextMenu = getContextMenu();
        final ObservableList<MenuItem> items = contextMenu.getItems();
        items.clear();

        modelNode.fillContextMenu(this, items);

        if(items.isEmpty()) {
            return null;
        }

        return contextMenu;
    }

    /**
     * Уведомление и обработка перемещения нода.
     */
    public void notifyMoved(final ModelNode<?> prevParent, final ModelNode<?> newParent, final ModelNode<?> modelNode) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> prevParentItem = findItemForValue(treeView, prevParent);
        final TreeItem<ModelNode<?>> newParentItem = findItemForValue(treeView, newParent);
        final TreeItem<ModelNode<?>> nodeItem = findItemForValue(treeView, modelNode);

        if(prevParentItem == null || newParentItem == null || nodeItem == null) {
            return;
        }

        prevParentItem.getChildren().remove(nodeItem);
        newParentItem.getChildren().add(0, nodeItem);
    }

    /**
     * Уведомление и обработка изменения узла модели.
     */
    public void notifyChanged(final ModelNode<?> modelNode) {

    }

    /**
     * Уведомление и обработка добавления нового узла.
     */
    public void notifyAdded(final ModelNode<?> parent, final ModelNode<?> modelNode) {

    }

    /**
     * Уведомление и обработка удаленя нода.
     */
    public void notifyRemoved(final ModelNode<?> modelNode) {

        final TreeItem<ModelNode<?>> treeItem = findItemForValue(getTreeView(), modelNode);

        if(treeItem == null) {
            return;
        }

        final TreeItem<ModelNode<?>> parent = treeItem.getParent();
        final ObservableList<TreeItem<ModelNode<?>>> children = parent.getChildren();
        children.remove(treeItem);
    }

    /**
     * Попробовать начать редактирование указанного элемента.
     */
    public void startEdit(final ModelNode<?> modelNode) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> treeItem = findItemForValue(treeView, modelNode);

        if(treeItem == null) {
            return;
        }

        treeView.edit(treeItem);
    }
}
