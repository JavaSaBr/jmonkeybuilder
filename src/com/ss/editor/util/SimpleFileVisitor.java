package com.ss.editor.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Упрощенная версия файлового визитера.
 *
 * @author Ronn
 */
public interface SimpleFileVisitor extends FileVisitor<Path> {

    @Override
    public default FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public default FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        visit(file, attrs);
        return FileVisitResult.CONTINUE;
    }

    public void visit(Path file, BasicFileAttributes attrs);

    @Override
    public default FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public default FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
