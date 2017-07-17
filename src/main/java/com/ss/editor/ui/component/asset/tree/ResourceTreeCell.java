package com.ss.editor.ui.component.asset.tree;

import static com.ss.editor.manager.FileIconManager.DEFAULT_FILE_ICON_SIZE;
import static java.util.Collections.singletonList;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.ui.component.asset.tree.resource.FolderElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceLoadingElement;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
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
 * The implementation of the cell for {@link TreeView} for showing a resource.
 *
 * @author JavaSaBr
 */
public class ResourceTreeCell extends TreeCell<ResourceElement> {

    @NotNull
    private static final FileIconManager ICON_MANAGER = FileIconManager.getInstance();

    /**
     * The tooltip of this resource.
     */
    @NotNull
    private final Tooltip tooltip;

    /**
     * Instantiates a new Resource tree cell.
     */
    protected ResourceTreeCell() {
        setOnMouseClicked(this::processClick);
        setOnDragDetected(this::startDrag);
        setOnDragDone(this::stopDrag);

        this.tooltip = new Tooltip();

        FXUtils.addClassTo(this, CSSClasses.SPECIAL_FONT_13);
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

        final boolean isFolder = item instanceof FolderElement;
        final ResourceTree treeView = (ResourceTree) getTreeView();

        if (event.getButton() == MouseButton.SECONDARY) {

            final ContextMenu contextMenu = treeView.getContextMenu(item);
            if (contextMenu == null) return;

            contextMenu.show(this, Side.BOTTOM, 0, 0);

        } else if (!isFolder && event.getButton() == MouseButton.PRIMARY && event.getClickCount() > 1) {

            final Consumer<ResourceElement> openFunction = treeView.getOpenFunction();

            if (openFunction != null) {
                openFunction.accept(item);
            }
        }
    }

    @Override
    protected void updateItem(@Nullable final ResourceElement item, boolean empty) {
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
    private void updateTooltip(@NotNull final String text) {
        tooltip.setText(text);
    }
}
