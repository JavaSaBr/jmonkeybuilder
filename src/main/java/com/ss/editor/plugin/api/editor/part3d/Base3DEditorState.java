package com.ss.editor.plugin.api.editor.part3d;

import com.ss.editor.state.editor.impl.AbstractEditor3DState;
import com.ss.editor.ui.component.editor.FileEditor;
import org.jetbrains.annotations.NotNull;

/**
 * The base implementation of 3D part of an editor.
 *
 * @author JavaSaBr
 */
public class Base3DEditorState<T extends FileEditor> extends AbstractEditor3DState<T> {

    public Base3DEditorState(@NotNull final T fileEditor) {
        super(fileEditor);
    }
}
