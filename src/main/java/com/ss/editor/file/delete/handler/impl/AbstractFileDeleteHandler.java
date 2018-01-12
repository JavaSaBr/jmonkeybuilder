package com.ss.editor.file.delete.handler.impl;

import com.ss.editor.JmeApplication;
import com.ss.editor.JfxApplication;
import com.ss.editor.file.delete.handler.FileDeleteHandler;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The base implementation of file delete handler.
 *
 * @author JavaSaBr
 */
public abstract class AbstractFileDeleteHandler implements FileDeleteHandler {

    /**
     * The javaFX application.
     */
    @NotNull
    protected static final JfxApplication JFX_APPLICATION = JfxApplication.getInstance();

    /**
     * The editor.
     */
    @NotNull
    protected static final JmeApplication JME_APPLICATION = JmeApplication.getInstance();

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
