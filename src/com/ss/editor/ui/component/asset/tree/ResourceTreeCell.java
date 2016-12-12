package com.ss.editor.ui.component.asset.tree;

import static com.ss.editor.manager.FileIconManager.DEFAULT_FILE_ICON_SIZE;
import static com.ss.editor.ui.css.CSSIds.ASSET_COMPONENT_RESOURCE_TREE_CELL;
import static java.util.Collections.singletonList;

import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.ui.component.asset.tree.resource.FolderElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceLoadingElement;
import com.ss.editor.ui.css.CSSClasses;

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

/**
 * The implementation of the cell for {@link TreeView} for showing a resource.
 *
 * @author JavaSaBr.
 */
public class ResourceTreeCell extends TreeCell<ResourceElement> {

    public static final Callback<TreeView<ResourceElement>, TreeCell<ResourceElement>> CELL_FACTORY = param -> new ResourceTreeCell();

    private static final FileIconManager ICON_MANAGER = FileIconManager.getInstance();

    /**
     * The tooltip of this resource.
     */
    private final Tooltip tooltip;

    public ResourceTreeCell() {
        setId(ASSET_COMPONENT_RESOURCE_TREE_CELL);
        setMinHeight(15);
        setOnMouseClicked(this::processClick);

        FXUtils.addClassTo(this, CSSClasses.TRANSPARENT_TREE_CELL);
        FXUtils.addClassTo(this, CSSClasses.SPECIAL_FONT_12);

        this.tooltip = new Tooltip();

        setOnDragDetected(this::startDrag);
        setOnDragDone(this::stopDrag);
    }

    /**
     * Handle stopping dragging.
     */
    private void stopDrag(final DragEvent event) {
        setCursor(Cursor.DEFAULT);
        event.consume();
    }

    /**
     * Handle starting dragging.
     */
    private void startDrag(final MouseEvent mouseEvent) {

        final ResourceElement item = getItem();
        if (item == null) return;

        final Path file = item.getFile();
        if (!Files.exists(file)) return;

        final Dragboard dragBoard = startDragAndDrop(TransferMode.COPY);
        final ClipboardContent content = new ClipboardContent();
        content.put(DataFormat.FILES, singletonList(file.toFile()));

        dragBoard.setContent(content);

        setCursor(Cursor.MOVE);
        mouseEvent.consume();
    }

    /**
     * Handle a click.
     */
    private void processClick(final MouseEvent event) {

        final ResourceElement item = getItem();
        if (item == null) return;

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
        setGraphic(new ImageView(ICON_MANAGER.getIcon(file, DEFAULT_FILE_ICON_SIZE)));

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();

        if (file.equals(currentAsset)) {
            Tooltip.install(this, tooltip);
            updateTooltip(file.toString());
        } else {
            Tooltip.uninstall(this, tooltip);
            updateTooltip(StringUtils.EMPTY);
        }
    }

    /**
     * Update the tooltip.
     */
    private void updateTooltip(final String text) {
        tooltip.setText(text);
    }
}
