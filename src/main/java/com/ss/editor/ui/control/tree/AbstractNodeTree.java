package com.ss.editor.ui.control.tree;

import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;
import static com.ss.editor.ui.util.UIUtils.findItemForValue;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.tree.node.ModelNode;
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
public abstract class AbstractNodeTree<C extends ChangeConsumer> extends VBox {

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

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
    private TreeView<ModelNode<?>> treeView;

    /**
     * Instantiates a new Abstract node tree.
     *
     * @param selectionHandler the selection handler
     * @param consumer         the consumer
     */
    public AbstractNodeTree(@NotNull final Consumer<Object> selectionHandler, @Nullable final C consumer) {
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

        final MultipleSelectionModel<TreeItem<ModelNode<?>>> selectionModel = treeView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> processSelect(newValue));

        FXUtils.addToPane(treeView, this);
    }

    /**
     * Create node tree cell abstract node tree cell.
     *
     * @return the abstract node tree cell
     */
    @NotNull
    protected AbstractNodeTreeCell<C, ?> createNodeTreeCell() {
        throw new UnsupportedOperationException();
    }

    /**
     * Select the item.
     */
    private void processSelect(@Nullable final TreeItem<ModelNode<?>> treeItem) {

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
    @NotNull
    public TreeView<ModelNode<?>> getTreeView() {
        return notNull(treeView);
    }

    /**
     * Fill the tree for the object.
     *
     * @param object the object.
     */
    @FXThread
    public void fill(@NotNull final Object object) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> currentRoot = treeView.getRoot();

        if (currentRoot != null) {
            treeView.setRoot(null);
        }

        final ModelNode<?> rootElement = createFor(object);
        final TreeItem<ModelNode<?>> newRoot = new TreeItem<>(rootElement);
        newRoot.setExpanded(true);

        fill(newRoot, false, 1);

        treeView.setRoot(newRoot);
    }

    /**
     * Fill the item.
     */
    @FXThread
    private void fill(@NotNull final TreeItem<ModelNode<?>> treeItem, final boolean expanded, final int level) {
        treeItem.setExpanded(expanded || level == 1);

        final ModelNode<?> element = treeItem.getValue();
        if (!element.hasChildren(this)) return;

        final ObservableList<TreeItem<ModelNode<?>>> items = treeItem.getChildren();

        final Array<ModelNode<?>> children = element.getChildren(this);
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
     * @param modelNode the model node
     */
    @FXThread
    public void refresh(@NotNull final ModelNode<?> modelNode) {

        final TreeItem<ModelNode<?>> treeItem = findItemForValue(getTreeView(), modelNode);
        if (treeItem == null) return;

        final ObservableList<TreeItem<ModelNode<?>>> items = treeItem.getChildren();
        items.clear();

        final ModelNode<?> element = treeItem.getValue();
        if (!element.hasChildren(this)) return;

        final Array<ModelNode<?>> children = element.getChildren(this);
        children.forEach(child -> items.add(new TreeItem<>(child)));

        items.forEach(modelNodeTreeItem -> fill(modelNodeTreeItem, true, -1));
    }

    /**
     * Update the node.
     *
     * @param modelNode the model node
     */
    @FXThread
    public void update(@NotNull final ModelNode<?> modelNode) {

        final TreeItem<ModelNode<?>> treeItem = findItemForValue(getTreeView(), modelNode);
        if (treeItem == null) return;

        treeItem.setValue(null);
        treeItem.setValue(modelNode);
    }

    /**
     * Get the context menu for the element.
     *
     * @param modelNode the model node
     * @return the context menu
     */
    @FXThread
    ContextMenu getContextMenu(@NotNull final ModelNode<?> modelNode) {

        final C changeConsumer = getChangeConsumer();
        if (changeConsumer == null) return null;

        final ContextMenu contextMenu = new ContextMenu();
        final ObservableList<MenuItem> items = contextMenu.getItems();
        modelNode.fillContextMenu(this, items);
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
        notifyMoved(createFor(prevParent), createFor(newParent), createFor(node), index);
    }

    /**
     * Notify about moving the element.
     */
    @FXThread
    private void notifyMoved(@Nullable final ModelNode<?> prevParent, @Nullable final ModelNode<?> newParent,
                             @Nullable final ModelNode<?> node, final int index) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> prevParentItem = findItemForValue(treeView, prevParent);
        final TreeItem<ModelNode<?>> newParentItem = findItemForValue(treeView, newParent);
        final TreeItem<ModelNode<?>> nodeItem = findItemForValue(treeView, node);

        if (prevParentItem == null || newParentItem == null || nodeItem == null) {
            return;
        }

        final ModelNode<?> prevParenModelNode = prevParentItem.getValue();
        prevParenModelNode.notifyChildPreRemove(node);
        prevParentItem.getChildren().remove(nodeItem);
        prevParenModelNode.notifyChildRemoved(node);

        final ModelNode<?> newParentModelNode = newParentItem.getValue();
        newParentModelNode.notifyChildAdded(node);
        newParentItem.getChildren().add(index, nodeItem);
        newParentModelNode.notifyChildAdded(node);

        EXECUTOR_MANAGER.addFXTask(() -> select(node.getElement()));
    }

    /**
     * Notify about changing the element.
     *
     * @param parent the parent
     * @param object the object
     */
    @FXThread
    public void notifyChanged(@Nullable Object parent, @NotNull final Object object) {
        notifyChanged(createFor(object));
    }

    /**
     * Notify about changed the element.
     */
    @FXThread
    private void notifyChanged(@Nullable final ModelNode<?> modelNode) {
        if (modelNode == null) return;

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> treeItem = findItemForValue(treeView, modelNode);
        if (treeItem == null) return;

        final TreeItem<ModelNode<?>> parentItem = treeItem.getParent();
        if (parentItem == null) {
            treeItem.setValue(null);
            treeItem.setValue(modelNode);
            return;
        }

        final ModelNode<?> parent = parentItem.getValue();
        final ModelNode<?> old = treeItem.getValue();

        if (modelNode.isNeedToSaveName()) {
            modelNode.setName(old.getName());
        }

        parent.notifyChildPreRemove(old);
        treeItem.setValue(null);
        parent.notifyChildRemoved(old);
        parent.notifyChildPreAdd(modelNode);
        treeItem.setValue(modelNode);
        parent.notifyChildAdded(modelNode);
    }

    /**
     * Notify about replacing the element.
     *
     * @param parent   the parent
     * @param oldChild the old child
     * @param newChild the new child
     */
    @FXThread
    public void notifyReplace(@Nullable final Object parent, @Nullable final Object oldChild,
                              @Nullable final Object newChild) {
        notifyReplace(createFor(parent), createFor(oldChild), createFor(newChild));
    }

    /**
     * Notify about replacing the element.
     */
    @FXThread
    private void notifyReplace(@Nullable final ModelNode<?> parent, @Nullable final ModelNode<?> oldChild,
                               @Nullable final ModelNode<?> newChild) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> parentItem = findItemForValue(treeView, parent);

        if (parentItem == null) {
            if (newChild == null) return;
            final TreeItem<ModelNode<?>> childItem = new TreeItem<>(newChild);
            childItem.setExpanded(true);
            fill(childItem, true, -1);
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
            parent.notifyChildPreRemove(oldChild);
            index = children.indexOf(oldChildItem);
            needExpand = oldChildItem.isExpanded();
            children.remove(oldChildItem);
            parent.notifyChildRemoved(oldChild);
        }

        if (newChild == null) return;

        final TreeItem<ModelNode<?>> childItem = new TreeItem<>(newChild);
        childItem.setExpanded(needExpand);

        fill(childItem, true, -1);

        parent.notifyChildPreAdd(newChild);
        children.add(index, childItem);
        parent.notifyChildAdded(newChild);

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
        notifyAdded(createFor(parent), createFor(child), index);
    }

    /**
     * Notify about adding the element.
     */
    @FXThread
    private void notifyAdded(@Nullable final ModelNode<?> parent, @Nullable final ModelNode<?> child, final int index) {
        if (child == null) return;

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> parentItem = findItemForValue(treeView, parent);
        if (parentItem == null) return;

        parent.notifyChildPreAdd(child);

        final TreeItem<ModelNode<?>> childItem = new TreeItem<>(child);

        final ObservableList<TreeItem<ModelNode<?>>> children = parentItem.getChildren();
        if (index == -1) children.add(childItem);
        else children.add(index, childItem);

        parentItem.setExpanded(true);
        parent.notifyChildAdded(child);

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
        notifyRemoved(createFor(child));
    }

    /**
     * Notify about removing the element.
     */
    @FXThread
    private void notifyRemoved(@Nullable final ModelNode<?> modelNode) {
        if (modelNode == null) return;

        final TreeItem<ModelNode<?>> treeItem = findItemForValue(getTreeView(), modelNode);
        if (treeItem == null) return;

        final TreeItem<ModelNode<?>> parentItem = treeItem.getParent();
        final ModelNode<?> parentModelNode = parentItem.getValue();

        final ObservableList<TreeItem<ModelNode<?>>> children = parentItem.getChildren();
        parentModelNode.notifyChildPreRemove(modelNode);
        children.remove(treeItem);
        parentModelNode.notifyChildRemoved(modelNode);

        if (parentItem.isExpanded() && children.isEmpty()) {
            parentItem.setExpanded(false);
        }
    }

    /**
     * Find a parent for the node.
     *
     * @param modelNode the model node
     * @return the parent or null.
     */
    @Nullable
    @FXThread
    public ModelNode<?> findParent(@NotNull final ModelNode<?> modelNode) {

        final TreeItem<ModelNode<?>> treeItem = findItemForValue(getTreeView(), modelNode);
        if (treeItem == null) return null;

        final TreeItem<ModelNode<?>> parent = treeItem.getParent();
        return parent == null ? null : parent.getValue();
    }

    /**
     * Start editing the element.
     *
     * @param modelNode the model node
     */
    @FXThread
    public void startEdit(@NotNull final ModelNode<?> modelNode) {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> treeItem = findItemForValue(treeView, modelNode);
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
     * Gets selected.
     *
     * @return the selected
     */
    @Nullable
    @FXThread
    public ModelNode<?> getSelected() {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final MultipleSelectionModel<TreeItem<ModelNode<?>>> selectionModel = treeView.getSelectionModel();
        final TreeItem<ModelNode<?>> selectedItem = selectionModel.getSelectedItem();

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
    @Nullable
    @FXThread
    public Object getSelectedObject() {

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final MultipleSelectionModel<TreeItem<ModelNode<?>>> selectionModel = treeView.getSelectionModel();
        final TreeItem<ModelNode<?>> selectedItem = selectionModel.getSelectedItem();

        if (selectedItem == null) {
            return null;
        }

        final ModelNode<?> modelNode = selectedItem.getValue();
        return modelNode.getElement();
    }

    /**
     * Gets change consumer.
     *
     * @return the consumer of changes of the model.
     */
    @Nullable
    @FXThread
    public C getChangeConsumer() {
        return changeConsumer;
    }
}
