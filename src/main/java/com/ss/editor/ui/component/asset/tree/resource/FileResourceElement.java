package com.ss.editor.ui.component.asset.tree.resource;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The presentation of a file.
 *
 * @author JavaSaBr
 */
public class FileResourceElement extends ResourceElement {

    /**
     * Instantiates a new File element.
     *
     * @param file the file
     */
    FileResourceElement(@NotNull final Path file) {
        super(file);
    }
}
