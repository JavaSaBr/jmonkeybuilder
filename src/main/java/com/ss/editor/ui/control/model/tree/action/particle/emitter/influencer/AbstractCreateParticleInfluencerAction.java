package com.ss.editor.ui.control.model.tree.action.particle.emitter.influencer;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.particle.emitter.ChangeParticleInfluencerOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
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
    public AbstractCreateParticleInfluencerAction(@NotNull final AbstractNodeTree<?> nodeTree,
                                                  @NotNull final ModelNode<?> node) {
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

        final AbstractNodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ModelChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());

        final ModelNode<?> modelNode = getNode();
        final ParticleEmitter emitter = (ParticleEmitter) modelNode.getElement();
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
