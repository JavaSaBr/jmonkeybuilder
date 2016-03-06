package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.util.EditorUtil;

import java.nio.file.Path;

import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import static java.util.Collections.singletonList;

/**
 * Реализация действия по копированию файла.
 *
 * @author Ronn
 */
public class CopyFileAction extends MenuItem {

    /**
     * Элемент действия.
     */
    private final ResourceElement element;

    public CopyFileAction(final ResourceElement element) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_COPY_FILE);
        setOnAction(event -> processCopy());
    }

    /**
     * Процесс копирования файла.
     */
    private void processCopy() {

        final Path file = element.getFile();

        final ClipboardContent content = new ClipboardContent();
        content.putFiles(singletonList(file.toFile()));
        content.put(EditorUtil.JAVA_PARAM, "copy");

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.setContent(content);
    }
}
