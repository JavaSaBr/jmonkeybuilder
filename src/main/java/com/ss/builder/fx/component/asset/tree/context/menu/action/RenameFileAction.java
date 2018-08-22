package com.ss.builder.ui.component.asset.tree.context.menu.action;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.builder.ui.component.asset.tree.resource.ResourceElement;
import com.ss.builder.ui.dialog.RenameDialog;
import com.ss.builder.ui.event.FxEventManager;
import com.ss.builder.ui.event.impl.RenamedFileEvent;
import com.ss.builder.util.EditorUtils;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.dialog.RenameDialog;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.RenamedFileEvent;
import com.ss.editor.util.EditorUtils;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.StringUtils;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;

/**
 * The action to rename a file.
 *
 * @author JavaSaBr
 */
public class RenameFileAction extends FileAction {

    public RenameFileAction(@NotNull ResourceElement element) {
        super(element);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.EDIT_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_RENAME_FILE;
    }

    @Override
    @FxThread
    protected void execute(@Nullable ActionEvent event) {
        super.execute(event);

        var file = getFile();

        var renameDialog = new RenameDialog();
        renameDialog.setValidator(this::checkName);
        renameDialog.setHandler(this::processRename);
        renameDialog.setInitName(FileUtils.getNameWithoutExtension(file));
        renameDialog.show();
    }

    /**
     * The checking of the new file name.
     */
    private Boolean checkName(@NotNull String newFileName) {

        if (!FileUtils.isValidName(newFileName)) {
            return false;
        }

        var file = getElement().getFile();
        var extension = FileUtils.getExtension(file);

        var parent = file.getParent();
        var targetFile = parent.resolve(StringUtils.isEmpty(extension) ? newFileName : newFileName + "." + extension);

        return !Files.exists(targetFile);
    }

    /**
     * The process of renaming.
     */
    private void processRename(@NotNull String newFileName) {

        var file = getFile();

        var extension = FileUtils.getExtension(file);
        var resultName = StringUtils.isEmpty(extension) ? newFileName : newFileName + "." + extension;

        var newFile = file.resolveSibling(resultName);

        try {
            Files.move(file, newFile);
        } catch (IOException e) {
            EditorUtils.handleException(null, this, e);
            return;
        }

        FxEventManager.getInstance()
                .notify(new RenamedFileEvent(file, newFile));
    }
}
