package com.ss.editor.plugin.api.editor;

import static com.ss.editor.ui.component.editor.FileEditorUtils.loadCameraState;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.editor.part3d.Advanced3DEditorState;
import com.ss.editor.ui.component.editor.state.impl.Editor3DEditorState;

/**
 * The advanced implementation of 3D editor.
 *
 * @author JavaSaBr
 */
public abstract class Advanced3DFileEditor<T extends Advanced3DEditorState, S extends Editor3DEditorState>
        extends Base3DFileEditor<T, S> {

    @Override
    @FxThread
    protected void loadState() {
        super.loadState();

        final S editorState = getEditorState();
        if (editorState != null) {
            loadCameraState(editorState, getEditor3DState());
        }
    }
}
