package com.ss.editor.ui.control.model.tree;

import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.css.CSSClasses;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 * The implementation of Tree for presentation the structure of model in the Editor.
 *
 * @author JavaSaBr
 */
public class ModelNodeTree extends TitledPane {

    public static final String USER_DATA_IS_SKY = ModelNodeTree.class.getName() + ".isSky";

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * THe handler of selected objects.
     */
    @NotNull
    private final Consumer<Object> selectionHandler;

    /**
     * The consumer of changes of the model.
     */
    @NotNull
    private final ModelChangeConsumer modelChangeConsumer;

    /**
     * The tree with structure of the model.
     */
    private TreeView<ModelNode<?>> treeView;

    public ModelNodeTree(@NotNull final Consumer<Object> selectionHandler, @NotNull final ModelChangeConsumer modelChangeConsumer) {
        this.selectionHandler = selectionHandler;
        this.modelChangeConsumer = modelChangeConsumer;
        setText(Messages.MODEL_FILE_EDITOR_NODE_TREE);
        createComponents();
        setAnimated(false);
    }

    /**
     * Create components of this component.
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
     * Select the item.
     */
    private void processSelect(@Nullable final TreeItem<ModelNode<?>> treeItem) {

        if (treeItem == null) {
            selectionHandler.accept(null);
            return;
        }

        final ModelNode<?> value = treeItem.getValue();
        final Object element = value == null ? null : value.getElement();

        selectionHandler.accept(element);
    }

    /**
     * @return the tree of this model.
     */
    @NotNull
    private TreeView<ModelNode<?>> getTreeView() {
        return treeView;
    }

    /**
     * Fill the tree for the model.
     *
     * @param model the model.
     */
    public void fill(@NotNull final Spatial model) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> currentRoot = treeView.getRoot();

        if (currentRoot != null) {
            treeView.setRoot(null);
        }

        final ModelNode<?> rootElement = createFor(model);
        final TreeItem<ModelNode<?>> newRoot = new TreeItem<>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot, false);

        treeView.setRoot(newRoot);
    }

    /**
     * Fill the item.
     */
    private void fill(@NotNull final TreeItem<ModelNode<?>> treeItem, final boolean expanded) {
        treeItem.setExpanded(expanded);

        final ModelNode<?> element = treeItem.getValue();
        if (!element.hasChildren()) return;

        final ObservableList<TreeItem<ModelNode<?>>> items = treeItem.getChildren();

        final Array<ModelNode<?>> children = element.getChildren();
        children.forEach(child -> items.add(new TreeItem<>(child)));

        items.forEach(item -> fill(item, expanded));
    }

    /**
     * Fill the node.
     */
    public void refresh(@NotNull final ModelNode<?> modelNode) {

        final TreeItem<ModelNode<?>> treeItem = findItemForValue(getTreeView(), modelNode);
        if (treeItem == null) return;

        final ObservableList<TreeItem<ModelNode<?>>> items = treeItem.getChildren();
        items.clear();

        final ModelNode<?> element = treeItem.getValue();
        if (!element.hasChildren()) return;

        final Array<ModelNode<?>> children = element.getChildren();
        children.forEach(child -> items.add(new TreeItem<>(child)));

        items.forEach(modelNodeTreeItem -> fill(modelNodeTreeItem, true));
    }

    /**
     * Update the node.
     */
    public void update(@NotNull final ModelNode<?> modelNode) {

        final TreeItem<ModelNode<?>> treeItem = findItemForValue(getTreeView(), modelNode);
        if (treeItem == null) return;

        treeItem.setValue(null);
        treeItem.setValue(modelNode);
    }

    /**
     * Get the context menu for the element.
     */
    public ContextMenu getContextMenu(@NotNull final ModelNode<?> modelNode) {

        final ContextMenu contextMenu = getContextMenu();
        final ObservableList<MenuItem> items = contextMenu.getItems();
        items.clear();

        modelNode.fillContextMenu(this, items);

        if (items.isEmpty()) return null;

        return contextMenu;
    }

    /**
     * Notify about moving the element.
     */
    public void notifyMoved(@NotNull final Object prevParent, @NotNull final Object newParent, @NotNull final Object node, final int index) {
        notifyMoved(createFor(prevParent), createFor(newParent), createFor(node), index);
    }

    /**
     * Notify about moving the element.
     */
    public void notifyMoved(@NotNull final ModelNode<?> prevParent, @NotNull final ModelNode<?> newParent, @NotNull final ModelNode<?> node, final int index) {

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
     * Notify about changing the element.
     */
    public void notifyChanged(@NotNull final Object object) {
        notifyChanged(createFor(object));
    }

    /**
     * Notify about changed the element.
     */
    public void notifyChanged(@NotNull final ModelNode<?> modelNode) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> treeItem = findItemForValue(treeView, modelNode);
        if (treeItem == null) return;

        treeItem.setValue(null);
        treeItem.setValue(modelNode);
    }

    /**
     * Notify about replacing the element.
     */
    public void notifyReplace(@NotNull final Object parent, @Nullable final Object oldChild, @Nullable final Object newChild) {
        notifyReplace(createFor(parent), oldChild == null ? null : createFor(oldChild), newChild == null ? null : createFor(newChild));
    }

    /**
     * Notify about replacing the element.
     */
    public void notifyReplace(@NotNull final ModelNode<?> parent, @Nullable final ModelNode<?> oldChild, @Nullable final ModelNode<?> newChild) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> parentItem = findItemForValue(treeView, parent);

        if (parentItem == null) {
            if (newChild == null) return;
            final TreeItem<ModelNode<?>> childItem = new TreeItem<>(newChild);
            childItem.setExpanded(true);
            fill(childItem, true);
            treeView.setRoot(childItem);
            return;
        }

        int index = 0;
        boolean needExpand = false;

        final MultipleSelectionModel<TreeItem<ModelNode<?>>> selectionModel = treeView.getSelectionModel();
        final ObservableList<TreeItem<ModelNode<?>>> children = parentItem.getChildren();
        final TreeItem<ModelNode<?>> oldChildItem = oldChild == null ? null : findItemForValue(treeView, oldChild);
        final TreeItem<ModelNode<?>> selectedItem = selectionModel.getSelectedItem();

        final boolean needSelect = selectedItem == oldChildItem;

        if (oldChildItem != null) {
            index = children.indexOf(oldChildItem);
            needExpand = oldChildItem.isExpanded();
            children.remove(oldChildItem);
        }

        if (newChild == null) return;

        final TreeItem<ModelNode<?>> childItem = new TreeItem<>(newChild);
        childItem.setExpanded(needExpand);

        fill(childItem, true);

        children.add(index, childItem);
        if (needSelect) selectionModel.select(childItem);
    }

    /**
     * Notify about adding the element.
     */
    public void notifyAdded(@NotNull final Object parent, @NotNull final Object child, final int index) {
        notifyAdded(createFor(parent), createFor(child, parent), index);
    }

    /**
     * Notify about adding the element.
     */
    public void notifyAdded(@NotNull final ModelNode<?> parent, @NotNull final ModelNode<?> child, final int index) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> parentItem = findItemForValue(treeView, parent);
        if (parentItem == null) return;

        final TreeItem<ModelNode<?>> childItem = new TreeItem<>(child);

        final ObservableList<TreeItem<ModelNode<?>>> children = parentItem.getChildren();
        if (index == -1) children.add(childItem);
        else children.add(index, childItem);

        parentItem.setExpanded(true);

        fill(childItem, true);
    }

    /**
     * Notify about removing the element.
     */
    public void notifyRemoved(@NotNull final Object parent, @NotNull final Object child) {
        notifyRemoved(createFor(child, parent));
    }

    /**
     * Notify about removing the element.
     */
    public void notifyRemoved(@NotNull final ModelNode<?> modelNode) {

        final TreeItem<ModelNode<?>> treeItem = findItemForValue(getTreeView(), modelNode);
        if (treeItem == null) return;

        final TreeItem<ModelNode<?>> parentItem = treeItem.getParent();
        final ObservableList<TreeItem<ModelNode<?>>> children = parentItem.getChildren();
        children.remove(treeItem);

        if (parentItem.isExpanded() && children.isEmpty()) {
            parentItem.setExpanded(false);
        }
    }

    /**
     * Find a parent for the node.
     *
     * @return the parent or null.
     */
    @Nullable
    public ModelNode<?> findParent(@NotNull final ModelNode<?> modelNode) {

        final TreeItem<ModelNode<?>> treeItem = findItemForValue(getTreeView(), modelNode);
        if (treeItem == null) return null;

        final TreeItem<ModelNode<?>> parent = treeItem.getParent();
        return parent == null ? null : parent.getValue();
    }

    /**
     * Start editing the element.
     */
    public void startEdit(@NotNull final ModelNode<?> modelNode) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> treeItem = findItemForValue(treeView, modelNode);
        if (treeItem == null) return;

        treeView.edit(treeItem);
    }

    /**
     * Select the object in the tree.
     */
    public void select(@Nullable final Object object) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final MultipleSelectionModel<TreeItem<ModelNode<?>>> selectionModel = treeView.getSelectionModel();

        if (object == null) {
            selectionModel.select(null);
            return;
        }

        final ModelNode<Object> modelNode = createFor(object);
        final TreeItem<ModelNode<?>> treeItem = findItemForValue(treeView, modelNode);

        if (treeItem == null) {
            selectionModel.select(null);
            return;
        }

        selectionModel.select(treeItem);
    }

    /**
     * @return the consumer of changes of the model.
     */
    @NotNull
    public ModelChangeConsumer getModelChangeConsumer() {
        return modelChangeConsumer;
    }
}
