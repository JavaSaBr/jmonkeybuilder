package com.ss.editor.ui.control.model.tree;

import static com.ss.editor.ui.util.UIUtils.findItem;
import static com.ss.editor.ui.util.UIUtils.findItemForValue;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.action.operation.MoveChildOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.GeomUtils;

import java.util.Set;

import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
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
import javafx.util.StringConverter;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;

/**
 * Реализация ячейки узла модели в дереве.
 *
 * @author Ronn
 */
public class ModelNodeTreeCell extends TextFieldTreeCell<ModelNode<?>> {

    private static final DataFormat DATA_FORMAT = new DataFormat("SSEditor.modelNodeTree.modelNode");

    public final StringConverter<ModelNode<?>> stringConverter = new StringConverter<ModelNode<?>>() {

        @Override
        public String toString(final ModelNode<?> object) {
            return object.getName();
        }

        @Override
        public ModelNode<?> fromString(final String string) {

            final ModelNode<?> item = getItem();
            if (item == null) return null;

            item.changeName(getNodeTree(), string);

            return item;
        }
    };

    /**
     * Компонент дерева узлов модели.
     */
    private final ModelNodeTree nodeTree;

    /**
     * Компонент для отображения иконки.
     */
    private final ImageView imageView;

    public ModelNodeTreeCell(final ModelNodeTree nodeTree) {
        this.nodeTree = nodeTree;
        this.imageView = new ImageView();

        setId(CSSIds.MODEL_NODE_TREE_CELL);
        setOnMouseClicked(this::processClick);
        setOnDragDetected(this::startDrag);
        setOnDragDone(this::stopDrag);
        setOnDragOver(this::dragOver);
        setOnDragDropped(this::dragDropped);
        setOnDragExited(this::dragExited);

        FXUtils.addClassTo(this, CSSClasses.TRANSPARENT_TREE_CELL);
        FXUtils.addClassTo(this, CSSClasses.SPECIAL_FONT_12);

        setConverter(stringConverter);
    }

    @Override
    public void startEdit() {

        if (!isEditable()) {
            return;
        }

        super.startEdit();

        final TextField textField = (TextField) getGraphic();
        textField.setMinHeight(getMinHeight());
    }

    /**
     * @return компонент для отображения иконки.
     */
    private ImageView getImageView() {
        return imageView;
    }

    @Override
    public void updateItem(final ModelNode<?> item, final boolean empty) {
        super.updateItem(item, empty);

        final ImageView imageView = getImageView();

        if (item == null) {
            setText(StringUtils.EMPTY);
            setGraphic(null);
            return;
        }

        imageView.setImage(item.getIcon());

        setText(item.getName());
        setEditable(item.canEditName());
        setGraphic(imageView);
    }

    /**
     * @return компонент дерева узлов модели.
     */
    private ModelNodeTree getNodeTree() {
        return nodeTree;
    }

    /**
     * Процесс обработки клика на элемент дерева.
     */
    private void processClick(final MouseEvent event) {

        final ModelNode<?> item = getItem();
        if (item == null) return;

        final MouseButton button = event.getButton();
        if (button != MouseButton.SECONDARY) return;

        final ModelNodeTree nodeTree = getNodeTree();
        final ContextMenu contextMenu = nodeTree.getContextMenu(item);
        if (contextMenu == null) return;

        contextMenu.show(this, Side.BOTTOM, 0, 0);
    }

    /**
     * Обработка завершения перемещения.
     */
    private void stopDrag(final DragEvent event) {
        setId(CSSIds.MODEL_NODE_TREE_CELL);
        setCursor(Cursor.DEFAULT);
        event.consume();
    }

    /**
     * Обработка старта перемещения файла.
     */
    private void startDrag(final MouseEvent mouseEvent) {

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
     * Обработка принятия.
     */
    private void dragDropped(final DragEvent dragEvent) {

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

        if (isCopy) {

            //TODO переделать на операцию
            final ModelNode<?> copy = item.copy();

            final ModelNode<?> newParent = newParentItem.getValue();
            newParent.add(dragItem);

            final ModelNodeTree nodeTree = getNodeTree();
            nodeTree.notifyAdded(newParent, copy, -1);

        } else if (element instanceof Spatial) {

            final ModelNodeTree nodeTree = getNodeTree();
            final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();

            final TreeItem<ModelNode<?>> parent = dragTreeItem.getParent();

            final ModelNode<?> prevParent = parent.getValue();
            final ModelNode<?> newParent = newParentItem.getValue();
            if (newParent == prevParent) return;

            final Spatial spatial = (Spatial) element;
            final Node prevParentNode = (Node) prevParent.getElement();
            final Node newParentNode = (Node) newParent.getElement();
            final Spatial currentModel = modelChangeConsumer.getCurrentModel();

            final int prevParentIndex = GeomUtils.getIndex(currentModel, prevParentNode);
            final int newParentIndex = GeomUtils.getIndex(currentModel, newParentNode);
            final int childIndex = prevParentNode.getChildIndex(spatial);

            modelChangeConsumer.execute(new MoveChildOperation(spatial, prevParentIndex, newParentIndex, childIndex));
        }

        dragEvent.consume();
    }

    /**
     * Обработка вхождения в зону.
     */
    private void dragOver(final DragEvent dragEvent) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final Long objectId = (Long) dragboard.getContent(DATA_FORMAT);

        if (objectId == null) {
            return;
        }

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
     * Обработка выхода из зоны.
     */
    private void dragExited(final DragEvent dragEvent) {
        setId(CSSIds.MODEL_NODE_TREE_CELL);
    }
}
