package com.ss.editor.plugin.api.editor.part3d;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.part3d.editor.impl.AdvancedAbstractEditor3dPart;
import com.ss.editor.plugin.api.editor.Advanced3dFileEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The advanced implementation of 3D part of an editor.
 *
 * @author JavaSaBr
 */
public abstract class Advanced3dEditorPart<T extends Advanced3dFileEditor> extends AdvancedAbstractEditor3dPart<T> {

    /**
     * The node on which the camera is looking.
     */
    @Nullable
    private Node cameraNode;

    public Advanced3dEditorPart(@NotNull T fileEditor) {
        super(fileEditor);
        stateNode.attachChild(getCameraNode());
    }

    @Override
    @FromAnyThread
    protected @NotNull Node getNodeForCamera() {

        if (cameraNode == null) {
            cameraNode = new Node("CameraNode");
        }

        return cameraNode;
    }

    /**
     * Get the node on which the camera is looking.
     *
     * @return the node on which the camera is looking.
     */
    @FromAnyThread
    protected @NotNull Node getCameraNode() {
        return notNull(cameraNode);
    }

    @Override
    @JmeThread
    protected void undo() {
        super.undo();
        fileEditor.undo();
    }

    @Override
    @JmeThread
    protected void redo() {
        super.redo();
        fileEditor.redo();
    }

    @Override
    @JmeThread
    protected void notifyChangedCameraSettings(
            @NotNull Vector3f cameraLocation,
            float hRotation,
            float vRotation,
            float targetDistance,
            float cameraSpeed
    ) {
        super.notifyChangedCameraSettings(cameraLocation, hRotation, vRotation, targetDistance, cameraSpeed);

        ExecutorManager.getInstance()
                .addFxTask(() -> fileEditor.notifyChangedCameraSettings(cameraLocation, hRotation, vRotation, targetDistance, cameraSpeed));
    }
}
