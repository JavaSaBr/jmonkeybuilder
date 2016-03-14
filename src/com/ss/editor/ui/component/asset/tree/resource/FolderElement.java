package com.ss.editor.ui.component.asset.tree.resource;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import rlib.util.FileUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import static com.ss.editor.ui.component.asset.tree.resource.ResourceElementFactory.createFor;

/**
 * Реализация элемента для папки.
 *
 * @author Ronn
 */
public class FolderElement extends ResourceElement {

    public FolderElement(final Path file) {
        super(file);
    }

    public Array<ResourceElement> getChildren(final Array<String> extensionFilter) {

        if (!Files.isDirectory(file)) {
            return null;
        }

        final Array<ResourceElement> elements = ArrayFactory.newArray(ResourceElement.class);

        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(file)) {
            stream.forEach(child -> {

                final String fileName = child.getFileName().toString();

                if (fileName.startsWith(".")) {
                    return;
                } else if (Files.isDirectory(child) || extensionFilter == null) {
                    elements.add(createFor(child));
                    return;
                }

                final String extension = FileUtils.getExtension(child);

                if (extensionFilter.contains(extension)) {
                    elements.add(createFor(child));
                }
            });

        } catch (IOException e) {
            LOGGER.warning(this, e);
        }

        return elements;
    }

    @Override
    public boolean hasChildren(Array<String> extensionFilter) {
        return true;
    }
}
