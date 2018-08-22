package com.ss.builder.ui.component.editor.state.impl;

import com.ss.builder.annotation.FxThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.editor.state.EditorState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * The empty implementation of editor state.
 *
 * @author JavaSaBr
 */
public final class VoidEditorState implements EditorState {

    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 1;

    private VoidEditorState() {
        throw new RuntimeException();
    }

    @FxThread
    @Override
    public void setChangeHandler(@NotNull final Runnable handle) {
    }

    @FxThread
    @Override
    public <T extends AdditionalEditorState> @NotNull T getOrCreateAdditionalState(@NotNull final Class<T> type,
                                                                                   @NotNull final Supplier<T> factory) {
        throw new RuntimeException();
    }
}
