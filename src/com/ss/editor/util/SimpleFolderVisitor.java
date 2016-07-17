package com.ss.editor.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Упрощенная версия визитера по папкам.
 *
 * @author Ronn
 */
public interface SimpleFolderVisitor extends SimpleFileVisitor {

    @Override
    public default FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        visit(dir, attrs);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public default FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
