package com.ss.builder.fx.editor.part.ui;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.editor.layout.EditorLayout;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement a Ui part of editor.
 *
 * @author JavaSaBr
 */
public interface EditorUiPart {

    /**
     * Build Ui controls on the editor's layout.
     *
     * @param layout the editor's layout.
     */
    @FxThread
    default void buildUi(@NotNull EditorLayout layout) {
    }
}
