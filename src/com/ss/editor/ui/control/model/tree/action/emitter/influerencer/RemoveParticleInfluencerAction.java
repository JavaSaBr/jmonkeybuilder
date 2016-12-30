package com.ss.editor.ui.control.model.tree.action.emitter.influerencer;

import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.RemoveParticleInfluencerOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.emitter.ParticleInfluencerModelNode;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The action for to remove the {@link ParticleInfluencer} from the {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class RemoveParticleInfluencerAction extends AbstractNodeAction {

    public RemoveParticleInfluencerAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.REMOVE_18;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_REMOVE;
    }

    @Override
    protected void process() {

        final ParticleInfluencerModelNode node = (ParticleInfluencerModelNode) getNode();
        final ParticleInfluencer influencer = node.getElement();
        final ParticleEmitterNode emitterNode = node.getEmitterNode();

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();

        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), emitterNode);
        final int childIndex = emitterNode.indexOfInfluencer(influencer);

        modelChangeConsumer.execute(new RemoveParticleInfluencerOperation(influencer, index, childIndex));
    }
}
