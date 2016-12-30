package com.ss.editor.ui.component.asset.tree.context.menu.action;

import static java.util.Collections.singletonList;

import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.util.EditorUtil;

import java.nio.file.Path;

import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * The action to copy a file.
 *
 * @author JavaSaBr
 */
public class CopyFileAction extends MenuItem {

    /**
     * The action element.
     */
    private final ResourceElement element;

    public CopyFileAction(final ResourceElement element) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_COPY_FILE);
        setOnAction(event -> processCopy());
        setGraphic(new ImageView(Icons.COPY_16));
    }

    /**
     * Process of copying.
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
