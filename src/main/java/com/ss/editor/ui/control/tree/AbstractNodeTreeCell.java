package com.ss.editor.ui.control.tree;

import static com.ss.editor.ui.util.UIUtils.findItem;
import static com.ss.editor.ui.util.UIUtils.findItemForValue;
import static com.ss.rlib.util.ClassUtils.unsafeCast;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.HideableNode;
import com.ss.editor.ui.control.tree.node.ModelNode;
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
public abstract class AbstractNodeTreeCell<C extends ChangeConsumer, M extends AbstractNodeTree<C>> extends TextFieldTreeCell<ModelNode<?>> {

    @NotNull
    private static final PseudoClass DROP_AVAILABLE_PSEUDO_CLASS = PseudoClass.getPseudoClass("drop-available");

    @NotNull
    private static final PseudoClass DRAGGED_PSEUDO_CLASS = PseudoClass.getPseudoClass("dragged");

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    @NotNull
    private static final DataFormat DATA_FORMAT = new DataFormat(AbstractNodeTreeCell.class.getName());

    @NotNull
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
     * The dragged state.
     */
    @NotNull
    private final BooleanProperty dragged = new BooleanPropertyBase(false) {
        public void invalidated() {
            pseudoClassStateChanged(DRAGGED_PSEUDO_CLASS, get());
        }

        @Override
        public Object getBean() {
            return AbstractNodeTreeCell.this;
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
            return AbstractNodeTreeCell.this;
        }

        @Override
        public String getName() {
            return "drop available";
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
    public AbstractNodeTreeCell(@NotNull final M nodeTree) {
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

        DynamicIconSupport.updateListener(this, icon, selectedProperty());

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
        dragged.setValue(false);
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

        dragged.setValue(true);
        setCursor(Cursor.MOVE);

        mouseEvent.consume();
    }

    /**
     * Handle dropping a dragged element.
     */
    private void dragDropped(@NotNull final DragEvent dragEvent) {

        final ModelNode<?> item = getItem();
        if (item == null) return;

        final Dragboard dragboard = dragEvent.getDragboard();
        final Long objectId = (Long) dragboard.getContent(DATA_FORMAT);

        if (objectId != null) {

            final TreeView<ModelNode<?>> treeView = getTreeView();
            final TreeItem<ModelNode<?>> dragTreeItem = findItem(treeView, objectId);
            final ModelNode<?> dragItem = dragTreeItem == null ? null : dragTreeItem.getValue();
            if (dragItem == null || !item.canAccept(dragItem)) return;

            final TreeItem<ModelNode<?>> newParentItem = findItemForValue(treeView, item);
            if (newParentItem == null) return;

            final Set<TransferMode> transferModes = dragboard.getTransferModes();
            final boolean isCopy = transferModes.contains(TransferMode.COPY);


            final Object element = dragItem.getElement();

            if (processDragDropped(dragTreeItem, dragItem, item, isCopy, newParentItem, element)) return;

        } else if (item.canAcceptExternal(dragboard)) {

            final M nodeTree = getNodeTree();
            final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());

            item.acceptExternal(dragboard, changeConsumer);
        }

        dragEvent.consume();
    }

    /**
     * Process drag dropped boolean.
     *
     * @param dragTreeItem  the drag tree item
     * @param dragItem      the drag item
     * @param item          the item
     * @param isCopy        the is copy
     * @param newParentItem the new parent item
     * @param element       the element
     * @return the boolean
     */
    protected boolean processDragDropped(@NotNull final TreeItem<ModelNode<?>> dragTreeItem, @NotNull final ModelNode<?> dragItem,
                                         @NotNull final ModelNode<?> item, final boolean isCopy,
                                         @NotNull final TreeItem<ModelNode<?>> newParentItem, @NotNull final Object element) {
        return true;
    }

    /**
     * Handle entering a dragged element.
     */
    private void dragOver(@NotNull final DragEvent dragEvent) {

        final ModelNode<?> item = getItem();
        if (item == null) return;

        final Dragboard dragboard = dragEvent.getDragboard();
        final Long objectId = (Long) dragboard.getContent(DATA_FORMAT);

        if (objectId != null) {

            final TreeView<ModelNode<?>> treeView = getTreeView();
            final TreeItem<ModelNode<?>> dragTreeItem = findItem(treeView, objectId);
            final ModelNode<?> dragItem = dragTreeItem == null ? null : dragTreeItem.getValue();
            if (dragItem == null || !item.canAccept(dragItem)) return;

        } else if (!item.canAcceptExternal(dragboard)) {
            return;
        }

        final Set<TransferMode> transferModes = dragboard.getTransferModes();
        final boolean isCopy = transferModes.contains(TransferMode.COPY);

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
