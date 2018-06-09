package com.ss.editor.ui.control.tree;

import static com.ss.editor.ui.util.UiUtils.findItemForValue;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.factory.TreeNodeFactoryRegistry;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayCollectors;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * The implementation of {@link TreeView} to present some structure.
 *
 * @param <C> the type parameter
 * @author JavaSaBr
 */
public class NodeTree<C extends ChangeConsumer> extends VBox {

    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    protected static final TreeNodeFactoryRegistry FACTORY_REGISTRY = TreeNodeFactoryRegistry.getInstance();

    @FunctionalInterface
    public interface MultiItemActionFiller {

        @FxThread
        void fill(
                @NotNull NodeTree<?> nodeTree,
                @NotNull List<MenuItem> menuItems,
                @NotNull Array<TreeNode<?>> treeNodes
        );
    }
    /**
     * The list of action fillers.
     */
    private static final Array<MultiItemActionFiller> MULTI_ITEMS_ACTION_FILLERS =
            Array.ofType(MultiItemActionFiller.class);

    /**
     * Register the new multi items action filler.
     *
     * @param actionFiller the new multi items action filler.
     */
    @FxThread
    public static void register(@NotNull MultiItemActionFiller actionFiller) {
        MULTI_ITEMS_ACTION_FILLERS.add(actionFiller);
    }

    /**
     * The handler of selected objects.
     */
    @NotNull
    private final Consumer<Array<Object>> selectionHandler;

    /**
     * The consumer of changes of the model.
     */
    @Nullable
    private final C changeConsumer;

    /**
     * The selection mode.
     */
    @NotNull
    private final SelectionMode selectionMode;

    /**
     * The tree with structure of the model.
     */
    @Nullable
    private TreeView<TreeNode<?>> treeView;

    public NodeTree(@NotNull Consumer<Array<Object>> selectionHandler, @Nullable C consumer) {
        this(selectionHandler, consumer, SelectionMode.SINGLE);
    }

    public NodeTree(
            @NotNull Consumer<Array<Object>> selectionHandler,
            @Nullable C consumer,
            @NotNull SelectionMode selectionMode
    ) {
        this.selectionHandler = selectionHandler;
        this.changeConsumer = consumer;
        this.selectionMode = selectionMode;
        createComponents();
        FxUtils.addClass(this, CssClasses.ABSTRACT_NODE_TREE_CONTAINER);
    }

    /**
     * Create components of this component.
     */
    @FxThread
    protected void createComponents() {

        treeView = new TreeView<>();
        treeView.setCellFactory(param -> createNodeTreeCell());
        treeView.setShowRoot(true);
        treeView.setEditable(true);
        treeView.setFocusTraversable(true);
        treeView.prefHeightProperty().bind(heightProperty());
        treeView.prefWidthProperty().bind(widthProperty());

        var selectionModel = treeView.getSelectionModel();
        selectionModel.setSelectionMode(selectionMode);

        FxControlUtils.onSelectedItemChange(treeView, treeNodeTreeItem -> updateSelection());

        FxUtils.addChild(this, treeView);
    }

    /**
     * Create node tree cell abstract node tree cell.
     *
     * @return the abstract node tree cell
     */
    @FxThread
    protected @NotNull NodeTreeCell<C, ?> createNodeTreeCell() {
        return new NodeTreeCell<>(this);
    }

    /**
     * Select the item.
     */
    @FxThread
    private void updateSelection() {

        var objects = getTreeView()
                .getSelectionModel()
                .getSelectedItems()
                .stream()
                .map(TreeItem::getValue)
                .collect(ArrayCollectors.<Object>toArray(Object.class));

        selectionHandler.accept(objects);
    }

    /**
     * Get the tree view.
     *
     * @return the tree of this model.
     */
    @FxThread
    public @NotNull TreeView<TreeNode<?>> getTreeView() {
        return notNull(treeView);
    }

    /**
     * Fill the tree for the object.
     *
     * @param object the object.
     */
    @FxThread
    public void fill(@NotNull Object object) {

        var treeView = getTreeView();
        var currentRoot = treeView.getRoot();

        if (currentRoot != null) {
            treeView.setRoot(null);
        }

        var rootElement = FACTORY_REGISTRY.createFor(object);
        var newRoot = new TreeItem<TreeNode<?>>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot, false, 1);

        treeView.setRoot(newRoot);
    }

    /**
     * Fill the item.
     */
    @FxThread
    private void fill(@NotNull TreeItem<TreeNode<?>> treeItem, boolean expanded, int level) {
        treeItem.setExpanded(expanded || level == 1);

        var element = treeItem.getValue();
        if (!element.hasChildren(this)) {
            return;
        }

        var items = treeItem.getChildren();
        var children = element.getChildren(this);
        children.forEach(child -> {
            element.notifyChildPreAdd(child);
            items.add(new TreeItem<>(child));
            element.notifyChildAdded(child);
        });

        items.forEach(item -> fill(item, expanded, level == -1 ? -1 : level + 1));
    }

    /**
     * Expand this tree to the level.
     *
     * @param level the level.
     */
    @FxThread
    public void expandToLevel(int level) {
        expandToLevel(getTreeView().getRoot(), 0, level);
    }

    /**
     * Expand this tree to the level.
     *
     * @param item         the current item.
     * @param currentLevel the current level.
     * @param level        the level.
     */
    @FxThread
    private void expandToLevel(TreeItem<TreeNode<?>> item, int currentLevel, int level) {
        item.setExpanded(currentLevel <= level);
        item.getChildren().forEach(child -> expandToLevel(child, currentLevel + 1, level));
    }

    /**
     * Refresh the object in this tree.
     *
     * @param object the object.
     */
    @FxThread
    public void refresh(@NotNull Object object) {

        var treeItem = findItemForValue(getTreeView(), object);
        if (treeItem == null) {
            return;
        }

        var treeNode = treeItem.getValue();
        var items = treeItem.getChildren();
        items.clear();

        var expanded = treeItem.isExpanded();
        var selected = getSelected();

        var element = treeItem.getValue();
        if (!element.hasChildren(this)) {
            return;
        }

        var children = element.getChildren(this);
        children.forEach(child -> items.add(new TreeItem<>(child)));

        items.forEach(modelNodeTreeItem -> fill(modelNodeTreeItem, false, -1));
        treeItem.setExpanded(expanded);

        if (selected == treeNode) {
            selectSingle(treeNode);
        }
    }

    /**
     * Update the node.
     *
     * @param treeNode the model node
     */
    @FxThread
    public void update(@NotNull TreeNode<?> treeNode) {

        var treeItem = findItemForValue(getTreeView(), treeNode);
        if (treeItem == null) {
            return;
        }

        treeItem.setValue(null);
        treeItem.setValue(treeNode);
    }

    /**
     * Get the context menu for the element.
     *
     * @param requestedNode the requested node.
     * @return the context menu.
     */
    @FxThread
    public ContextMenu getContextMenu(@Nullable TreeNode<?> requestedNode) {

        var changeConsumer = getChangeConsumer();
        if (changeConsumer == null) {
            return null;
        }

        var selectedItems = getTreeView()
                .getSelectionModel()
                .getSelectedItems();

        if (selectedItems.isEmpty()) {
            return null;
        }

        var contextMenu = new ContextMenu();
        var items = contextMenu.getItems();

        if (selectedItems.size() == 1 && requestedNode != null) {

            requestedNode.fillContextMenu(this, items);

            if (items.isEmpty()) {
                return null;
            }

            requestedNode.handleResultContextMenu(this, items);

        } else {

            var treeNodes = selectedItems.stream()
                    .map(TreeItem::getValue)
                    .collect(ArrayCollectors.<TreeNode<?>>toArray(TreeNode.class));

            MULTI_ITEMS_ACTION_FILLERS.forEach(filler -> filler.fill(this, items, treeNodes));
        }

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
    @FxThread
    public void notifyMoved(@NotNull Object prevParent, @NotNull Object newParent, @NotNull Object node, int index) {
        notifyMoved(
                FACTORY_REGISTRY.createFor(prevParent),
                FACTORY_REGISTRY.createFor(newParent),
                FACTORY_REGISTRY.createFor(node),
                index
        );
    }

    /**
     * Notify about moving the element.
     */
    @FxThread
    private void notifyMoved(
            @Nullable TreeNode<?> prevParent,
            @Nullable TreeNode<?> newParent,
            @Nullable TreeNode<?> node,
            int index
    ) {

        var treeView = getTreeView();
        var prevParentItem = findItemForValue(treeView, prevParent);
        var newParentItem = findItemForValue(treeView, newParent);
        var nodeItem = findItemForValue(treeView, node);

        if (prevParentItem == null || newParentItem == null || nodeItem == null) {
            return;
        }

        var prevParenTreeNode = prevParentItem.getValue();
        prevParenTreeNode.notifyChildPreRemove(node);
        prevParentItem.getChildren().remove(nodeItem);
        prevParenTreeNode.notifyChildRemoved(node);

        var newParentTreeNode = newParentItem.getValue();
        newParentTreeNode.notifyChildAdded(node);

        if (index >= 0) {
            newParentItem.getChildren().add(index, nodeItem);
        } else {
            newParentItem.getChildren().add(nodeItem);
        }

        newParentTreeNode.notifyChildAdded(node);

        EXECUTOR_MANAGER.addFxTask(() -> selectSingle(node.getElement()));
    }

    /**
     * Notify about changing the object.
     *
     * @param parent the parent
     * @param object the object
     */
    @FxThread
    public void notifyChanged(@Nullable Object parent, @NotNull final Object object) {

        final TreeItem<TreeNode<?>> treeItem = tryToFindItem(parent, object);
        if (treeItem == null) {
            return;
        }

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
     * Notify about replacing the element.
     *
     * @param parent         the parent.
     * @param oldChild       the old child.
     * @param newChild       the new child.
     * @param needExpand     true if need to expand new node.
     * @param needDeepExpand true if need to expand new node deeply.
     */
    @FxThread
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

        if (newChild == null) {
            return;
        }

        final TreeItem<TreeNode<?>> childItem = new TreeItem<>(FACTORY_REGISTRY.createFor(newChild));
        final TreeNode<?> newChildNode = childItem.getValue();
        fill(childItem, needExpand, -1);
        childItem.setExpanded(needExpand);

        parentNode.notifyChildPreAdd(newChildNode);
        children.add(index, childItem);
        parentNode.notifyChildAdded(newChildNode);

        if (needSelect) {
            selectionModel.select(childItem);
        }
    }

    /**
     * Notify about adding the element.
     *
     * @param parent the parent.
     * @param child  the child.
     * @param index  the index.
     */
    @FxThread
    public void notifyAdded(@Nullable final Object parent, @Nullable final Object child, final int index) {

        if (child == null || parent == null) {
            return;
        }

        final TreeView<TreeNode<?>> treeView = getTreeView();
        final TreeItem<TreeNode<?>> parentItem = findItemForValue(treeView, parent);
        if (parentItem == null || findItemForValue(parentItem, child) != null) {
            return;
        }

        final TreeNode<?> childNode = FACTORY_REGISTRY.createFor(child);
        if (childNode == null) {
            return;
        }

        final TreeNode<?> parentNode = parentItem.getValue();
        parentNode.notifyChildPreAdd(childNode);

        final TreeItem<TreeNode<?>> childItem = new TreeItem<>(childNode);
        final ObservableList<TreeItem<TreeNode<?>>> children = parentItem.getChildren();

        if (index == -1) {
            children.add(childItem);
        } else {
            children.add(index, childItem);
        }

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
    @FxThread
    public void notifyRemoved(@Nullable final Object parent, @NotNull final Object child) {

        final TreeItem<TreeNode<?>> treeItem = tryToFindItem(parent, child);
        if (treeItem == null) {
            return;
        }

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
    @FxThread
    public @Nullable TreeNode<?> findParent(@NotNull final TreeNode<?> treeNode) {

        final TreeItem<TreeNode<?>> treeItem = findItemForValue(getTreeView(), treeNode);
        if (treeItem == null) {
            return null;
        }

        final TreeItem<TreeNode<?>> parent = treeItem.getParent();
        return parent == null ? null : parent.getValue();
    }

    /**
     * Start editing the element.
     *
     * @param treeNode the model node
     */
    @FxThread
    public void startEdit(@NotNull final TreeNode<?> treeNode) {

        final TreeView<TreeNode<?>> treeView = getTreeView();
        final TreeItem<TreeNode<?>> treeItem = findItemForValue(treeView, treeNode);
        if (treeItem == null) {
            return;
        }

        treeView.edit(treeItem);
    }

    /**
     * Select the object in the tree.
     *
     * @param object the object.
     */
    @FxThread
    public void selectSingle(@Nullable final Object object) {

        final TreeView<TreeNode<?>> treeView = getTreeView();
        final MultipleSelectionModel<TreeItem<TreeNode<?>>> selectionModel = treeView.getSelectionModel();

        if (object == null) {
            selectionModel.clearSelection();
            return;
        }

        final TreeNode<Object> treeNode = FACTORY_REGISTRY.createFor(object);
        final TreeItem<TreeNode<?>> treeItem = findItemForValue(treeView, treeNode);

        if (treeItem == null) {
            selectionModel.clearSelection();
            return;
        }

        selectionModel.clearSelection();
        selectionModel.select(treeItem);
    }

    /**
     * Select the objects in the tree.
     *
     * @param objects the objects.
     */
    @FxThread
    public void selects(@NotNull final Array<?> objects) {

        final TreeView<TreeNode<?>> treeView = getTreeView();
        final MultipleSelectionModel<TreeItem<TreeNode<?>>> selectionModel = treeView.getSelectionModel();
        selectionModel.clearSelection();

        objects.stream().map(FACTORY_REGISTRY::createFor)
            .filter(Objects::nonNull)
            .map(node -> findItemForValue(treeView, node))
            .filter(Objects::nonNull)
            .forEach(selectionModel::select);
    }

    /**
     * Get the selected item.
     *
     * @return the selected item or null.
     */
    @FxThread
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
     * Get the current selected items count.
     *
     * @return the current selected items count.
     */
    @FxThread
    public int getSelectedCount() {
        final TreeView<TreeNode<?>> treeView = getTreeView();
        return treeView.getSelectionModel()
            .getSelectedItems().size();
    }

    /**
     * Get the selected nodes.
     *
     * @return the selected nodes.
     */
    @FxThread
    public @NotNull Array<TreeNode<?>> getSelectedItems() {
        final TreeView<TreeNode<?>> treeView = getTreeView();
        return treeView.getSelectionModel()
                .getSelectedItems()
                .stream()
                .map(TreeItem::getValue)
                .collect(ArrayCollectors.toArray(TreeNode.class));
    }

    /**
     * Gets selected object.
     *
     * @return the selected object
     */
    @FxThread
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
    @FxThread
    public @Nullable C getChangeConsumer() {
        return changeConsumer;
    }

    /**
     * Require a change consumer.
     *
     * @return the change consumer.
     */
    @FxThread
    public @NotNull C requireChangeConsumer() {
        return notNull(changeConsumer);
    }

    /**
     * Get an option of a change consumer.
     *
     * @return the change consumer.
     */
    @FxThread
    public @NotNull Optional<C> getChangeConsumerOpt() {
        return Optional.ofNullable(changeConsumer);
    }
}
