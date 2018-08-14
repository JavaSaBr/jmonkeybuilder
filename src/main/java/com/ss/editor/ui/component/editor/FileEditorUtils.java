package com.ss.editor.ui.component.editor;

import com.ss.editor.part3d.editor.impl.Base3dSceneEditor3dPart;
import com.ss.editor.ui.component.editor.state.impl.Editor3dEditorState;
import org.jetbrains.annotations.NotNull;

/**
 * The utility class.
 *
 * @author JavaSaBr
 */
public class FileEditorUtils {

    public static void loadCameraState(
            @NotNull Editor3dEditorState editorState,
            @NotNull Base3dSceneEditor3dPart editor3dPart
    ) {

        var cameraLocation = editorState.getCameraLocation();

        var hRotation = editorState.getCameraHRotation();
        var vRotation = editorState.getCameraVRotation();
        var tDistance = editorState.getCameraTDistance();
        var cameraSpeed = editorState.getCameraSpeed();

        /** FIXME ExecutorManager.getInstance()
                .addJmeTask(() -> editor3dPart.applyState(cameraLocation, hRotation, vRotation, tDistance, cameraSpeed));*/
    }
}
