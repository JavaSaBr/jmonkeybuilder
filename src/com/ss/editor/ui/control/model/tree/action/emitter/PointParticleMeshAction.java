package com.ss.editor.ui.control.model.tree.action.emitter;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.ChangeParticleMeshOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.geometry.ParticleGeometry;
import tonegod.emitter.particle.ParticleDataMeshInfo;
import tonegod.emitter.particle.ParticleDataPointMesh;

/**
 * The action for switching the particle mesh of the {@link ParticleGeometry} to {@link
 * ParticleDataPointMesh}.
 *
 * @author JavaSaBr
 */
public class PointParticleMeshAction extends AbstractNodeAction {

    public PointParticleMeshAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return "Point";
    }

    @Override
    protected void process() {

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();

        final ModelNode<?> modelNode = getNode();
        final ParticleGeometry element = (ParticleGeometry) modelNode.getElement();

        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), element);
        final ParticleDataMeshInfo meshInfo = new ParticleDataMeshInfo(ParticleDataPointMesh.class, null);

        modelChangeConsumer.execute(new ChangeParticleMeshOperation(meshInfo, index));
    }
}
