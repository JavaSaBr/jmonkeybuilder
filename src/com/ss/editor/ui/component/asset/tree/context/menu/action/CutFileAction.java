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
import org.jetbrains.annotations.NotNull;

/**
 * The action to cut a file.
 *
 * @author JavaSaBr
 */
public class CutFileAction extends MenuItem {

    /**
     * The action element.
     */
    @NotNull
    private final ResourceElement element;

    /**
     * Instantiates a new Cut file action.
     *
     * @param element the element
     */
    public CutFileAction(@NotNull final ResourceElement element) {
        this.element = element;
        setText(Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CUT_FILE);
        setOnAction(event -> processCut());
        setGraphic(new ImageView(Icons.CUT_16));
    }

    /**
     * Process of cutting.
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
