package com.ss.builder.editor;

import com.ss.builder.jme.editor.part3d.impl.Base3dSceneEditor3dPart;
import com.ss.builder.editor.state.impl.Editor3dEditorState;
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
        var tDistance = editorState.getCameraTargetDistance();
        var cameraSpeed = editorState.getCameraFlySpeed();

        /** FIXME ExecutorManager.getInstance()
                .addJmeTask(() -> editor3dPart.applyState(cameraLocation, hRotation, vRotation, tDistance, cameraFlySpeed));*/
    }
}
