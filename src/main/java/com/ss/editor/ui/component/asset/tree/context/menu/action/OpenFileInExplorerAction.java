package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.util.EditorUtil;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The action to open a file in an system explorer.
 *
 * @author JavaSaBr
 */
public class OpenFileInExplorerAction extends FileAction {

    public OpenFileInExplorerAction(@NotNull final ResourceElement element) {
        super(element);
    }

    @Override
    protected @Nullable Image getIcon() {
        return super.getIcon();
    }

    @Override
    protected @NotNull String getName() {
        return Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_OPEN_FILE_BY_SYSTEM_EXPLORER;
    }

    @Override
    protected void execute(@Nullable final ActionEvent event) {
        super.execute(event);

        final Path file = getElement().getFile();

        if (Files.isDirectory(file)) {
            EditorUtil.openFileInExternalEditor(file);
        } else {
            EditorUtil.openFileInExternalEditor(file.getParent());
        }
    }
}
