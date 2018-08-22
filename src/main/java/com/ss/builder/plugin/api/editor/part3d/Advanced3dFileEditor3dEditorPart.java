package com.ss.builder.plugin.api.editor.part3d;

import com.ss.builder.jme.editor.part3d.impl.Base3dSceneEditor3dPart;
import com.ss.builder.plugin.api.editor.Advanced3dFileEditor;
import org.jetbrains.annotations.NotNull;

/**
 * The advanced implementation of 3D part of an editor.
 *
 * @author JavaSaBr
 */
public abstract class Advanced3dFileEditor3dEditorPart<T extends Advanced3dFileEditor> extends Base3dSceneEditor3dPart<T> {

    public Advanced3dFileEditor3dEditorPart(@NotNull T fileEditor) {
        super(fileEditor);
    }
}
