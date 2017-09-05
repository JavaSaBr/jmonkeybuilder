package com.ss.editor.ui.component.asset.tree.context.menu.action;

import static java.util.stream.Collectors.toList;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.util.array.Array;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * The action to cut a file.
 *
 * @author JavaSaBr
 */
public class CutFileAction extends FileAction {

    public CutFileAction(@NotNull final Array<ResourceElement> elements) {
        super(elements);
    }

    @Override
    protected @Nullable Image getIcon() {
        return Icons.CUT_16;
    }

    @Override
    protected @NotNull String getName() {
        return Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_CUT_FILE;
    }

    @Override
    protected void execute(@Nullable final ActionEvent event) {
        super.execute(event);

        final List<File> files = getElements().stream()
                .map(ResourceElement::getFile)
                .map(Path::toFile)
                .collect(toList());

        final ClipboardContent content = new ClipboardContent();
        content.putFiles(files);
        content.put(EditorUtil.JAVA_PARAM, "cut");

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.setContent(content);
    }
}
