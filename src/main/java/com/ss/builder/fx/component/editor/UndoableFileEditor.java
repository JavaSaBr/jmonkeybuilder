package com.ss.builder.fx.component.editor;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FromAnyThread;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * The interface to mark an editor that it supports undo/redo methods.
 *
 * @author JavaSaBr
 */
public interface UndoableFileEditor extends FileEditor {

    @FromAnyThread
    @NotNull CompletableFuture<UndoableFileEditor> undo();

    @FromAnyThread
    @NotNull CompletableFuture<UndoableFileEditor> redo();
}
