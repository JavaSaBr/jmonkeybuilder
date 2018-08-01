package com.ss.editor.plugin.api.editor.part3d;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.light.LightProbe;
import com.jme3.scene.Node;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.plugin.api.editor.Advanced3dFileEditor;
import com.ss.editor.util.EditorUtils;
import org.jetbrains.annotations.NotNull;

/**
 * The advanced implementation of 3D part of an editor with PBR Light probe.
 *
 * @author JavaSaBr
 */
public abstract class AdvancedPbr3dEditorPart<T extends Advanced3dFileEditor> extends Advanced3dEditorPart<T> {

    @NotNull
    private final JobProgressAdapter<LightProbe> probeHandler = new JobProgressAdapter<>() {

        @Override
        public void done(@NotNull LightProbe result) {
            if (!isInitialized()) return;
            attachModelNode();
        }
    };

    /**
     * The model node.
     */
    @NotNull
    protected final Node modelNode;

    /**
     * The count of frames.
     */
    private int frame;

    public AdvancedPbr3dEditorPart(@NotNull T fileEditor) {
        super(fileEditor);
        this.modelNode = new Node("ModelNode");
    }

    @Override
    @JmeThread
    public void initialize(@NotNull AppStateManager stateManager, @NotNull Application application) {
        super.initialize(stateManager, application);
        frame = 0;
    }

    @Override
    @JmeThread
    public void cleanup() {
        super.cleanup();
        modelNode.detachAllChildren();
        stateNode.detachChild(modelNode);
    }

    /**
     * Attach model node to state node.
     */
    @JmeThread
    private void attachModelNode() {
        stateNode.attachChild(modelNode);
    }

    @Override
    @JmeThread
    public void update(float tpf) {
        super.update(tpf);

        if (frame == 2) {
            EditorUtils.updateGlobalLightProbe(probeHandler);
        }

        frame++;
    }
}
