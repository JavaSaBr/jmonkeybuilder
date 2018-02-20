package com.ss.editor.ui.component.asset.tree.context.menu.filler.impl;

import static com.ss.editor.ui.util.UiUtils.hasFileInClipboard;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.asset.tree.context.menu.action.*;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.AssetTreeMultiContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.AssetTreeSingleContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.resource.FileResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.FolderResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.rlib.util.array.Array;
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
public class ResourceAssetTreeSingleContextMenuFiller implements AssetTreeSingleContextMenuFiller,
        AssetTreeMultiContextMenuFiller {

    @Override
    @FxThread
    public void fill(@NotNull final ResourceElement element, @NotNull final List<MenuItem> items,
                     @NotNull final Predicate<Class<?>> actionTester) {

        if (element instanceof FolderResourceElement && actionTester.test(NewFileAction.class)) {
            items.add(new NewFileAction(element));
        }

        if(actionTester.test(OpenFileInExplorerAction.class)) {
            items.add(new OpenFileInExplorerAction(element));
        }

        if (element instanceof FileResourceElement && actionTester.test(NewFileAction.class)) {
            items.add(new NewFileAction(element));
        }

        if (hasFileInClipboard() && actionTester.test(PasteFileAction.class)) {
            items.add(new PasteFileAction(element));
        }

        if (actionTester.test(ImportModelFileAction.class)) {
            items.add(new ImportModelFileAction(element));
        }

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();
        final Path file = element.getFile();

        if (!Objects.equals(currentAsset, file)) {
            if (actionTester.test(RenameFileAction.class)) {
                items.add(new RenameFileAction(element));
            }
        }
    }

    @Override
    @FxThread
    public void fill(@NotNull final Array<ResourceElement> elements, @NotNull final List<MenuItem> items,
                     @NotNull final Predicate<Class<?>> actionTester) {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path currentAsset = editorConfig.getCurrentAsset();

        boolean onlyFiles = true;
        boolean onlyFolders = true;
        boolean selectedAsset = false;

        for (final ResourceElement element : elements.array()) {
            if (element == null) break;

            if (element instanceof FileResourceElement) {
                onlyFolders = false;
            } else if (element instanceof FolderResourceElement) {
                onlyFiles = false;
            }

            if (Objects.equals(currentAsset, element.getFile())) {
                selectedAsset = true;
            }
        }

        if (!selectedAsset && (onlyFiles || elements.size() == 1)) {

            if (actionTester.test(CopyFileAction.class)) {
                items.add(new CopyFileAction(elements));
            }

            if (actionTester.test(CutFileAction.class)) {
                items.add(new CutFileAction(elements));
            }

            if (actionTester.test(DeleteFileAction.class)) {
                items.add(new DeleteFileAction(elements));
            }
        }
    }
}