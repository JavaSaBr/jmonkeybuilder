package com.ss.editor.ui.component.asset.tree.context.menu.action;

import static com.ss.rlib.common.util.array.ArrayCollectors.toArray;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.util.array.Array;
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

    @FxThread
    public static void applyFor(@NotNull Array<ResourceElement> elements) {
        new CopyFileAction(elements).getOnAction().handle(null);
    }

    public CopyFileAction(@NotNull Array<ResourceElement> elements) {
        super(elements);
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.ASSET_COMPONENT_RESOURCE_TREE_CONTEXT_MENU_COPY_FILE;
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.COPY_16;
    }

    @Override
    @FxThread
    protected void execute(@Nullable ActionEvent event) {
        super.execute(event);

        var files = getElements().stream()
                .map(ResourceElement::getFile)
                .collect(toArray(Path.class));

        var clipboard = Clipboard.getSystemClipboard();
        clipboard.setContent(EditorUtil.addCopiedFile(files, new ClipboardContent()));
    }
}
