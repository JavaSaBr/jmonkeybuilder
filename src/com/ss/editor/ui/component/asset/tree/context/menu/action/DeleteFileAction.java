package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.file.delete.handler.FileDeleteHandler;
import com.ss.editor.file.delete.handler.FileDeleteHandlerFactory;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.ui.dialog.ConfirmDialog;
import com.ss.editor.ui.scene.EditorFXScene;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import rlib.util.FileUtils;
import rlib.util.array.Array;

import java.nio.file.Path;

/**
 * The action for deleting a file.
 *
 * @author JavaSaBr
 */
public class DeleteFileAction extends MenuItem {

    @NotNull
    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    /**
     * The node in the tree.
     */
    @NotNull
    private final ResourceElement element;

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

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final ConfirmDialog confirmDialog = new ConfirmDialog(result -> handle(file, result), question);
        confirmDialog.show(scene.getWindow());
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
