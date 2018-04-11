package com.ss.editor.ui.component.asset.tree.context.menu.action;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The action to copy a file.
 *
 * @author JavaSaBr
 */
public class CopyFileAction extends FileAction {

    public CopyFileAction(@NotNull final Array<ResourceElement> elements) {
        super(elements);
    }

    @FxThread
    @Override
    protected @NotNull String getName() {
        return Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_COPY_FILE;
    }

    @FxThread
    @Override
    protected @Nullable Image getIcon() {
        return Icons.COPY_16;
    }

    @FxThread
    @Override
    protected void execute(@Nullable final ActionEvent event) {
        super.execute(event);

        final Array<ResourceElement> elements = getElements();
        final Array<Path> files = ArrayFactory.newArray(Path.class, elements.size());
        elements.forEach(files, (resource, toStore) -> toStore.add(resource.getFile()));

        final ClipboardContent content = new ClipboardContent();

        EditorUtil.addCopiedFile(files, content);

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.setContent(content);
    }
}
