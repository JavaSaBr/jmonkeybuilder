package com.ss.builder.ui.component.asset.tree.context.menu.filler.impl;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.file.converter.FileConverterDescription;
import com.ss.builder.file.converter.FileConverterRegistry;
import com.ss.builder.ui.component.asset.tree.resource.FileResourceElement;
import com.ss.builder.ui.component.asset.tree.resource.ResourceElement;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.editor.file.converter.FileConverterRegistry;
import com.ss.editor.ui.component.asset.tree.context.menu.action.*;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.AssetTreeMultiContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.context.menu.filler.AssetTreeSingleContextMenuFiller;
import com.ss.editor.ui.component.asset.tree.resource.FileResourceElement;
import com.ss.editor.ui.component.asset.tree.resource.ResourceElement;
import com.ss.rlib.common.util.array.Array;
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
public class FileAssetTreeSingleContextMenuFiller implements AssetTreeSingleContextMenuFiller,
        AssetTreeMultiContextMenuFiller {

    @NotNull
    private static final FileConverterRegistry FILE_CONVERTER_REGISTRY = FileConverterRegistry.getInstance();

    @Override
    @FxThread
    public void fill(@NotNull final ResourceElement element, @NotNull final List<MenuItem> items,
                     @NotNull final Predicate<Class<?>> actionTester) {

        if (element instanceof FileResourceElement) {

            if (actionTester.test(OpenFileAction.class)) {
                items.add(new OpenFileAction(element));
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

            if (actionTester.test(OpenFileByExternalEditorAction.class)) {
                items.add(new OpenFileByExternalEditorAction(element));
            }
        }
    }

    @Override
    @FxThread
    public void fill(@NotNull final Array<ResourceElement> elements, @NotNull final List<MenuItem> items,
                     @NotNull final Predicate<Class<?>> actionTester) {
    }
}
