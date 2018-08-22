package com.ss.builder.ui.component.asset.tree.resource;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.manager.JavaFxImageManager;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.JavaFxImageManager;
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
     * Create a resource element for the file.
     *
     * @param file the file.
     * @return the created resource element.
     */
    @FromAnyThread
    public static @NotNull ResourceElement createFor(@NotNull final Path file) {
        if (Files.isDirectory(file)) {
            return new FolderResourceElement(file);
        } else if (JavaFxImageManager.isImage(file)) {
            return new ImageResourceElement(file);
        } else {
            return new FileResourceElement(file);
        }
    }
}
