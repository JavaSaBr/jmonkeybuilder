package com.ss.editor.ui.control.tree.action.impl.particle.emitter;

import static com.ss.editor.util.NodeUtils.visitSpatial;
import com.jme3.effect.ParticleEmitter;
import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * The action to reset a {@link ParticleEmitter}.
 *
 * @author JavaSaBr
 */
public class ResetParticleEmittersAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * The list of additional action on executing this action.
     */
    @NotNull
    private static final Array<Function<@NotNull Node, @Nullable Runnable>> ADDITIONAL_ACTIONS = ArrayFactory.newArray(Function.class);

    /**
     * Register the additional action on executing this action.
     *
     * @param action the additional action on executing this action.
     */
    @FxThread
    public static void registerAdditionalAction(@NotNull final Function<@NotNull Node, @Nullable Runnable> action) {
        ADDITIONAL_ACTIONS.add(action);
    }

    public ResetParticleEmittersAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
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

        final TreeNode<?> treeNode = getNode();
        final Node node = (Node) treeNode.getElement();

        EXECUTOR_MANAGER.addJmeTask(() -> visitSpatial(node, ParticleEmitter.class, ParticleEmitter::killAllParticles));
        ADDITIONAL_ACTIONS.forEach(node, (factory, toCheck) -> {
            final Runnable action = factory.apply(toCheck);
            if (action != null) {
                EXECUTOR_MANAGER.addJmeTask(action);
            }
        });
    }
}
