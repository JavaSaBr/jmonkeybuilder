package com.ss.editor.ui.component.asset.tree.context.menu.filler.impl;

import static com.ss.editor.util.EditorUtil.hasFileInClipboard;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.asset.tree.context.menu.action.*;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.AssetTreeContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import javafx.scene.control.MenuItem;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * The implementation a filler to create actions for files.
 *
 * @author JavaSaBr
 */
public class ResourceAssetTreeContextMenuFiller implements AssetTreeContextMenuFiller {

    @Override
    public void fill(@NotNull final ResourceElement element, @NotNull final List<MenuItem> items,
                     @NotNull final Predicate<Class<?>> actionTester) {

        if (actionTester.test(NewFileAction.class)) {
            items.add(new NewFileAction(element));
        }

        if (hasFileInClipboard() && actionTester.test(PasteFileAction.class)) {
            items.add(new PasteFileAction(element));
        }

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        final Path file = element.getFile();

        if (!Objects.equals(currentAsset, file)) {

            if (actionTester.test(CopyFileAction.class)) {
                items.add(new CopyFileAction(element));
            }

            if (actionTester.test(CutFileAction.class)) {
                items.add(new CutFileAction(element));
            }

            if (actionTester.test(RenameFileAction.class)) {
                items.add(new RenameFileAction(element));
            }

            if (actionTester.test(DeleteFileAction.class)) {
                items.add(new DeleteFileAction(element));
            }
        }
    }
}