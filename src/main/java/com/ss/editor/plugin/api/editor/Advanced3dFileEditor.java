package com.ss.editor.plugin.api.editor;

import static com.ss.editor.ui.component.editor.FileEditorUtils.loadCameraState;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.editor.part3d.Advanced3dEditorPart;
import com.ss.editor.ui.component.editor.state.impl.Editor3dEditorState;

/**
 * The advanced implementation of 3D editor.
 *
 * @author JavaSaBr
 */
public abstract class Advanced3dFileEditor<T extends Advanced3dEditorPart, S extends Editor3dEditorState>
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
