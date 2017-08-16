package com.ss.editor.ui.component.asset.tree.resource;

import com.ss.editor.manager.JavaFXImageManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The factory to create resource elements.
 *
 * @author JavaSaBr
 */
public class ResourceElementFactory {

    /**
     * Create for resource element.
     *
     * @param file the file
     * @return the resource element
     */
    @NotNull
    public static ResourceElement createFor(@NotNull final Path file) {
        if (Files.isDirectory(file)) {
            return new FolderResourceElement(file);
        } else if (JavaFXImageManager.isImage(file)) {
            return new ImageResourceElement(file);
        } else {
            return new FileResourceElement(file);
        }
    }
}
