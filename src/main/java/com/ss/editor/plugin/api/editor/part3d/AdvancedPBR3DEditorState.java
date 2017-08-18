package com.ss.editor.plugin.api.editor.part3d;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.environment.generation.JobProgressAdapter;
import com.jme3.light.LightProbe;
import com.jme3.scene.Node;
import com.ss.editor.plugin.api.editor.Advanced3DFileEditor;
import org.jetbrains.annotations.NotNull;

/**
 * The advanced implementation of 3D part of an editor.
 *
 * @author JavaSaBr
 */
public abstract class AdvancedPBR3DEditorState<T extends Advanced3DFileEditor> extends Advanced3DEditorState<T> {

    @NotNull
    private final JobProgressAdapter<LightProbe> probeHandler = new JobProgressAdapter<LightProbe>() {

        @Override
        public void done(final LightProbe result) {
            if (!isInitialized()) return;
            attachModelNode();
        }
    };

    /**
     * The model node.
     */
    @NotNull
    private final Node modelNode;

    /**
     * The count of frames.
     */
    private int frame;

    public AdvancedPBR3DEditorState(@NotNull final T fileEditor) {
        super(fileEditor);
        this.modelNode = new Node("ModelNode");
    }

    @Override
    public void initialize(@NotNull final AppStateManager stateManager, @NotNull final Application application) {
        super.initialize(stateManager, application);
        frame = 0;
    }

    @Override
    public void cleanup() {
        super.cleanup();

        final Node modelNode = getModelNode();
        modelNode.detachAllChildren();

        final Node stateNode = getStateNode();
        stateNode.detachChild(modelNode);
    }

    /**
     * Attach model node to state node.
     */
    private void attachModelNode() {
        final Node stateNode = getStateNode();
        stateNode.attachChild(modelNode);
    }

    /**
     * @return the model node.
     */
    protected @NotNull Node getModelNode() {
        return modelNode;
    }

    @Override
    public void update(final float tpf) {
        super.update(tpf);

        if (frame == 2) {
            EDITOR.updateLightProbe(probeHandler);
        }

        frame++;
    }
}
