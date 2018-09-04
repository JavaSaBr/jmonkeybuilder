package com.ss.builder.editor.impl.control.impl;

import com.ss.builder.editor.FileEditor;
import com.ss.builder.editor.impl.control.EditorControl;
import org.jetbrains.annotations.NotNull;

/**
 * The base implementation of {@link EditorControl}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditorControl<F extends FileEditor> implements EditorControl {

    /**
     * The file editor.
     */
    @NotNull
    protected final F fileEditor;

    public AbstractEditorControl(@NotNull F fileEditor) {
        this.fileEditor = fileEditor;
    }
}
