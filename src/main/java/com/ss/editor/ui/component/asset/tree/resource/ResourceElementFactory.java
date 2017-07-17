package com.ss.editor.ui.component.asset.tree.resource;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Реализация фабрики элементов ресурсов.
 *
 * @author Ronn
 */
public class ResourceElementFactory {

    /**
     * Create for resource element.
     *
     * @param file the file
     * @return the resource element
     */
    public static ResourceElement createFor(final Path file) {
        return Files.isDirectory(file) ? new FolderElement(file) : new FileElement(file);
    }
}
