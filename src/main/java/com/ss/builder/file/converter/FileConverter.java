package com.ss.builder.file.converter;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FxThread;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The interface to implement a file converter.
 *
 * @author JavaSaBr
 */
public interface FileConverter {

    /**
     * Convert the source file.
     *
     * @param source the source file.
     */
    @FxThread
    void convert(@NotNull Path source);

    /**
     * Convert the source file to destination file.
     *
     * @param source      the source file.
     * @param destination the destination file.
     */
    @FxThread
    void convert(@NotNull Path source, @NotNull Path destination);

    /**
     * Get the target extension.
     *
     * @return the result file format name.
     */
    @FxThread
    @NotNull String getTargetExtension();
}
