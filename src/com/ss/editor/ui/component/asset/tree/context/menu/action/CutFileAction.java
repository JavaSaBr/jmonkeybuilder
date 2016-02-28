package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.util.EditorUtil;

import java.nio.file.Path;

import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import static com.ss.editor.Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CUT_FILE;
import static java.util.Collections.singletonList;

/**
 * Реализация действия по вырезанию файла.
 *
 * @author Ronn
 */
public class CutFileAction extends MenuItem {

    /**
     * Элемент действия.
     */
    private final ResourceElement element;

    public CutFileAction(final ResourceElement element) {
        this.element = element;
        setText(ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CUT_FILE);
        setOnAction(event -> processCut());
    }

    /**
     * Процесс вырезания файла.
     */
    private void processCut() {

        final Path file = element.getFile();

        final ClipboardContent content = new ClipboardContent();
        content.putFiles(singletonList(file.toFile()));
        content.put(EditorUtil.JAVA_PARAM, "cut");

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.setContent(content);
    }
}
