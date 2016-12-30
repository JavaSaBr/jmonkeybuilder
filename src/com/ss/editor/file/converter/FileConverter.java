package com.ss.editor.file.converter;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Interface for implementing a file converter.
 *
 * @author JavaSaBr
 */
public interface FileConverter {

    /**
     * Convert a source file.
     *
     * @param source the source file.
     */
    public void convert(@NotNull Path source);

    /**
     * Convert a source file to destination file.
     *
     * @param source      the source file.
     * @param destination the destination file.
     */
    public void convert(@NotNull Path source, @NotNull Path destination);


    /**
     * @return the result file format name.
     */
    @NotNull
    public String getTargetExtension();
}
