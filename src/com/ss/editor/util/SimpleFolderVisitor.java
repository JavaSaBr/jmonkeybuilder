package com.ss.editor.util;

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
    default FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
        visit(dir, attrs);
        return FileVisitResult.CONTINUE;
    }

    @Override
    default FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
