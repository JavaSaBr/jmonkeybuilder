package com.ss.editor.ui.control.model.tree;

import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
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

import static com.ss.editor.ui.control.model.tree.node.ModelNodeFactory.createFor;
import static com.ss.editor.ui.util.UIUtils.findItemForValue;

/**
 * Компонент реализации дерево узлов модели.
 *
 * @author Ronn
 */
public class ModelNodeTree extends TitledPane {

    public static final String USER_DATA_IS_SKY = ModelNodeTree.class.getName() + ".isSky";

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * Обработчик выделения элемента в дереве.
     */
    private final Consumer<Object> selectionHandler;

    /**
     * Потребитель изменений модели.
     */
    private final ModelChangeConsumer modelChangeConsumer;

    /**
     * Дерево со структурой модели.
     */
    private TreeView<ModelNode<?>> treeView;

    public ModelNodeTree(final Consumer<Object> selectionHandler, final ModelChangeConsumer modelChangeConsumer) {
        this.selectionHandler = selectionHandler;
        this.modelChangeConsumer = modelChangeConsumer;
        setText(Messages.MODEL_FILE_EDITOR_NODE_TREE);
        createComponents();
        setAnimated(false);
    }

    /**
     * @return потребитель изменений модели.
     */
    public ModelChangeConsumer getModelChangeConsumer() {
        return modelChangeConsumer;
    }

    /**
     * Создание создаержания компонента.
     */
    private void createComponents() {

        final VBox container = new VBox();

        treeView = new TreeView<>();
        treeView.setCellFactory(param -> new ModelNodeTreeCell(this));
        treeView.setShowRoot(true);
        treeView.setEditable(true);
        treeView.setFocusTraversable(true);

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

        final Consumer<Object> selectionHandler = getSelectionHandler();

        if (treeItem == null) {
            selectionHandler.accept(null);
            return;
        }

        final ModelNode<?> value = treeItem.getValue();
        final Object element = value == null ? null : value.getElement();

        Object toSelect = null;

        if (element instanceof Spatial) {
            toSelect = element;
        } else if (element instanceof Mesh) {

            final TreeItem<ModelNode<?>> parent = treeItem.getParent();
            final ModelNode<?> parentElement = parent.getValue();

            toSelect = parentElement.getElement();
        } else {
            toSelect = element;
        }

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

        final ModelNode<?> rootElement = createFor(model);
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

        if (!element.hasChildren()) {
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

        if (treeItem == null) {
            return;
        }

        final ObservableList<TreeItem<ModelNode<?>>> items = treeItem.getChildren();
        items.clear();

        final ModelNode<?> element = treeItem.getValue();

        if (!element.hasChildren()) {
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

        if (items.isEmpty()) {
            return null;
        }

        return contextMenu;
    }

    /**
     * Уведомление и обработка перемещения нода.
     */
    public void notifyMoved(final Object prevParent, final Object newParent, final Object node, final int index) {
        notifyMoved(createFor(prevParent), createFor(newParent), createFor(node), index);
    }

    /**
     * Уведомление и обработка перемещения нода.
     */
    public void notifyMoved(final ModelNode<?> prevParent, final ModelNode<?> newParent, final ModelNode<?> node, final int index) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> prevParentItem = findItemForValue(treeView, prevParent);
        final TreeItem<ModelNode<?>> newParentItem = findItemForValue(treeView, newParent);
        final TreeItem<ModelNode<?>> nodeItem = findItemForValue(treeView, node);

        if (prevParentItem == null || newParentItem == null || nodeItem == null) {
            return;
        }

        prevParentItem.getChildren().remove(nodeItem);
        newParentItem.getChildren().add(index, nodeItem);

        EXECUTOR_MANAGER.addFXTask(() -> select(node.getElement()));
    }

    /**
     * Уведомление и обработка изменения узла модели.
     */
    public void notifyChanged(final Object object) {
        notifyChanged(createFor(object));
    }

    /**
     * Уведомление и обработка изменения узла модели.
     */
    public void notifyChanged(final ModelNode<?> modelNode) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> treeItem = findItemForValue(treeView, modelNode);

        if (treeItem == null) {
            return;
        }

        treeItem.setValue(null);
        treeItem.setValue(modelNode);
    }

    /**
     * Уведомление и обработка добавления нового узла.
     */
    public void notifyReplace(final Object parent, final Object oldChild, final Object newChild) {
        notifyReplace(createFor(parent), createFor(oldChild), createFor(newChild));
    }

    /**
     * Уведомление и обработка добавления нового узла.
     */
    public void notifyReplace(final ModelNode<?> parent, final ModelNode<?> oldChild, final ModelNode<?> newChild) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> parentItem = findItemForValue(treeView, parent);

        if (parentItem == null) {

            final TreeItem<ModelNode<?>> childItem = new TreeItem<>(newChild);
            childItem.setExpanded(true);

            fill(childItem);

            treeView.setRoot(childItem);
            return;
        }

        int index = 0;
        boolean needExpand = false;

        final ObservableList<TreeItem<ModelNode<?>>> children = parentItem.getChildren();
        final TreeItem<ModelNode<?>> oldChildItem = findItemForValue(treeView, oldChild);

        if (oldChildItem != null) {
            index = children.indexOf(oldChildItem);
            needExpand = oldChildItem.isExpanded();
            children.remove(oldChildItem);
        }

        final TreeItem<ModelNode<?>> childItem = new TreeItem<>(newChild);
        childItem.setExpanded(needExpand);

        fill(childItem);

        children.add(index, childItem);
    }

    /**
     * Уведомление и обработка добавления нового узла.
     */
    public void notifyAdded(final Object parent, final Object child) {
        notifyAdded(createFor(parent), createFor(child));
    }

    /**
     * Уведомление и обработка добавления нового узла.
     */
    public void notifyAdded(final ModelNode<?> parent, final ModelNode<?> child) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> parentItem = findItemForValue(treeView, parent);

        if (parentItem == null) {
            return;
        }

        final TreeItem<ModelNode<?>> childItem = new TreeItem<>(child);

        final ObservableList<TreeItem<ModelNode<?>>> children = parentItem.getChildren();
        children.add(0, childItem);

        parentItem.setExpanded(true);

        fill(childItem);
    }

    /**
     * Уведомление и обработка удаленя нода.
     */
    public void notifyRemoved(final Object child) {
        notifyRemoved(createFor(child));
    }

    /**
     * Уведомление и обработка удаленя нода.
     */
    public void notifyRemoved(final ModelNode<?> modelNode) {

        final TreeItem<ModelNode<?>> treeItem = findItemForValue(getTreeView(), modelNode);

        if (treeItem == null) {
            return;
        }

        final TreeItem<ModelNode<?>> parentItem = treeItem.getParent();
        final ObservableList<TreeItem<ModelNode<?>>> children = parentItem.getChildren();
        children.remove(treeItem);

        if (parentItem.isExpanded() && children.isEmpty()) {
            parentItem.setExpanded(false);
        }
    }

    /**
     * Поиск родительского элемента для указанного элемента.
     */
    public ModelNode<?> findParent(final ModelNode<?> modelNode) {

        final TreeItem<ModelNode<?>> treeItem = findItemForValue(getTreeView(), modelNode);

        if (treeItem == null) {
            return null;
        }

        final TreeItem<ModelNode<?>> parent = treeItem.getParent();

        return parent == null ? null : parent.getValue();
    }

    /**
     * Попробовать начать редактирование указанного элемента.
     */
    public void startEdit(final ModelNode<?> modelNode) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> treeItem = findItemForValue(treeView, modelNode);

        if (treeItem == null) {
            return;
        }

        treeView.edit(treeItem);
    }

    /**
     * Выделение указанного объекта в дереве.
     */
    public void select(final Object object) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final MultipleSelectionModel<TreeItem<ModelNode<?>>> selectionModel = treeView.getSelectionModel();

        final ModelNode<Object> modelNode = createFor(object);

        if (modelNode == null) {
            selectionModel.select(null);
            return;
        }

        final TreeItem<ModelNode<?>> treeItem = findItemForValue(treeView, modelNode);

        if (treeItem == null) {
            selectionModel.select(null);
            return;
        }

        selectionModel.select(treeItem);
    }
}
