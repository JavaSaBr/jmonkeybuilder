package com.ss.builder.plugin.api.editor;

import static com.ss.builder.editor.FileEditorUtils.loadCameraState;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.editor.part3d.Advanced3dFileEditor3dEditorPart;
import com.ss.builder.editor.state.impl.Editor3dEditorState;

/**
 * The advanced implementation of 3D editor.
 *
 * @author JavaSaBr
 */
public abstract class Advanced3dFileEditor<T extends Advanced3dFileEditor3dEditorPart, S extends Editor3dEditorState>
        extends Base3dFileEditor<T, S> {

    @Override
    @FxThread
    protected void loadState() {
        super.loadState();

        var editorState = getEditorState();

        if (editorState != null) {
            loadCameraState(editorState, editor3dPart);
        }
    }
}
