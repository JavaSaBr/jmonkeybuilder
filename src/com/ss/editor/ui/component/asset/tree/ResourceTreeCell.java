package com.ss.editor.ui.component.asset.tree;

import com.ss.editor.manager.FileIconManager;
import com.ss.editor.ui.component.asset.tree.resource.FolderElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceLoadingElement;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;

import static com.ss.editor.manager.FileIconManager.DEFAULT_FILE_ICON_SIZE;
import static com.ss.editor.ui.css.CSSClasses.MAIN_FONT_13;
import static com.ss.editor.ui.css.CSSClasses.TRANSPARENT_TREE_CELL;
import static com.ss.editor.ui.css.CSSIds.ASSET_COMPONENT_RESOURCE_TREE_CELL;
import static java.util.Collections.singletonList;

/**
 * Реализация ячейки ресурса для дерева ресурсов.
 *
 * @author Ronn
 */
public class ResourceTreeCell extends TreeCell<ResourceElement> {

    public static final Callback<TreeView<ResourceElement>, TreeCell<ResourceElement>> CELL_FACTORY = param -> new ResourceTreeCell();

    private static final FileIconManager ICON_MANAGER = FileIconManager.getInstance();

    /**
     * Всплывающая подсказка.
     */
    private final Tooltip tooltip;

    public ResourceTreeCell() {
        setId(ASSET_COMPONENT_RESOURCE_TREE_CELL);
        setMinHeight(20);
        setOnMouseClicked(this::processClick);

        FXUtils.addClassTo(this, TRANSPARENT_TREE_CELL);
        FXUtils.addClassTo(this, MAIN_FONT_13);

        this.tooltip = new Tooltip();

        //FIXME надо сделать как-то подругому :)
        // Tooltip.install(this, tooltip);

        setOnDragDetected(this::startDrag);
        setOnDragDone(this::stopDrag);
    }

    /**
     * Обработка завершения перемещения.
     */
    private void stopDrag(final DragEvent event) {
        setCursor(Cursor.DEFAULT);
        event.consume();
    }

    /**
     * Обработка старта перемещения файла.
     */
    private void startDrag(final MouseEvent mouseEvent) {

        final ResourceElement item = getItem();

        if (item == null) {
            return;
        }

        final Path file = item.getFile();

        if (!Files.exists(file)) {
            return;
        }

        final Dragboard dragBoard = startDragAndDrop(TransferMode.COPY);
        final ClipboardContent content = new ClipboardContent();
        content.put(DataFormat.FILES, singletonList(file.toFile()));

        dragBoard.setContent(content);

        setCursor(Cursor.MOVE);
        mouseEvent.consume();
    }

    /**
     * Процесс обработки клика на элемент дерева.
     */
    private void processClick(final MouseEvent event) {

        final ResourceElement item = getItem();

        if (item == null) {
            return;
        }

        final ResourceTree treeView = (ResourceTree) getTreeView();

        if (event.getButton() == MouseButton.SECONDARY) {

            treeView.updateContextMenu(item);

            final ContextMenu contextMenu = treeView.getContextMenu();

            if (contextMenu == null) {
                return;
            }

            contextMenu.show(this, Side.BOTTOM, 0, 0);

        } else if (!(item instanceof FolderElement) && event.getButton() == MouseButton.PRIMARY && event.getClickCount() > 1) {
            final Consumer<ResourceElement> openFunction = treeView.getOpenFunction();
            openFunction.accept(item);
        }
    }

    @Override
    protected void updateItem(final ResourceElement item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            setText(StringUtils.EMPTY);
            updateTooltip(StringUtils.EMPTY);
            setGraphic(null);
            return;
        } else if (item instanceof ResourceLoadingElement) {
            setText(StringUtils.EMPTY);
            updateTooltip(StringUtils.EMPTY);
            setGraphic(new ProgressIndicator());
            return;
        }

        final Path file = item.getFile();
        final Path fileName = file.getFileName();

        setText(fileName.toString());
        updateTooltip(file.toString());
        setGraphic(new ImageView(ICON_MANAGER.getIcon(file, DEFAULT_FILE_ICON_SIZE)));
    }

    /**
     * Обновление текста всплывающей подсказки.
     */
    private void updateTooltip(final String text) {
        tooltip.setText(text);
    }
}
