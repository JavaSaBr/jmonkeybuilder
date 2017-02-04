package com.ss.editor.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * The simple folder visitor.
 *
 * @author JavaSaBr.
 */
public interface SimpleFolderVisitor extends SimpleFileVisitor {

    @Override
    default FileVisitResult preVisitDirectory(@NotNull final Path dir, @NotNull final BasicFileAttributes attrs)
            throws IOException {
        visit(dir, attrs);
        return FileVisitResult.CONTINUE;
    }

    @Override
    default FileVisitResult visitFile(@NotNull final Path file, @NotNull final BasicFileAttributes attrs)
            throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
