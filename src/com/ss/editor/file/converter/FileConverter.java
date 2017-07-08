package com.ss.editor.file.converter;

import com.ss.editor.annotation.FXThread;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The interface to implement a file converter.
 *
 * @author JavaSaBr
 */
public interface FileConverter {

    /**
     * Convert a source file.
     *
     * @param source the source file.
     */
    @FXThread
    void convert(@NotNull Path source);

    /**
     * Convert a source file to destination file.
     *
     * @param source      the source file.
     * @param destination the destination file.
     */
    @FXThread
    void convert(@NotNull Path source, @NotNull Path destination);


    /**
     * Gets target extension.
     *
     * @return the result file format name.
     */
    @NotNull
    @FXThread
    String getTargetExtension();
}
