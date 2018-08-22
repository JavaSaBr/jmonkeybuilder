package com.ss.editor.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * The simple file visitor.
 *
 * @author JavaSaBr.
 */
public interface SimpleFileVisitor extends FileVisitor<Path> {

    @Override
    default FileVisitResult preVisitDirectory(@NotNull Path dir, @NotNull BasicFileAttributes attrs)
            throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    default FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) throws IOException {
        visit(file, attrs);
        return FileVisitResult.CONTINUE;
    }

    /**
     * Visit the file.
     *
     * @param file  the file.
     * @param attrs the attributes of the file.
     */
    void visit(@NotNull Path file, @NotNull BasicFileAttributes attrs);

    @Override
    default FileVisitResult visitFileFailed(@NotNull Path file, @Nullable IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    default FileVisitResult postVisitDirectory(@NotNull Path dir, @Nullable IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
