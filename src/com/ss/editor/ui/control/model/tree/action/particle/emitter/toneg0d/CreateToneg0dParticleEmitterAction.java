package com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d;

import static java.util.Objects.requireNonNull;

import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.impl.AlphaInfluencer;
import tonegod.emitter.influencers.impl.ColorInfluencer;
import tonegod.emitter.influencers.impl.SizeInfluencer;

/**
 * The action for creating new {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class CreateToneg0dParticleEmitterAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Create toneg 0 d emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateToneg0dParticleEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.EMITTER_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_TONEG0D_PARTICLE_EMITTER;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final AbstractNodeTree<?> nodeTree = getNodeTree();

        final ParticleEmitterNode emitter = createEmitterNode();
        emitter.addInfluencers(new ColorInfluencer(), new AlphaInfluencer(), new SizeInfluencer());
        emitter.setEnabled(true);

        final SizeInfluencer sizeInfluencer = emitter.getInfluencer(SizeInfluencer.class);

        if (sizeInfluencer != null) {
            sizeInfluencer.addSize(0.1f);
            sizeInfluencer.addSize(0f);
        }

        final ModelNode<?> modelNode = getNode();
        final Node parent = (Node) modelNode.getElement();

        final ChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new AddChildOperation(emitter, parent));
    }

    /**
     * Create emitter node particle emitter node.
     *
     * @return the particle emitter node
     */
    @NotNull
    protected ParticleEmitterNode createEmitterNode() {
        return new ParticleEmitterNode(EDITOR.getAssetManager());
    }
}
