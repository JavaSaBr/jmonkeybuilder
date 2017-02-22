package com.ss.editor.ui.control.model.tree.action.emitter.influerencer;

import static java.util.Objects.requireNonNull;

import com.ss.editor.Messages;
import com.ss.editor.model.node.ParticleInfluencers;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.RemoveParticleInfluencerOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.control.model.node.spatial.emitter.ParticleInfluencerModelNode;
import com.ss.editor.ui.control.model.node.spatial.emitter.ParticleInfluencersModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import rlib.util.array.Array;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The action for to remove the {@link ParticleInfluencer} from the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class RemoveParticleInfluencerAction extends AbstractNodeAction<ModelChangeConsumer> {

    public RemoveParticleInfluencerAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.REMOVE_12;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_REMOVE;
    }

    @Override
    protected void process() {

        final ParticleInfluencerModelNode node = (ParticleInfluencerModelNode) getNode();
        final ParticleInfluencersModelNode parent = (ParticleInfluencersModelNode) requireNonNull(node.getParent());
        final ParticleInfluencers particleInfluencers = requireNonNull(parent.getElement());

        final ParticleInfluencer influencer = node.getElement();
        final ParticleEmitterNode emitterNode = particleInfluencers.getEmitterNode();
        final Array<ParticleInfluencer> influencers = emitterNode.getInfluencers();
        final int childIndex = influencers.indexOf(influencer);

        final AbstractNodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ModelChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new RemoveParticleInfluencerOperation(influencer, emitterNode, childIndex));
    }
}
