package com.ss.builder.ui.component.virtual.tree;

import static com.ss.editor.manager.FileIconManager.DEFAULT_FILE_ICON_SIZE;
import com.ss.builder.manager.FileIconManager;
import com.ss.builder.ui.component.virtual.tree.resource.FolderVirtualResourceElement;
import com.ss.builder.ui.component.virtual.tree.resource.VirtualResourceElement;
import com.ss.editor.manager.FileIconManager;
import com.ss.editor.ui.component.virtual.tree.resource.FolderVirtualResourceElement;
import com.ss.editor.ui.component.virtual.tree.resource.VirtualResourceElement;
import com.ss.rlib.common.util.StringUtils;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The implementation of the cell for {@link TreeView} to show resource.
 *
 * @author JavaSaBr
 */
public class VirtualResourceTreeCell extends TreeCell<VirtualResourceElement<?>> {

    /**
     * The icon manager.
     */
    @NotNull
    private static final FileIconManager ICON_MANAGER = FileIconManager.getInstance();

    /**
     * The icon.
     */
    @NotNull
    private final ImageView icon;

    protected VirtualResourceTreeCell() {
        this.icon = new ImageView();
    }

    @Override
    protected void updateItem(@Nullable final VirtualResourceElement<?> item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            setText(StringUtils.EMPTY);
            setGraphic(null);
            return;
        }

        final Path file = Paths.get(item.getPath());
        final boolean folder = item instanceof FolderVirtualResourceElement;

        icon.setImage(ICON_MANAGER.getIcon(file, folder, false, FileIconManager.DEFAULT_FILE_ICON_SIZE));

        setText(item.getName());
        setGraphic(icon);
    }
}
