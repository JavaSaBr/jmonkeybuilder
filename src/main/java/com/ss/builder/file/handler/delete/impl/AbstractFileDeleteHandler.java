package com.ss.builder.file.handler.delete.impl;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.file.handler.delete.FileDeleteHandler;
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

    protected static final Logger LOGGER = LoggerManager.getLogger(FileDeleteHandler.class);

    @Override
    @FxThread
    public void preDelete(@NotNull Path file) {
    }

    @Override
    @FxThread
    public void postDelete(@NotNull Path file) {

    }

    @Override
    @FxThread
    public boolean isNeedHandle(@NotNull Path file) {
        return false;
    }

    @Override
    public @NotNull FileDeleteHandler clone() {
        try {
            return (FileDeleteHandler) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
