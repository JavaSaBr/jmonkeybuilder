package com.ss.editor.ui.control.tree;

import static com.ss.editor.ui.util.UIUtils.findItem;
import static com.ss.editor.ui.util.UIUtils.findItemForValue;
import static rlib.util.ClassUtils.unsafeCast;

import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.HideableNode;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import com.ss.editor.ui.util.UIUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;

/**
 * The implementation of {@link TreeCell} to show tree nodes.
 *
 * @author JavaSaBr
 */
public abstract class AbstractNodeTreeCell<C extends ChangeConsumer, M extends AbstractNodeTree<C>> extends TextFieldTreeCell<ModelNode<?>> {

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    private static final DataFormat DATA_FORMAT = new DataFormat(AbstractNodeTreeCell.class.getName());

    private static final Insets VISIBLE_ICON_OFFSET = new Insets(0, 0, 0, 2);

    private final StringConverter<ModelNode<?>> stringConverter = new StringConverter<ModelNode<?>>() {

        @Override
        public String toString(@NotNull final ModelNode<?> object) {
            return object.getName();
        }

        @Override
        public ModelNode<?> fromString(@NotNull final String string) {

            final ModelNode<?> item = getItem();
            if (item == null) return null;

            item.changeName(getNodeTree(), string);

            return item;
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

    public AbstractNodeTreeCell(@NotNull final M nodeTree) {
        this.nodeTree = nodeTree;
        this.icon = new ImageView();
        this.content = new HBox();
        this.text = new Label();
        this.visibleIcon = new ImageView();
        this.visibleIcon.addEventFilter(MouseEvent.MOUSE_RELEASED, this::processHide);
        this.visibleIcon.setOnMouseReleased(this::processHide);
        this.visibleIcon.setPickOnBounds(true);

        setId(CSSIds.MODEL_NODE_TREE_CELL);
        setOnMouseClicked(this::processClick);
        setOnDragDetected(this::startDrag);
        setOnDragDone(this::stopDrag);
        setOnDragOver(this::dragOver);
        setOnDragDropped(this::dragDropped);
        setOnDragExited(this::dragExited);
        setOnKeyReleased(event -> {
            if (isEditing()) event.consume();
        });

        FXUtils.addClassTo(this, CSSClasses.TRANSPARENT_TREE_CELL);
        FXUtils.addClassTo(this, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(text, CSSClasses.SPECIAL_FONT_13);

        FXUtils.addToPane(icon, content);
        FXUtils.addToPane(visibleIcon, content);
        FXUtils.addToPane(text, content);

        HBox.setMargin(visibleIcon, VISIBLE_ICON_OFFSET);

        setConverter(stringConverter);
    }

    /**
     * Update hide status.
     */
    private void processHide(@NotNull final MouseEvent event) {
        event.consume();

        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        final ModelNode<?> item = getItem();
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

        final TreeItem<ModelNode<?>> treeItem = getTreeItem();
        if (treeItem != null) treeItem.setGraphic(null);

        setIgnoreUpdate(true);
        try {
            super.startEdit();
        } finally {
            setIgnoreUpdate(false);
        }

        UIUtils.updateEditedCell(this);
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
        final TreeItem<ModelNode<?>> treeItem = getTreeItem();
        if (treeItem != null) treeItem.setGraphic(content);
        setText(StringUtils.EMPTY);
    }

    @Override
    public void commitEdit(@NotNull final ModelNode<?> newValue) {
        super.commitEdit(newValue);
        final TreeItem<ModelNode<?>> treeItem = getTreeItem();
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
    public void updateItem(@Nullable final ModelNode<?> item, final boolean empty) {
        super.updateItem(item, empty);
        if (isIgnoreUpdate()) return;

        final ImageView icon = getIcon();

        if (item == null) {
            final TreeItem<ModelNode<?>> treeItem = getTreeItem();
            if (treeItem != null) treeItem.setGraphic(null);
            setText(StringUtils.EMPTY);
            setEditable(false);
            return;
        }

        icon.setImage(item.getIcon());

        final TreeItem<ModelNode<?>> treeItem = getTreeItem();
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
        } else {
            visibleIcon.setVisible(false);
            visibleIcon.setManaged(false);
        }

        text.setText(item.getName());

        setText(StringUtils.EMPTY);
        setEditable(item.canEditName());
    }

    /**
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

        final ModelNode<?> item = getItem();
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
        setId(CSSIds.MODEL_NODE_TREE_CELL);
        setCursor(Cursor.DEFAULT);
        event.consume();
    }

    /**
     * Handle starting dragging.
     */
    private void startDrag(@NotNull final MouseEvent mouseEvent) {

        final ModelNode<?> item = getItem();
        if (item == null) return;

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> treeItem = findItemForValue(treeView, item);
        if (treeView.getRoot() == treeItem) return;

        TransferMode transferMode = item.canMove() ? TransferMode.MOVE : null;
        transferMode = item.canCopy() ? TransferMode.COPY : transferMode;
        if (transferMode == null) return;

        final Dragboard dragBoard = startDragAndDrop(transferMode);
        final ClipboardContent content = new ClipboardContent();
        content.put(DATA_FORMAT, item.getObjectId());

        dragBoard.setContent(content);

        setId(CSSIds.MODEL_NODE_TREE_CELL_DRAGGED);
        setCursor(Cursor.MOVE);

        mouseEvent.consume();
    }

    /**
     * Handle dropping a dragged element.
     */
    private void dragDropped(@NotNull final DragEvent dragEvent) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final Long objectId = (Long) dragboard.getContent(DATA_FORMAT);
        if (objectId == null) return;

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> dragTreeItem = findItem(treeView, objectId);
        final ModelNode<?> dragItem = dragTreeItem == null ? null : dragTreeItem.getValue();
        if (dragItem == null) return;

        final ModelNode<?> item = getItem();
        if (item == null || !item.canAccept(dragItem)) return;

        final Set<TransferMode> transferModes = dragboard.getTransferModes();
        final boolean isCopy = transferModes.contains(TransferMode.COPY);

        final TreeItem<ModelNode<?>> newParentItem = findItemForValue(treeView, item);
        if (newParentItem == null) return;

        final Object element = dragItem.getElement();

        if (processDragDropped(dragTreeItem, dragItem, item, isCopy, newParentItem, element)) return;

        dragEvent.consume();
    }

    protected boolean processDragDropped(@NotNull final TreeItem<ModelNode<?>> dragTreeItem, @NotNull final ModelNode<?> dragItem,
                                         @NotNull final ModelNode<?> item, final boolean isCopy,
                                         @NotNull final TreeItem<ModelNode<?>> newParentItem, @NotNull final Object element) {
//        FIXME if (isCopy) {
//
//            //TODO переделать на операцию
//            final ModelNode<?> copy = item.copy();
//            final ModelNode<?> newParent = newParentItem.getValue();
//            newParent.add(dragItem);
//
//            final M nodeTree = getNodeTree();
//            nodeTree.notifyAdded(newParent, copy, -1);
//            return false;
//        }

        return true;
    }

    /**
     * Handle entering a dragged element.
     */
    private void dragOver(@NotNull final DragEvent dragEvent) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final Long objectId = (Long) dragboard.getContent(DATA_FORMAT);
        if (objectId == null) return;

        final TreeView<ModelNode<?>> treeView = getTreeView();
        final TreeItem<ModelNode<?>> dragTreeItem = findItem(treeView, objectId);
        final ModelNode<?> dragItem = dragTreeItem == null ? null : dragTreeItem.getValue();
        if (dragItem == null) return;

        final ModelNode<?> item = getItem();
        if (item == null || !item.canAccept(dragItem)) return;

        final Set<TransferMode> transferModes = dragboard.getTransferModes();
        final boolean isCopy = transferModes.contains(TransferMode.COPY);

        dragEvent.acceptTransferModes(isCopy ? TransferMode.COPY : TransferMode.MOVE);
        dragEvent.consume();

        setId(CSSIds.MODEL_NODE_TREE_CELL_DROP_AVAILABLE);
    }

    /**
     * Handle exiting a dragged element.
     */
    private void dragExited(@NotNull final DragEvent dragEvent) {
        setId(CSSIds.MODEL_NODE_TREE_CELL);
    }
}
