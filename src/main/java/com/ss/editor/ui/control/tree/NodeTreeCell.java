package com.ss.editor.ui.control.tree;

import static com.ss.editor.ui.util.UIUtils.findItem;
import static com.ss.editor.ui.util.UIUtils.findItemForValue;
import static com.ss.rlib.util.ClassUtils.unsafeCast;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.HideableNode;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The implementation of {@link TreeCell} to show tree nodes.
 *
 * @param <C> the type of a {@link ChangeConsumer}
 * @param <M> the type parameter
 * @author JavaSaBr
 */
public abstract class NodeTreeCell<C extends ChangeConsumer, M extends NodeTree<C>> extends TextFieldTreeCell<TreeNode<?>> {

    @NotNull
    private static final PseudoClass DROP_AVAILABLE_PSEUDO_CLASS = PseudoClass.getPseudoClass("drop-available");

    @NotNull
    private static final PseudoClass DRAGGED_PSEUDO_CLASS = PseudoClass.getPseudoClass("dragged");

    @NotNull
    private static final PseudoClass EDITING_PSEUDO_CLASS = PseudoClass.getPseudoClass("editing");

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    @NotNull
    private static final DataFormat DATA_FORMAT = new DataFormat(NodeTreeCell.class.getName());

    @NotNull
    private final StringConverter<TreeNode<?>> stringConverter = new StringConverter<TreeNode<?>>() {

        @Override
        public String toString(@NotNull final TreeNode<?> object) {
            return object.getName();
        }

        @Override
        public TreeNode<?> fromString(@NotNull final String string) {

            final TreeNode<?> item = getItem();
            if (item == null) return null;

            item.changeName(getNodeTree(), string);

            return item;
        }
    };

    /**
     * The dragged state.
     */
    @NotNull
    private final BooleanProperty dragged = new BooleanPropertyBase(false) {
        public void invalidated() {
            pseudoClassStateChanged(DRAGGED_PSEUDO_CLASS, get());
        }

        @Override
        public Object getBean() {
            return NodeTreeCell.this;
        }

        @Override
        public String getName() {
            return "dragged";
        }
    };

    /**
     * The drop available state.
     */
    @NotNull
    private final BooleanProperty dropAvailable = new BooleanPropertyBase(false) {
        public void invalidated() {
            pseudoClassStateChanged(DROP_AVAILABLE_PSEUDO_CLASS, get());
        }

        @Override
        public Object getBean() {
            return NodeTreeCell.this;
        }

        @Override
        public String getName() {
            return "drop available";
        }
    };

    /**
     * The editing state.
     */
    @NotNull
    private final BooleanProperty editing = new BooleanPropertyBase(false) {
        public void invalidated() {
            pseudoClassStateChanged(EDITING_PSEUDO_CLASS, get());
        }

        @Override
        public Object getBean() {
            return NodeTreeCell.this;
        }

        @Override
        public String getName() {
            return "editing";
        }
    };

    /**
     * The tree.
     */
    @NotNull
    private final M nodeTree;

    /**
     * The icon of node.
     */
    @NotNull
    private final ImageView icon;

    /**
     * The content box.
     */
    @NotNull
    private final HBox content;

    /**
     * The label of this cell.
     */
    @NotNull
    private final Label text;

    /**
     * The visible icon.
     */
    @NotNull
    private final ImageView visibleIcon;

    /**
     * The flag of ignoring updates.
     */
    private boolean ignoreUpdate;

    /**
     * Instantiates a new Abstract node tree cell.
     *
     * @param nodeTree the node tree
     */
    public NodeTreeCell(@NotNull final M nodeTree) {
        this.nodeTree = nodeTree;
        this.icon = new ImageView();
        this.content = new HBox();
        this.text = new Label();
        this.visibleIcon = new ImageView();
        this.visibleIcon.addEventFilter(MouseEvent.MOUSE_RELEASED, this::processHide);
        this.visibleIcon.setOnMouseReleased(this::processHide);
        this.visibleIcon.setPickOnBounds(true);

        setOnMouseClicked(this::processClick);
        setOnDragDetected(this::startDrag);
        setOnDragDone(this::stopDrag);
        setOnDragOver(this::dragOver);
        setOnDragDropped(this::dragDropped);
        setOnDragExited(this::dragExited);
        setOnKeyReleased(event -> {
            if (isEditing()) event.consume();
        });

        FXUtils.addToPane(icon, content);
        FXUtils.addToPane(visibleIcon, content);
        FXUtils.addToPane(text, content);

        setConverter(stringConverter);

        FXUtils.addClassTo(content, CSSClasses.DEF_HBOX);
        FXUtils.addClassTo(this, CSSClasses.ABSTRACT_NODE_TREE_CELL);
    }

    /**
     * Update hide status.
     */
    private void processHide(@NotNull final MouseEvent event) {
        event.consume();

        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        final TreeNode<?> item = getItem();
        if (!(item instanceof HideableNode)) return;

        final HideableNode<C> hideable = unsafeCast(item);

        if (hideable.isHided()) {
            hideable.show(getNodeTree());
        } else {
            hideable.hide(getNodeTree());
        }
    }

    @Override
    public void startEdit() {
        if (!isEditable()) return;

        final TreeItem<TreeNode<?>> treeItem = getTreeItem();
        if (treeItem != null) treeItem.setGraphic(null);

        setIgnoreUpdate(true);
        try {
            super.startEdit();
        } finally {
            setIgnoreUpdate(false);
        }

        UIUtils.updateEditedCell(this);
        editing.setValue(true);
    }

    /**
     * @return true if need to ignore update.
     */
    private boolean isIgnoreUpdate() {
        return ignoreUpdate;
    }

    /**
     * @param ignoreUpdate the flag of ignoring updates.
     */
    private void setIgnoreUpdate(final boolean ignoreUpdate) {
        this.ignoreUpdate = ignoreUpdate;
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        editing.setValue(false);
        final TreeItem<TreeNode<?>> treeItem = getTreeItem();
        if (treeItem != null) treeItem.setGraphic(content);
        setText(StringUtils.EMPTY);
    }

    @Override
    public void commitEdit(@NotNull final TreeNode<?> newValue) {
        super.commitEdit(newValue);
        editing.setValue(false);
        final TreeItem<TreeNode<?>> treeItem = getTreeItem();
        if (treeItem != null) treeItem.setGraphic(content);
        setText(StringUtils.EMPTY);
    }

    /**
     * @return the icon of node.
     */
    @NotNull
    private ImageView getIcon() {
        return icon;
    }

    @Override
    public void updateItem(@Nullable final TreeNode<?> item, final boolean empty) {
        super.updateItem(item, empty);
        if (isIgnoreUpdate()) return;

        final ImageView icon = getIcon();

        if (item == null) {
            final TreeItem<TreeNode<?>> treeItem = getTreeItem();
            if (treeItem != null) treeItem.setGraphic(null);
            setText(StringUtils.EMPTY);
            setEditable(false);
            return;
        }

        icon.setImage(item.getIcon());

        DynamicIconSupport.updateListener(this, icon, selectedProperty());

        final TreeItem<TreeNode<?>> treeItem = getTreeItem();
        if (treeItem != null) treeItem.setGraphic(content);

        HideableNode hideable = null;

        if (item instanceof HideableNode) {
            hideable = (HideableNode) item;
        }

        if (hideable != null) {
            visibleIcon.setVisible(true);
            visibleIcon.setManaged(true);
            visibleIcon.setImage(hideable.isHided() ? Icons.INVISIBLE_16 : Icons.VISIBLE_16);
            visibleIcon.setOpacity(hideable.isHided() ? 0.5D : 1D);
            DynamicIconSupport.updateListener2(this, visibleIcon, selectedProperty());
        } else {
            visibleIcon.setVisible(false);
            visibleIcon.setManaged(false);
        }

        text.setText(item.getName());

        setText(StringUtils.EMPTY);
        setEditable(item.canEditName());
    }

    /**
     * Gets node tree.
     *
     * @return the tree.
     */
    @NotNull
    protected M getNodeTree() {
        return nodeTree;
    }

    /**
     * Handle a mouse click.
     */
    private void processClick(@NotNull final MouseEvent event) {

        final TreeNode<?> item = getItem();
        if (item == null) return;

        final MouseButton button = event.getButton();
        if (button != MouseButton.SECONDARY) return;

        final M nodeTree = getNodeTree();
        final ContextMenu contextMenu = nodeTree.getContextMenu(item);
        if (contextMenu == null) return;

        EXECUTOR_MANAGER.addFXTask(() -> contextMenu.show(this, Side.BOTTOM, 0, 0));
    }

    /**
     * Handle stopping dragging.
     */
    private void stopDrag(@NotNull final DragEvent event) {
        dragged.setValue(false);
        setCursor(Cursor.DEFAULT);
        event.consume();
    }

    /**
     * Handle starting dragging.
     */
    private void startDrag(@NotNull final MouseEvent mouseEvent) {

        final TreeNode<?> item = getItem();
        if (item == null) return;

        final TreeView<TreeNode<?>> treeView = getTreeView();
        final TreeItem<TreeNode<?>> treeItem = findItemForValue(treeView, item);
        if (treeView.getRoot() == treeItem) return;

        TransferMode transferMode = item.canMove() ? TransferMode.MOVE : null;
        transferMode = item.canCopy() && mouseEvent.isControlDown() ? TransferMode.COPY : transferMode;
        if (transferMode == null) return;

        final Dragboard dragBoard = startDragAndDrop(transferMode);
        final ClipboardContent content = new ClipboardContent();
        content.put(DATA_FORMAT, item.getObjectId());

        dragBoard.setContent(content);

        dragged.setValue(true);
        setCursor(Cursor.MOVE);

        mouseEvent.consume();
    }

    /**
     * Handle dropping a dragged element.
     */
    private void dragDropped(@NotNull final DragEvent dragEvent) {

        final TreeNode<?> item = getItem();
        if (item == null) return;

        final Dragboard dragboard = dragEvent.getDragboard();
        final Long objectId = (Long) dragboard.getContent(DATA_FORMAT);

        final Set<TransferMode> transferModes = dragboard.getTransferModes();
        final boolean isCopy = transferModes.contains(TransferMode.COPY);

        final M nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());

        if (objectId != null) {

            final TreeView<TreeNode<?>> treeView = getTreeView();
            final TreeItem<TreeNode<?>> dragTreeItem = findItem(treeView, objectId);
            final TreeNode<?> dragItem = dragTreeItem == null ? null : dragTreeItem.getValue();
            if (dragItem == null || !item.canAccept(dragItem, isCopy)) return;

            final TreeItem<TreeNode<?>> newParentItem = findItemForValue(treeView, item);
            if (newParentItem == null) return;

            item.accept(changeConsumer, dragItem.getElement(), isCopy);

        } else if (item.canAcceptExternal(dragboard)) {
            item.acceptExternal(dragboard, changeConsumer);
        }

        dragEvent.consume();
    }

    /**
     * Handle entering a dragged element.
     */
    private void dragOver(@NotNull final DragEvent dragEvent) {

        final TreeNode<?> item = getItem();
        if (item == null) return;

        final Dragboard dragboard = dragEvent.getDragboard();
        final Long objectId = (Long) dragboard.getContent(DATA_FORMAT);

        final Set<TransferMode> transferModes = dragboard.getTransferModes();
        final boolean isCopy = transferModes.contains(TransferMode.COPY);

        if (objectId != null) {

            final TreeView<TreeNode<?>> treeView = getTreeView();
            final TreeItem<TreeNode<?>> dragTreeItem = findItem(treeView, objectId);
            final TreeNode<?> dragItem = dragTreeItem == null ? null : dragTreeItem.getValue();
            if (dragItem == null || !item.canAccept(dragItem, isCopy)) return;

        } else if (!item.canAcceptExternal(dragboard)) {
            return;
        }

        dragEvent.acceptTransferModes(isCopy ? TransferMode.COPY : TransferMode.MOVE);
        dragEvent.consume();

        dropAvailable.setValue(true);
    }

    /**
     * Handle exiting a dragged element.
     */
    private void dragExited(@NotNull final DragEvent dragEvent) {
        dropAvailable.setValue(false);
    }
}
