package com.ss.editor.ui.control.model.tree.action.particle.emitter.influencer;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.particle.emitter.ChangeParticleInfluencerOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create a {@link ParticleInfluencer} for a {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractCreateParticleInfluencerAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Abstract create particle influencer action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public AbstractCreateParticleInfluencerAction(@NotNull final NodeTree<?> nodeTree,
                                                  @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.INFLUENCER_16;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ModelChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());

        final TreeNode<?> treeNode = getNode();
        final ParticleEmitter emitter = (ParticleEmitter) treeNode.getElement();
        final ParticleInfluencer influencer = createInfluencer();

        changeConsumer.execute(new ChangeParticleInfluencerOperation(influencer, emitter));
    }

    /**
     * Create influencer particle influencer.
     *
     * @return the particle influencer
     */
    @NotNull
    protected abstract ParticleInfluencer createInfluencer();
}
