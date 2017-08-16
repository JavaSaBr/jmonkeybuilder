package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.file.delete.handler.FileDeleteHandler;
import com.ss.editor.file.delete.handler.FileDeleteHandlerFactory;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.dialog.ConfirmDialog;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.array.Array;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The action for deleting a file.
 *
 * @author JavaSaBr
 */
public class DeleteFileAction extends MenuItem {

    /**
     * The node in the tree.
     */
    @NotNull
    private final ResourceElement element;

    /**
     * Instantiates a new Delete file action.
     *
     * @param element the element
     */
    public DeleteFileAction(@NotNull final ResourceElement element) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE);
        setOnAction(event -> processDelete());
        setGraphic(new ImageView(Icons.REMOVE_12));
    }

    /**
     * Handle deleting.
     */
    private void processDelete() {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        if (currentAsset == null) return;

        final Path file = element.getFile();

        if (currentAsset.equals(file)) {
            //TODO нужно варнинг показать
            return;
        }

        String question = Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE_QUESTION;
        question = question.replace("%file_name%", file.getFileName().toString());

        final ConfirmDialog confirmDialog = new ConfirmDialog(result -> handle(file, result), question);
        confirmDialog.show();
    }

    /**
     * Handle the answer.
     */
    private void handle(@NotNull final Path file, @NotNull final Boolean result) {
        if (!result) return;
        final Array<FileDeleteHandler> handlers = FileDeleteHandlerFactory.findFor(file);
        handlers.forEach(file, FileDeleteHandler::preDelete);
        FileUtils.delete(file);
        handlers.forEach(file, FileDeleteHandler::postDelete);
    }
}
