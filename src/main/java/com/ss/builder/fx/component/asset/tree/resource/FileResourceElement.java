package com.ss.builder.fx.component.asset.tree.resource;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The presentation of a file.
 *
 * @author JavaSaBr
 */
public class FileResourceElement extends ResourceElement {

    public FileResourceElement(@NotNull final Path file) {
        super(file);
    }
}
