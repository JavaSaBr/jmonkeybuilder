package com.ss.editor.file.delete.handler.impl;

import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
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
     * The constant JFX_APPLICATION.
     */
    @NotNull
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    /**
     * The constant EDITOR.
     */
    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

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
    public FileDeleteHandler clone() {
        try {
            return (FileDeleteHandler) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
