package com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.influerencer;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.node.particles.Toneg0dParticleInfluencers;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.operation.particle.emitter.toneg0d.AddParticleInfluencerOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The action to create a {@link ParticleInfluencer} for a {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractCreateParticleInfluencerAction extends AbstractNodeAction<ModelChangeConsumer> {

    public AbstractCreateParticleInfluencerAction(@NotNull final NodeTree<ModelChangeConsumer> nodeTree,
                                                  @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.INFLUENCER_16;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ModelChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());

        final TreeNode<?> treeNode = getNode();
        final Toneg0dParticleInfluencers element = (Toneg0dParticleInfluencers) treeNode.getElement();
        final ParticleEmitterNode emitterNode = element.getEmitterNode();
        final ParticleInfluencer influencer = createInfluencer();

        changeConsumer.execute(new AddParticleInfluencerOperation(influencer, emitterNode));
    }

    /**
     * Create influencer particle influencer.
     *
     * @return the particle influencer
     */
    @FxThread
    protected abstract @NotNull ParticleInfluencer createInfluencer();
}
