package com.ss.editor.ui.control.model.tree.action.particle.emitter;

import static com.ss.editor.util.NodeUtils.visitSpatial;
import com.jme3.effect.ParticleEmitter;
import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.ParticleEmitterNode;

/**
 * The action to reset a {@link ParticleEmitterNode} and a {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public class ResetParticleEmittersAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Reset toneg 0 d particle emitters action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public ResetParticleEmittersAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.REPLAY_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_RESET_PARTICLE_EMITTERS;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final ModelNode<?> modelNode = getNode();
        final Node node = (Node) modelNode.getElement();

        EXECUTOR_MANAGER.addEditorThreadTask(() -> visitSpatial(node, ParticleEmitterNode.class, ParticleEmitterNode::reset));
        EXECUTOR_MANAGER.addEditorThreadTask(() -> visitSpatial(node, ParticleEmitter.class, ParticleEmitter::killAllParticles));
    }
}
