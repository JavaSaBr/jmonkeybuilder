package com.ss.editor.plugin.api.editor.part3d;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.plugin.api.editor.Advanced3DFileEditor;
import com.ss.editor.part3d.editor.impl.AdvancedAbstractEditor3DPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The advanced implementation of 3D part of an editor.
 *
 * @author JavaSaBr
 */
public abstract class Advanced3DEditorPart<T extends Advanced3DFileEditor> extends AdvancedAbstractEditor3DPart<T> {

    /**
     * The node on which the camera is looking.
     */
    @Nullable
    private Node cameraNode;

    public Advanced3DEditorPart(@NotNull final T fileEditor) {
        super(fileEditor);

        final Node stateNode = getStateNode();
        stateNode.attachChild(getCameraNode());
    }

    @Override
    @FromAnyThread
    protected @NotNull Node getNodeForCamera() {
        if (cameraNode == null) cameraNode = new Node("CameraNode");
        return cameraNode;
    }

    /**
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
        getFileEditor().undo();
    }

    @Override
    @JmeThread
    protected void redo() {
        super.redo();
        getFileEditor().redo();
    }

    @Override
    @JmeThread
    protected void notifyChangedCameraSettings(@NotNull final Vector3f cameraLocation, final float hRotation,
                                               final float vRotation, final float targetDistance,
                                               final float cameraSpeed) {
        super.notifyChangedCameraSettings(cameraLocation, hRotation, vRotation, targetDistance, cameraSpeed);
        EXECUTOR_MANAGER.addFxTask(() -> getFileEditor().notifyChangedCameraSettings(cameraLocation, hRotation, vRotation, targetDistance, cameraSpeed));
    }
}
