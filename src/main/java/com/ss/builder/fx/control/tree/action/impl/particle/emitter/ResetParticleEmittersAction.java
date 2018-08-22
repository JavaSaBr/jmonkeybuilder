package com.ss.builder.ui.control.tree.action.impl.particle.emitter;

import static com.ss.editor.util.NodeUtils.visitSpatial;
import com.jme3.effect.ParticleEmitter;
import com.jme3.scene.Node;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.ui.Icons;
import com.ss.builder.util.NodeUtils;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to reset a {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public class ResetParticleEmittersAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * @see AdditionalAction
     */
    public static final String EP_ADDITIONAL_ACTIONS = "ResetParticleEmittersAction#additionalActions";

    private static final ExtensionPoint<AdditionalAction<Node>> ADDITIONAL_ACTIONS =
            ExtensionPointManager.register(EP_ADDITIONAL_ACTIONS);

    public ResetParticleEmittersAction(@NotNull NodeTree<?> nodeTree, @NotNull TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.REPLAY_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_RESET_PARTICLE_EMITTERS;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        var treeNode = getNode();
        var node = (Node) treeNode.getElement();

        ExecutorManager.getInstance()
                .addJmeTask(() -> NodeUtils.visitSpatial(node, ParticleEmitter.class, ParticleEmitter::killAllParticles));

        ADDITIONAL_ACTIONS.forEach(node, (factory, toCheck) -> {

            var executorManager = ExecutorManager.getInstance();
            var action = factory.makeAction(toCheck);

            if (action != null) {
                executorManager.addJmeTask(action);
            }
        });
    }
}
