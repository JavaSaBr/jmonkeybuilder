package com.ss.editor.plugin.api.editor.part3d;

import com.jme3.math.Vector3f;
import com.ss.editor.annotation.JMEThread;
import com.ss.editor.plugin.api.editor.Advanced3DFileEditor;
import com.ss.editor.state.editor.impl.AdvancedAbstractEditor3DState;
import org.jetbrains.annotations.NotNull;

/**
 * The advanced implementation of 3D part of an editor.
 *
 * @author JavaSaBr
 */
public abstract class Advanced3DEditorState<T extends Advanced3DFileEditor> extends AdvancedAbstractEditor3DState<T> {

    public Advanced3DEditorState(@NotNull final T fileEditor) {
        super(fileEditor);
    }

    @Override
    @JMEThread
    protected void undo() {
        super.undo();
        getFileEditor().undo();
    }

    @Override
    @JMEThread
    protected void redo() {
        super.redo();
        getFileEditor().redo();
    }

    @Override
    protected void notifyChangedCameraSettings(@NotNull final Vector3f cameraLocation, final float hRotation, final float vRotation,
                                               final float targetDistance, final float cameraSpeed) {
        super.notifyChangedCameraSettings(cameraLocation, hRotation, vRotation, targetDistance, cameraSpeed);
        EXECUTOR_MANAGER.addFXTask(() -> getFileEditor().notifyChangedCameraSettings(cameraLocation, hRotation, vRotation, targetDistance, cameraSpeed));
    }
}
