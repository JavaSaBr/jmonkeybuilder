package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.dialog.RenameDialog;
import com.ss.editor.ui.event.impl.RenamedFileEvent;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.StringUtils;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The action to rename a file.
 *
 * @author JavaSaBr
 */
public class RenameFileAction extends FileAction {

    public RenameFileAction(@NotNull final ResourceElement element) {
        super(element);
    }

    @Override
    protected @Nullable Image getIcon() {
        return Icons.EDIT_16;
    }

    @Override
    protected @NotNull String getName() {
        return Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_RENAME_FILE;
    }

    @Override
    protected void execute(@Nullable final ActionEvent event) {
        super.execute(event);

        final Path file = getElement().getFile();

        final RenameDialog renameDialog = new RenameDialog();
        renameDialog.setValidator(this::checkName);
        renameDialog.setHandler(this::processRename);
        renameDialog.setInitName(FileUtils.getNameWithoutExtension(file));
        renameDialog.show();
    }

    /**
     * The checking of the new file name.
     */
    private Boolean checkName(@NotNull final String newFileName) {
        if (!FileUtils.isValidName(newFileName)) return false;

        final Path file = getElement().getFile();
        final String extension = FileUtils.getExtension(file);

        final Path parent = file.getParent();
        final Path targetFile = parent.resolve(StringUtils.isEmpty(extension) ? newFileName : newFileName + "." + extension);

        return !Files.exists(targetFile);
    }

    /**
     * The process of renaming.
     */
    private void processRename(@NotNull final String newFileName) {

        final Path file = getElement().getFile();

        final String extension = FileUtils.getExtension(file);
        final String resultName = StringUtils.isEmpty(extension) ? newFileName : newFileName + "." + extension;

        final Path newFile = file.resolveSibling(resultName);

        try {
            Files.move(file, newFile);
        } catch (final IOException e) {
            EditorUtil.handleException(null, this, e);
            return;
        }

        final RenamedFileEvent event = new RenamedFileEvent();
        event.setNewFile(newFile);
        event.setPrevFile(file);

        FX_EVENT_MANAGER.notify(event);
    }
}
