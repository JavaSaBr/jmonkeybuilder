package com.ss.editor.ui.component.editor;

import com.ss.editor.annotation.FromAnyThread;
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
