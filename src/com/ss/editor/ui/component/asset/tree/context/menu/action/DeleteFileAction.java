package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;

import java.nio.file.Path;

import javafx.scene.control.MenuItem;
import rlib.util.FileUtils;

/**
 * Реализация действия по удалении файла.
 *
 * @author Ronn
 */
public class DeleteFileAction extends MenuItem {

    /**
     * Элемент действия.
     */
    private final ResourceElement element;

    public DeleteFileAction(final ResourceElement element) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_DELETE_FILE);
        setOnAction(event -> processDelete());
    }

    /**
     * Процесс удаления файла.
     */
    private void processDelete() {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        final Path file = element.getFile();

        if (currentAsset.equals(file)) {
            //TODO нужно варнинг показать
            return;
        }

        FileUtils.delete(file);
    }
}
