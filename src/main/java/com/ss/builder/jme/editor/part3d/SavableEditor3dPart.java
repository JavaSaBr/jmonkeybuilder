package com.ss.builder.jme.editor.part3d;

import com.ss.builder.annotation.JmeThread;
import com.ss.builder.fx.component.editor.FileEditor;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.fx.component.editor.FileEditor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * The interface to mark an editor 3d part that it supports a save method.
 *
 * @author JavaSaBr
 */
public interface SavableEditor3dPart extends Editor3dPart {

    /**
     * Save changes.
     */
    @JmeThread
    @NotNull CompletableFuture<FileEditor> save();

    /**
     * Return true if this editor part has unsaved changes.
     *
     * @return true if this editor part has unsaved changes.
     */
    @JmeThread
    boolean isDirty();
}
