package com.ss.editor.ui.component.asset.tree.context.menu.filler.impl;

import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.file.converter.FileConverterRegistry;
import com.ss.editor.ui.component.asset.tree.context.menu.action.ConvertFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.OpenFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.OpenFileByExternalEditorAction;
import com.ss.editor.ui.component.asset.tree.context.menu.action.OpenWithFileAction;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.AssetTreeContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.resource.FileElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.rlib.util.array.Array;
import javafx.scene.control.MenuItem;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

/**
 * The implementation a filler to create actions for files.
 *
 * @author JavaSaBr
 */
public class FileAssetTreeContextMenuFiller implements AssetTreeContextMenuFiller {

    @NotNull
    private static final FileConverterRegistry FILE_CONVERTER_REGISTRY = FileConverterRegistry.getInstance();

    @Override
    public void fill(@NotNull final ResourceElement element, @NotNull final List<MenuItem> items,
                     @NotNull final Predicate<Class<?>> actionTester) {

        if (element instanceof FileElement) {

            if (actionTester.test(OpenFileAction.class)) {
                items.add(new OpenFileAction(element));
            }

            if (actionTester.test(OpenFileByExternalEditorAction.class)) {
                items.add(new OpenFileByExternalEditorAction(element));
            }

            if (actionTester.test(OpenWithFileAction.class)) {
                items.add(new OpenWithFileAction(element));
            }

            if (actionTester.test(ConvertFileAction.class)) {

                final Path file = element.getFile();
                final Array<FileConverterDescription> descriptions = FILE_CONVERTER_REGISTRY.getDescriptions(file);

                if (!descriptions.isEmpty()) {
                    items.add(new ConvertFileAction(element, descriptions));
                }
            }
        }
    }
}
