package com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.influerencer;

import static java.util.Objects.requireNonNull;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.node.Toneg0dParticleInfluencers;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.toneg0d.influencer.Toneg0DParticleInfluencerTreeNode;
import com.ss.editor.ui.control.model.node.spatial.particle.emitter.toneg0d.influencer.Toneg0DParticleInfluencersTreeNode;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.particle.emitter.toneg0d.RemoveParticleInfluencerOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import com.ss.rlib.util.array.Array;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The action for to remove the {@link ParticleInfluencer} from the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class RemoveParticleInfluencerAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Remove particle influencer action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public RemoveParticleInfluencerAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
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

    @FXThread
    @Override
    protected void process() {

        final Toneg0DParticleInfluencerTreeNode node = (Toneg0DParticleInfluencerTreeNode) getNode();
        final Toneg0DParticleInfluencersTreeNode parent = (Toneg0DParticleInfluencersTreeNode) requireNonNull(node.getParent());
        final Toneg0dParticleInfluencers toneg0dParticleInfluencers = requireNonNull(parent.getElement());

        final ParticleInfluencer influencer = node.getElement();
        final ParticleEmitterNode emitterNode = toneg0dParticleInfluencers.getEmitterNode();
        final Array<ParticleInfluencer> influencers = emitterNode.getInfluencers();
        final int childIndex = influencers.indexOf(influencer);

        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ModelChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new RemoveParticleInfluencerOperation(influencer, emitterNode, childIndex));
    }
}
