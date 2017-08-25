package com.ss.editor.ui.control.tree;

import static com.ss.editor.ui.util.UIUtils.findItemForValue;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.TreeNodeFactoryRegistry;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * The implementation of {@link TreeView} to present some structure.
 *
 * @param <C> the type parameter
 * @author JavaSaBr
 */
public class NodeTree<C extends ChangeConsumer> extends VBox {

    /**
     * The executor manager.
     */
    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The tree node factory.
     */
    @NotNull
    protected static final TreeNodeFactoryRegistry FACTORY_REGISTRY = TreeNodeFactoryRegistry.getInstance();

    /**
     * The handler of selected objects.
     */
    @NotNull
    private final Consumer<Object> selectionHandler;

    /**
     * The consumer of changes of the model.
     */
    @Nullable
    private final C changeConsumer;

    /**
     * The tree with structure of the model.
     */
    @Nullable
    private TreeView<TreeNode<?>> treeView;

    /**
     * Instantiates a new Abstract node tree.
     *
     * @param selectionHandler the selection handler
     * @param consumer         the consumer
     */
    public NodeTree(@NotNull final Consumer<Object> selectionHandler, @Nullable final C consumer) {
        this.selectionHandler = selectionHandler;
        this.changeConsumer = consumer;
        createComponents();
        FXUtils.addClassTo(this, CSSClasses.ABSTRACT_NODE_TREE_CONTAINER);
    }

    /**
     * Create components of this component.
     */
    protected void createComponents() {

        treeView = new TreeView<>();
        treeView.setCellFactory(param -> createNodeTreeCell());
        treeView.setShowRoot(true);
        treeView.setEditable(true);
        treeView.setFocusTraversable(true);
        treeView.prefHeightProperty().bind(heightProperty());
        treeView.prefWidthProperty().bind(widthProperty());

        final MultipleSelectionModel<TreeItem<TreeNode<?>>> selectionModel = treeView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> processSelect(newValue));

        FXUtils.addToPane(treeView, this);
    }

    /**
     * Create node tree cell abstract node tree cell.
     *
     * @return the abstract node tree cell
     */
    protected @NotNull NodeTreeCell<C, ?> createNodeTreeCell() {
        return new NodeTreeCell<>(this);
    }

    /**
     * Select the item.
     */
    private void processSelect(@Nullable final TreeItem<TreeNode<?>> treeItem) {

        if (treeItem == null) {
            selectionHandler.accept(null);
            return;
        }

        selectionHandler.accept(treeItem.getValue());
    }

    /**
     * Gets tree view.
     *
     * @return the tree of this model.
     */
    public @NotNull TreeView<TreeNode<?>> getTreeView() {
        return notNull(treeView);
    }

    /**
     * Fill the tree for the object.
     *
     * @param object the object.
     */
    @FXThread
    public void fill(@NotNull final Object object) {

        final TreeView<TreeNode<?>> treeView = getTreeView();
        final TreeItem<TreeNode<?>> currentRoot = treeView.getRoot();

        if (currentRoot != null) {
            treeView.setRoot(null);
        }

        final TreeNode<?> rootElement = FACTORY_REGISTRY.createFor(object);
        final TreeItem<TreeNode<?>> newRoot = new TreeItem<>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot, false, 1);

        treeView.setRoot(newRoot);
    }

    /**
     * Fill the item.
     */
    @FXThread
    private void fill(@NotNull final TreeItem<TreeNode<?>> treeItem, final boolean expanded, final int level) {
        treeItem.setExpanded(expanded || level == 1);

        final TreeNode<?> element = treeItem.getValue();
        if (!element.hasChildren(this)) return;

        final ObservableList<TreeItem<TreeNode<?>>> items = treeItem.getChildren();

        final Array<TreeNode<?>> children = element.getChildren(this);
        children.forEach(child -> {
            element.notifyChildPreAdd(child);
            items.add(new TreeItem<>(child));
            element.notifyChildAdded(child);
        });

        items.forEach(item -> fill(item, expanded, level == -1 ? -1 : level + 1));
    }

    /**
     * Fill the node.
     *
     * @param treeNode the model node
     */
    @FXThread
    public void refresh(@NotNull final TreeNode<?> treeNode) {

        final TreeItem<TreeNode<?>> treeItem = findItemForValue(getTreeView(), treeNode);
        if (treeItem == null) return;

        final ObservableList<TreeItem<TreeNode<?>>> items = treeItem.getChildren();
        items.clear();

        final TreeNode<?> element = treeItem.getValue();
        if (!element.hasChildren(this)) return;

        final Array<TreeNode<?>> children = element.getChildren(this);
        children.forEach(child -> items.add(new TreeItem<>(child)));

        items.forEach(modelNodeTreeItem -> fill(modelNodeTreeItem, true, -1));
    }

    /**
     * Update the node.
     *
     * @param treeNode the model node
     */
    @FXThread
    public void update(@NotNull final TreeNode<?> treeNode) {

        final TreeItem<TreeNode<?>> treeItem = findItemForValue(getTreeView(), treeNode);
        if (treeItem == null) return;

        treeItem.setValue(null);
        treeItem.setValue(treeNode);
    }

    /**
     * Get the context menu for the element.
     *
     * @param treeNode the model node
     * @return the context menu
     */
    @FXThread
    public ContextMenu getContextMenu(@NotNull final TreeNode<?> treeNode) {

        final C changeConsumer = getChangeConsumer();
        if (changeConsumer == null) return null;

        final ContextMenu contextMenu = new ContextMenu();
        final ObservableList<MenuItem> items = contextMenu.getItems();
        treeNode.fillContextMenu(this, items);
        if (items.isEmpty()) return null;

        return contextMenu;
    }

    /**
     * Notify about moving the element.
     *
     * @param prevParent the prev parent
     * @param newParent  the new parent
     * @param node       the node
     * @param index      the index
     */
    @FXThread
    public void notifyMoved(@NotNull final Object prevParent, @NotNull final Object newParent,
                            @NotNull final Object node, final int index) {
        notifyMoved(FACTORY_REGISTRY.createFor(prevParent), FACTORY_REGISTRY.createFor(newParent),
                FACTORY_REGISTRY.createFor(node), index);
    }

    /**
     * Notify about moving the element.
     */
    @FXThread
    private void notifyMoved(@Nullable final TreeNode<?> prevParent, @Nullable final TreeNode<?> newParent,
                             @Nullable final TreeNode<?> node, final int index) {

        final TreeView<TreeNode<?>> treeView = getTreeView();
        final TreeItem<TreeNode<?>> prevParentItem = findItemForValue(treeView, prevParent);
        final TreeItem<TreeNode<?>> newParentItem = findItemForValue(treeView, newParent);
        final TreeItem<TreeNode<?>> nodeItem = findItemForValue(treeView, node);

        if (prevParentItem == null || newParentItem == null || nodeItem == null) {
            return;
        }

        final TreeNode<?> prevParenTreeNode = prevParentItem.getValue();
        prevParenTreeNode.notifyChildPreRemove(node);
        prevParentItem.getChildren().remove(nodeItem);
        prevParenTreeNode.notifyChildRemoved(node);

        final TreeNode<?> newParentTreeNode = newParentItem.getValue();
        newParentTreeNode.notifyChildAdded(node);

        if (index >= 0) {
            newParentItem.getChildren().add(index, nodeItem);
        } else {
            newParentItem.getChildren().add(nodeItem);
        }

        newParentTreeNode.notifyChildAdded(node);

        EXECUTOR_MANAGER.addFXTask(() -> select(node.getElement()));
    }

    /**
     * Notify about changing the object.
     *
     * @param parent the parent
     * @param object the object
     */
    @FXThread
    public void notifyChanged(@Nullable Object parent, @NotNull final Object object) {

        final TreeItem<TreeNode<?>> treeItem = tryToFindItem(parent, object);
        if (treeItem == null) return;

        final TreeItem<TreeNode<?>> parentItem = treeItem.getParent();

        if (parentItem == null) {
            final TreeNode<?> node = treeItem.getValue();
            treeItem.setValue(null);
            treeItem.setValue(node);
            return;
        }

        final TreeNode<?> parentNode = parentItem.getValue();
        final TreeNode<?> node = treeItem.getValue();

        parentNode.notifyChildPreRemove(node);
        treeItem.setValue(null);
        parentNode.notifyChildRemoved(node);
        parentNode.notifyChildPreAdd(node);
        treeItem.setValue(node);
        parentNode.notifyChildAdded(node);
    }

    /**
     * Refresh all children of the parent.
     *
     * @param parent the parent.
     */
    @FXThread
    public void refreshChildren(@NotNull final Object parent) {

        final TreeView<TreeNode<?>> treeView = getTreeView();
        final TreeItem<TreeNode<?>> treeItem = findItemForValue(treeView, parent);
        if(treeItem == null) return;

        treeItem.getChildren().clear();

        fill(treeItem, false, -1);
    }

    /**
     * Notify about replacing the element.
     *
     * @param parent         the parent.
     * @param oldChild       the old child.
     * @param newChild       the new child.
     * @param needExpand     true if need to expand new node.
     * @param needDeepExpand true if need to expand new node deeply.
     */
    @FXThread
    public void notifyReplace(@Nullable final Object parent, @Nullable final Object oldChild,
                              @Nullable final Object newChild, final boolean needExpand, final boolean needDeepExpand) {

        final TreeView<TreeNode<?>> treeView = getTreeView();
        final TreeItem<TreeNode<?>> parentItem = findItemForValue(treeView, parent);

        if (parentItem == null) {
            if (newChild == null) return;
            final TreeItem<TreeNode<?>> childItem = new TreeItem<>(FACTORY_REGISTRY.createFor(newChild));
            fill(childItem, needDeepExpand, -1);
            childItem.setExpanded(needExpand);
            treeView.setRoot(childItem);
            return;
        }

        int index = 0;

        final TreeNode<?> parentNode = parentItem.getValue();
        final MultipleSelectionModel<TreeItem<TreeNode<?>>> selectionModel = treeView.getSelectionModel();
        final ObservableList<TreeItem<TreeNode<?>>> children = parentItem.getChildren();
        final TreeItem<TreeNode<?>> oldChildItem = oldChild == null ? null : findItemForValue(treeView, oldChild);
        final TreeItem<TreeNode<?>> selectedItem = selectionModel.getSelectedItem();

        final boolean needSelect = selectedItem == oldChildItem;

        if (oldChildItem != null) {
            final TreeNode<?> oldChildNode = oldChildItem.getValue();
            parentNode.notifyChildPreRemove(oldChildNode);
            index = children.indexOf(oldChildItem);
            children.remove(oldChildItem);
            parentNode.notifyChildRemoved(oldChildNode);
        }

        if (newChild == null) return;

        final TreeItem<TreeNode<?>> childItem = new TreeItem<>(FACTORY_REGISTRY.createFor(newChild));
        final TreeNode<?> newChildNode = childItem.getValue();
        fill(childItem, needExpand, -1);
        childItem.setExpanded(needExpand);

        parentNode.notifyChildPreAdd(newChildNode);
        children.add(index, childItem);
        parentNode.notifyChildAdded(newChildNode);

        if (needSelect) selectionModel.select(childItem);
    }

    /**
     * Notify about adding the element.
     *
     * @param parent the parent
     * @param child  the child
     * @param index  the index
     */
    @FXThread
    public void notifyAdded(@Nullable final Object parent, @Nullable final Object child, final int index) {
        if (child == null || parent == null) return;

        final TreeView<TreeNode<?>> treeView = getTreeView();
        final TreeItem<TreeNode<?>> parentItem = findItemForValue(treeView, parent);
        if (parentItem == null) return;

        final TreeNode<?> childNode = FACTORY_REGISTRY.createFor(child);
        if (childNode == null) return;

        final TreeNode<?> parentNode = parentItem.getValue();
        parentNode.notifyChildPreAdd(childNode);

        final TreeItem<TreeNode<?>> childItem = new TreeItem<>(childNode);

        final ObservableList<TreeItem<TreeNode<?>>> children = parentItem.getChildren();
        if (index == -1) children.add(childItem);
        else children.add(index, childItem);

        parentItem.setExpanded(true);
        parentNode.notifyChildAdded(childNode);

        fill(childItem, false, -1);
    }

    /**
     * Notify about removing the element.
     *
     * @param parent the parent
     * @param child  the child
     */
    @FXThread
    public void notifyRemoved(@Nullable final Object parent, @NotNull final Object child) {

        final TreeItem<TreeNode<?>> treeItem = tryToFindItem(parent, child);
        if (treeItem == null) return;

        final TreeItem<TreeNode<?>> parentItem = treeItem.getParent();
        final TreeNode<?> parentNode = parentItem.getValue();
        final TreeNode<?> node = treeItem.getValue();

        final ObservableList<TreeItem<TreeNode<?>>> children = parentItem.getChildren();
        parentNode.notifyChildPreRemove(node);
        children.remove(treeItem);
        parentNode.notifyChildRemoved(node);

        if (parentItem.isExpanded() && children.isEmpty()) {
            parentItem.setExpanded(false);
        }
    }

    /**
     * Try to find tree item for the object.
     *
     * @param parent the parent object.
     * @param child  the child object.
     * @return the tree item or null.
     */
    private @Nullable TreeItem<TreeNode<?>> tryToFindItem(@Nullable final Object parent, @NotNull final Object child) {

        final TreeView<TreeNode<?>> treeView = getTreeView();
        final TreeItem<TreeNode<?>> treeItem;

        if (parent != null) {
            final TreeItem<TreeNode<?>> parentItem = findItemForValue(treeView, parent);
            if (parentItem == null) return null;
            treeItem = findItemForValue(parentItem, child);
        } else {
            treeItem = findItemForValue(treeView, child);
        }

        return treeItem;
    }

    /**
     * Find a parent for the node.
     *
     * @param treeNode the model node
     * @return the parent or null.
     */
    @FXThread
    public @Nullable TreeNode<?> findParent(@NotNull final TreeNode<?> treeNode) {

        final TreeItem<TreeNode<?>> treeItem = findItemForValue(getTreeView(), treeNode);
        if (treeItem == null) return null;

        final TreeItem<TreeNode<?>> parent = treeItem.getParent();
        return parent == null ? null : parent.getValue();
    }

    /**
     * Start editing the element.
     *
     * @param treeNode the model node
     */
    @FXThread
    public void startEdit(@NotNull final TreeNode<?> treeNode) {

        final TreeView<TreeNode<?>> treeView = getTreeView();
        final TreeItem<TreeNode<?>> treeItem = findItemForValue(treeView, treeNode);
        if (treeItem == null) return;

        treeView.edit(treeItem);
    }

    /**
     * Select the object in the tree.
     *
     * @param object the object
     */
    @FXThread
    public void select(@Nullable final Object object) {

        final TreeView<TreeNode<?>> treeView = getTreeView();
        final MultipleSelectionModel<TreeItem<TreeNode<?>>> selectionModel = treeView.getSelectionModel();

        if (object == null) {
            selectionModel.select(null);
            return;
        }

        final TreeNode<Object> treeNode = FACTORY_REGISTRY.createFor(object);
        final TreeItem<TreeNode<?>> treeItem = findItemForValue(treeView, treeNode);

        if (treeItem == null) {
            selectionModel.select(null);
            return;
        }

        selectionModel.select(treeItem);
    }

    /**
     * Gets selected.
     *
     * @return the selected
     */
    @FXThread
    public @Nullable TreeNode<?> getSelected() {

        final TreeView<TreeNode<?>> treeView = getTreeView();
        final MultipleSelectionModel<TreeItem<TreeNode<?>>> selectionModel = treeView.getSelectionModel();
        final TreeItem<TreeNode<?>> selectedItem = selectionModel.getSelectedItem();

        if (selectedItem == null) {
            return null;
        }

        return selectedItem.getValue();
    }

    /**
     * Gets selected object.
     *
     * @return the selected object
     */
    @FXThread
    public @Nullable Object getSelectedObject() {

        final TreeView<TreeNode<?>> treeView = getTreeView();
        final MultipleSelectionModel<TreeItem<TreeNode<?>>> selectionModel = treeView.getSelectionModel();
        final TreeItem<TreeNode<?>> selectedItem = selectionModel.getSelectedItem();

        if (selectedItem == null) {
            return null;
        }

        final TreeNode<?> treeNode = selectedItem.getValue();
        return treeNode.getElement();
    }

    /**
     * Gets change consumer.
     *
     * @return the consumer of changes of the model.
     */
    @FXThread
    public @Nullable C getChangeConsumer() {
        return changeConsumer;
    }
}
