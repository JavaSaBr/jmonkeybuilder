package com.ss.editor.ui.component.asset.tree;

import static com.ss.editor.manager.FileIconManager.DEFAULT_FILE_ICON_SIZE;
import static java.util.Collections.singletonList;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.ui.FXConstants;
import com.ss.editor.ui.component.asset.tree.resource.FolderResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.LoadingResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.rlib.util.StringUtils;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * The implementation of the cell for {@link TreeView} to show resource.
 *
 * @author JavaSaBr
 */
public class ResourceTreeCell extends TreeCell<ResourceElement> {

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

    /**
     * Instantiates a new Resource tree cell.
     */
    protected ResourceTreeCell() {
        setOnMouseClicked(this::processClick);
        setOnDragDetected(this::startDrag);
        setOnDragDone(this::stopDrag);
        this.icon = new ImageView();
        this.tooltip = new Tooltip();
    }

    /**
     * Handle stopping dragging.
     */
    private void stopDrag(@NotNull final DragEvent event) {
        setCursor(Cursor.DEFAULT);
        event.consume();
    }

    /**
     * Handle starting dragging.
     */
    private void startDrag(@NotNull final MouseEvent mouseEvent) {
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
    private void processClick(@NotNull final MouseEvent event) {

        final ResourceElement item = getItem();
        if (item == null) return;

        final boolean isFolder = item instanceof FolderResourceElement;
        final ResourceTree treeView = (ResourceTree) getTreeView();

        if (event.getButton() == MouseButton.SECONDARY) {
            final ContextMenu contextMenu = treeView.getContextMenu(item);
            if (contextMenu == null) return;
            contextMenu.show(this, Side.BOTTOM, 0, 0);
        } else if ((treeView.isOnlyFolders() || !isFolder) && event.getButton() == MouseButton.PRIMARY &&
                event.getClickCount() > 1) {
            final Consumer<ResourceElement> openFunction = treeView.getOpenFunction();
            if (openFunction != null) openFunction.accept(item);
        }
    }

    @Override
    protected void updateItem(@Nullable final ResourceElement item, boolean empty) {
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

        final Path file = item.getFile();
        final Path fileName = file.getFileName();

        icon.setImage(ICON_MANAGER.getIcon(file, DEFAULT_FILE_ICON_SIZE));

        setText(fileName.toString());
        setGraphic(icon);
        createToolTip();
    }

    private @NotNull ProgressIndicator createIndicator() {
        final ProgressIndicator indicator = new ProgressIndicator();
        indicator.setMaxHeight(FXConstants.RESOURCE_TREE_CELL_HEIGHT - 2);
        indicator.setMaxWidth(FXConstants.RESOURCE_TREE_CELL_HEIGHT - 2);
        return indicator;
    }

    private void removeToolTip() {
        if (tooltip == null) return;
        Tooltip.uninstall(this, tooltip);
        tooltip = null;
    }

    private void createToolTip() {
        tooltip = getItem().createToolTip();
        if (tooltip == null) return;
        Tooltip.install(this, tooltip);
    }
}
