package com.ss.editor.file.delete.handler.impl;

import com.ss.editor.file.delete.handler.FileDeleteHandler;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The base implementation of {@link FileDeleteHandler}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractFileDeleteHandler implements FileDeleteHandler {

    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(FileDeleteHandler.class);

    @Override
    public void preDelete(@NotNull final Path file) {
    }

    @Override
    public void postDelete(@NotNull final Path file) {

    }

    @Override
    public boolean isNeedHandle(@NotNull final Path file) {
        return false;
    }

    @Override
    public @NotNull FileDeleteHandler clone() {
        try {
            return (FileDeleteHandler) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
