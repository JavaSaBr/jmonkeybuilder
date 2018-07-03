package com.ss.editor.plugin.api.editor.part3d;

import com.ss.editor.part3d.editor.impl.AbstractEditor3dPart;
import com.ss.editor.ui.component.editor.FileEditor;
import org.jetbrains.annotations.NotNull;

/**
 * The base implementation of 3D part of an editor.
 *
 * @author JavaSaBr
 */
public class Base3DEditorPart<T extends FileEditor> extends AbstractEditor3dPart<T> {

    public Base3DEditorPart(@NotNull final T fileEditor) {
        super(fileEditor);
    }
}
