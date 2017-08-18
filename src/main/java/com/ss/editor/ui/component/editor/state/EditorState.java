package com.ss.editor.ui.component.editor.state;

import com.ss.editor.ui.component.editor.state.impl.AdditionalEditorState;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * The interface for implementing a state container of Editor.
 *
 * @author JavaSaBr
 */
public interface EditorState extends Serializable {

    /**
     * Sets change handler.
     *
     * @param handle the change handler.
     */
    void setChangeHandler(@NotNull Runnable handle);

    /**
     * Fet or create an additional editor state which will store state in this state.
     *
     * @param type    the type of additional state.
     * @param factory the factory of the state if it will not be exists.
     * @param <T>     the type of the state.
     * @return the additional editor state.
     */
    @NotNull <T extends AdditionalEditorState> T getOrCreateAdditionalState(@NotNull Class<T> type,
                                                                            @NotNull Supplier<T> factory);
}
