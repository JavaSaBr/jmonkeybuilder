package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
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

    @FxThread
    public static void applyFor(@NotNull Array<ResourceElement> elements) {
        new DeleteFileAction(elements).getOnAction().handle(null);
    }

    public DeleteFileAction(@NotNull Array<ResourceElement> elements) {
        super(elements);
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE;
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.REMOVE_12;
    }

    @Override
    @FxThread
    protected void execute(@Nullable ActionEvent event) {
        super.execute(event);

        var elements = getElements();
        var first = elements.first();

        if(elements.size() == 1) {

            var file = first.getFile();

            var editorConfig = EditorConfig.getInstance();
            var currentAsset = editorConfig.getCurrentAsset();
            if (currentAsset == null || currentAsset.equals(file)) {
                return;
            }

            var question = Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE_QUESTION;
            question = question.replace("%file_name%", file.getFileName().toString());

            var confirmDialog = new ConfirmDialog(result -> handle(file, result), question);
            confirmDialog.show();

        } else {

            var question = Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILES_QUESTION;
            question = question.replace("%file_count%", String.valueOf(elements.size()));

            var confirmDialog = new ConfirmDialog(result -> handle(elements, result), question);
            confirmDialog.show();
        }
    }

    /**
     * Handle the answer.
     */
    private void handle(@NotNull Path file, @Nullable Boolean result) {
        if (!Boolean.TRUE.equals(result)) return;
        deleteFile(file);
    }

    private void deleteFile(@NotNull Path file) {
        var handlers = FileDeleteHandlerFactory.findFor(file);
        handlers.forEach(file, FileDeleteHandler::preDelete);
        FileUtils.delete(file);
        handlers.forEach(file, FileDeleteHandler::postDelete);
    }

    /**
     * Handle the answer.
     */
    private void handle(@NotNull Array<ResourceElement> elements, @Nullable Boolean result) {

        if (!Boolean.TRUE.equals(result)) {
            return;
        }

        var editorConfig = EditorConfig.getInstance();
        var currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) {
            return;
        }

        elements.stream().map(ResourceElement::getFile)
                .filter(path -> !currentAsset.equals(path))
                .forEach(this::deleteFile);
    }
}
