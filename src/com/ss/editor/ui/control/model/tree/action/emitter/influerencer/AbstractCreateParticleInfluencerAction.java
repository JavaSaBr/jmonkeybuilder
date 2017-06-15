package com.ss.editor.ui.control.model.tree.action.emitter.influerencer;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.node.ParticleInfluencers;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.AddParticleInfluencerOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import javafx.scene.image.Image;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The action to create a {@link ParticleInfluencer} for a {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractCreateParticleInfluencerAction extends AbstractNodeAction<ModelChangeConsumer> {

    public AbstractCreateParticleInfluencerAction(@NotNull final AbstractNodeTree<ModelChangeConsumer> nodeTree, @NotNull final ModelNode<?> node) {
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

        final AbstractNodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ModelChangeConsumer changeConsumer = Objects.requireNonNull(nodeTree.getChangeConsumer());

        final ModelNode<?> modelNode = getNode();
        final ParticleInfluencers element = (ParticleInfluencers) modelNode.getElement();
        final ParticleEmitterNode emitterNode = element.getEmitterNode();
        final ParticleInfluencer influencer = createInfluencer();

        changeConsumer.execute(new AddParticleInfluencerOperation(influencer, emitterNode));
    }

    @NotNull
    protected abstract ParticleInfluencer createInfluencer();
}
