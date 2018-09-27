package com.ss.builder.fx.editor.part.ui;

import com.ss.builder.editor.FileEditor;
import org.jetbrains.annotations.NotNull;

/**
 * The base implementation of Ui part.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditorUiPart<T extends FileEditor> implements EditorUiPart {

    /**
     * The file editor.
     */
    @NotNull
    protected final T fileEditor;

    protected AbstractEditorUiPart(@NotNull T fileEditor) {
        this.fileEditor = fileEditor;
    }
}
