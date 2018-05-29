package com.ss.editor.ui.component.asset.tree;

import static com.ss.editor.manager.FileIconManager.DEFAULT_FILE_ICON_SIZE;
import static java.util.Collections.singletonList;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.ui.FxConstants;
import com.ss.editor.ui.component.asset.tree.resource.FolderResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.LoadingResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.rlib.common.util.StringUtils;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;

/**
 * The implementation of the cell for {@link TreeView} to show resource.
 *
 * @author JavaSaBr
 */
public class ResourceTreeCell extends TreeCell<ResourceElement> {

    /**
     * The file icon manager.
     */
    @NotNull
    private static final FileIconManager ICON_MANAGER = FileIconManager.getInstance();

    /**
     * The icon.
     */
    @NotNull
    private final ImageView icon;

    /**
     * The tooltip of this resource.
     */
    @Nullable
    private Tooltip tooltip;

    protected ResourceTreeCell() {
        setOnMouseClicked(this::handleMouseClickedEvent);
        setOnDragDetected(this::handleStartDragEvent);
        setOnDragDone(this::handleStopDragEvent);
        this.icon = new ImageView();
        this.tooltip = new Tooltip();
    }

    /**
     * Handle stop drag events.
     *
     * @param event the stop drag event.
     */
    @FxThread
    private void handleStopDragEvent(@NotNull DragEvent event) {
        setCursor(Cursor.DEFAULT);
        event.consume();
    }

    /**
     * Handle start drag events.
     *
     * @param mouseEvent the mouse event.
     */
    @FxThread
    private void handleStartDragEvent(@NotNull MouseEvent mouseEvent) {
        startFullDrag();

        var item = getItem();
        if (item == null) {
            return;
        }

        var file = item.getFile();
        if (!Files.exists(file)) {
            return;
        }

        var dragBoard = startDragAndDrop(TransferMode.COPY);
        var content = new ClipboardContent();
        content.put(DataFormat.FILES, singletonList(file.toFile()));

        dragBoard.setContent(content);

        setCursor(Cursor.MOVE);
        mouseEvent.consume();
    }

    /**
     * Handle mouse clicked events.
     *
     * @param event the mouse clicked event.
     */
    @FxThread
    private void handleMouseClickedEvent(@NotNull MouseEvent event) {

        var item = getItem();
        if (item == null) {
            return;
        }

        var isFolder = item instanceof FolderResourceElement;
        var treeView = (ResourceTree) getTreeView();

        if (event.getButton() == MouseButton.SECONDARY) {

            var contextMenu = treeView.getContextMenu(item);
            if (contextMenu == null) {
                return;
            }

            contextMenu.show(this, Side.BOTTOM, 0, 0);

        } else if ((treeView.isOnlyFolders() || !isFolder) &&
                event.getButton() == MouseButton.PRIMARY && event.getClickCount() > 1) {

            var openFunction = treeView.getOpenFunction();
            if (openFunction != null) {
                openFunction.accept(item);
            }
        }
    }

    @Override
    @FxThread
    protected void updateItem(@Nullable ResourceElement item, boolean empty) {
        super.updateItem(item, empty);

        removeToolTip();

        if (item == null) {
            setText(StringUtils.EMPTY);
            setGraphic(null);
            return;
        } else if (item instanceof LoadingResourceElement) {
            setText("Loading...");
            setGraphic(createIndicator());
            return;
        }

        var file = item.getFile();
        var fileName = file.getFileName();
        var folder = item instanceof FolderResourceElement;

        icon.setImage(ICON_MANAGER.getIcon(file, folder, true, DEFAULT_FILE_ICON_SIZE));

        setText(fileName == null ? file.toString() : fileName.toString());
        setGraphic(icon);
        createToolTip();
    }

    @FxThread
    private @NotNull ProgressIndicator createIndicator() {
        var indicator = new ProgressIndicator();
        indicator.setMaxHeight(FxConstants.RESOURCE_TREE_CELL_HEIGHT - 2);
        indicator.setMaxWidth(FxConstants.RESOURCE_TREE_CELL_HEIGHT - 2);
        return indicator;
    }

    @FxThread
    private void removeToolTip() {
        if (tooltip == null) return;
        Tooltip.uninstall(this, tooltip);
        tooltip = null;
    }

    @FxThread
    private void createToolTip() {
        tooltip = getItem().createToolTip();
        if (tooltip == null) return;
        Tooltip.install(this, tooltip);
    }
}
