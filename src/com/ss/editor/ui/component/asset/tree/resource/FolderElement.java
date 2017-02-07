package com.ss.editor.ui.component.asset.tree.resource;

import static com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory.createFor;
import org.jetbrains.annotations.NotNull;
import rlib.util.FileUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The presentation of a folder.
 *
 * @author JavaSaBr
 */
public class FolderElement extends ResourceElement {

    FolderElement(@NotNull final Path file) {
        super(file);
    }

    @Override
    public Array<ResourceElement> getChildren(@NotNull final Array<String> extensionFilter, final boolean onlyFolders) {
        if (!Files.isDirectory(file)) return null;

        final Array<ResourceElement> elements = ArrayFactory.newArray(ResourceElement.class);

        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(file)) {
            stream.forEach(child -> {

                final String fileName = child.getFileName().toString();

                if (fileName.startsWith(".")) {
                    return;
                } else if (Files.isDirectory(child)) {
                    elements.add(createFor(child));
                    return;
                }

                if (onlyFolders) return;

                final String extension = FileUtils.getExtension(child);

                if (extensionFilter.isEmpty() || extensionFilter.contains(extension)) {
                    elements.add(createFor(child));
                }
            });

        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        return elements;
    }

    @Override
    public boolean hasChildren(@NotNull final Array<String> extensionFilter, final boolean onlyFolders) {
        return true;
    }
}
