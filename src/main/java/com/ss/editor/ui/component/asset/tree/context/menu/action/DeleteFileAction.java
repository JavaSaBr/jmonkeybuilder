package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.file.delete.handler.FileDeleteHandler;
import com.ss.editor.file.delete.handler.FileDeleteHandlerFactory;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.dialog.ConfirmDialog;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.array.Array;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The action for deleting a file.
 *
 * @author JavaSaBr
 */
public class DeleteFileAction extends FileAction {

    public DeleteFileAction(@NotNull final Array<ResourceElement> elements) {
        super(elements);
    }

    @FXThread
    @Override
    protected @NotNull String getName() {
        return Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE;
    }

    @FXThread
    @Override
    protected @Nullable Image getIcon() {
        return Icons.REMOVE_12;
    }

    @FXThread
    @Override
    protected void execute(@Nullable final ActionEvent event) {
        super.execute(event);

        final Array<ResourceElement> elements = getElements();
        final ResourceElement first = elements.first();

        if(elements.size() == 1) {

            final Path file = first.getFile();

            final EditorConfig editorConfig = EditorConfig.getInstance();
            final Path currentAsset = editorConfig.getCurrentAsset();
            if (currentAsset == null || currentAsset.equals(file)) return;

            String question = Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE_QUESTION;
            question = question.replace("%file_name%", file.getFileName().toString());

            final ConfirmDialog confirmDialog = new ConfirmDialog(result -> handle(file, result), question);
            confirmDialog.show();

        } else {

            String question = Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILES_QUESTION;
            question = question.replace("%file_count%", String.valueOf(elements.size()));

            final ConfirmDialog confirmDialog = new ConfirmDialog(result -> handle(elements, result), question);
            confirmDialog.show();
        }
    }

    /**
     * Handle the answer.
     */
    private void handle(@NotNull final Path file, @Nullable final Boolean result) {
        if (!Boolean.TRUE.equals(result)) return;
        deleteFile(file);
    }

    private void deleteFile(@NotNull final Path file) {
        final Array<FileDeleteHandler> handlers = FileDeleteHandlerFactory.findFor(file);
        handlers.forEach(file, FileDeleteHandler::preDelete);
        FileUtils.delete(file);
        handlers.forEach(file, FileDeleteHandler::postDelete);
    }

    /**
     * Handle the answer.
     */
    private void handle(@NotNull final Array<ResourceElement> elements, @Nullable final Boolean result) {
        if (!Boolean.TRUE.equals(result)) return;

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) return;

        elements.stream().map(ResourceElement::getFile)
                .filter(path -> !currentAsset.equals(path))
                .forEach(this::deleteFile);
    }
}
