package com.ss.editor.ui.component.asset.tree;

import static com.ss.editor.manager.FileIconManager.DEFAULT_FILE_ICON_SIZE;
import static com.ss.editor.ui.css.CSSIds.ASSET_COMPONENT_RESOURCE_TREE_CELL;
import static java.util.Collections.singletonList;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.ui.FXConstants;
import com.ss.editor.ui.component.asset.tree.resource.FolderElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceLoadingElement;
import com.ss.editor.ui.css.CSSClasses;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * The implementation of the cell for {@link TreeView} for showing a resource.
 *
 * @author JavaSaBr
 */
public class ResourceTreeCell extends TreeCell<ResourceElement> {

    private static final FileIconManager ICON_MANAGER = FileIconManager.getInstance();

    /**
     * The tooltip of this resource.
     */
    private final Tooltip tooltip;

    /**
     * Instantiates a new Resource tree cell.
     */
    protected ResourceTreeCell() {
        setId(ASSET_COMPONENT_RESOURCE_TREE_CELL);
        setMinHeight(FXConstants.CELL_SIZE);
        setOnMouseClicked(this::processClick);

        FXUtils.addClassTo(this, CSSClasses.TRANSPARENT_TREE_CELL);
        FXUtils.addClassTo(this, CSSClasses.SPECIAL_FONT_13);

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
        startFullDrag();

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

            final ContextMenu contextMenu = treeView.getContextMenu(item);
            if (contextMenu == null) return;

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
