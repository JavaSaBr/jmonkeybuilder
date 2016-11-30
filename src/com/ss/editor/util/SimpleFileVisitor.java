package com.ss.editor.util;

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
    default FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    default FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        visit(file, attrs);
        return FileVisitResult.CONTINUE;
    }

    void visit(final Path file, final BasicFileAttributes attrs);

    @Override
    default FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    default FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
