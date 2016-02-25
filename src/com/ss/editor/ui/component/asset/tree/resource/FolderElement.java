package com.ss.editor.ui.component.asset.tree.resource;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

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

    public Array<ResourceElement> getChildren() {

        if(!Files.isDirectory(file)) {
            return null;
        }

        final Array<ResourceElement> elements = ArrayFactory.newArray(ResourceElement.class);

        try(final DirectoryStream<Path> stream = Files.newDirectoryStream(file)) {
            stream.forEach(child -> elements.add(createFor(child)));
        } catch (IOException e) {
           LOGGER.warning(this, e);
        }

        return elements;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }
}
