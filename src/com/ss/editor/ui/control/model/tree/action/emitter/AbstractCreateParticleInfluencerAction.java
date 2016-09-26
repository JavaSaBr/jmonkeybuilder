package com.ss.editor.ui.control.model.tree.action.emitter;

import com.ss.editor.model.node.ParticleInfluencers;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.AddParticleInfluencerOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The action for creating the {@link ParticleInfluencer} for the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractCreateParticleInfluencerAction extends AbstractNodeAction {

    public AbstractCreateParticleInfluencerAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    protected void process() {

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();

        final ModelNode<?> modelNode = getNode();
        final ParticleInfluencers element = (ParticleInfluencers) modelNode.getElement();
        final ParticleEmitterNode emitterNode = element.getEmitterNode();

        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), emitterNode);
        final ParticleInfluencer influencer = createInfluencer();

        modelChangeConsumer.execute(new AddParticleInfluencerOperation(influencer, index));
    }

    @NotNull
    protected abstract ParticleInfluencer createInfluencer();
}
