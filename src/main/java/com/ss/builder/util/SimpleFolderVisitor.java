package com.ss.builder.util;

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
    default @NotNull FileVisitResult preVisitDirectory(
            @NotNull Path dir, @NotNull BasicFileAttributes attrs
    ) throws IOException {
        visit(dir, attrs);
        return FileVisitResult.CONTINUE;
    }

    @Override
    default @NotNull FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs)
            throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
