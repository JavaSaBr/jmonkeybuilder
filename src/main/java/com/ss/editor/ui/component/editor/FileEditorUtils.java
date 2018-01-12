package com.ss.editor.ui.component.editor;

import com.jme3.math.Vector3f;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.state.editor.impl.AdvancedAbstractEditor3DState;
import com.ss.editor.ui.component.editor.state.impl.Editor3DEditorState;
import org.jetbrains.annotations.NotNull;

/**
 * The utility class.
 *
 * @author JavaSaBr
 */
public class FileEditorUtils {

    /**
     * The constant EXECUTOR_MANAGER.
     */
    @NotNull
    protected static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    public static void loadCameraState(@NotNull final Editor3DEditorState editorState,
                                       @NotNull final AdvancedAbstractEditor3DState editor3DState) {

        final Vector3f cameraLocation = editorState.getCameraLocation();

        final float hRotation = editorState.getCameraHRotation();
        final float vRotation = editorState.getCameraVRotation();
        final float tDistance = editorState.getCameraTDistance();
        final float cameraSpeed = editorState.getCameraSpeed();

        EXECUTOR_MANAGER.addJmeTask(() -> editor3DState.updateCameraSettings(cameraLocation, hRotation, vRotation, tDistance, cameraSpeed));
    }
}
