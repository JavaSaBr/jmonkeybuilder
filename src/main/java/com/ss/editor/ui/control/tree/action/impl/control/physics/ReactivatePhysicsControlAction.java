package com.ss.editor.ui.control.tree.action.impl.control.physics;

import com.jme3.bullet.control.RigidBodyControl;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to re-activate a physics control.
 *
 * @author JavaSaBr
 */
public class ReactivatePhysicsControlAction extends AbstractNodeAction<ModelChangeConsumer> {

    public ReactivatePhysicsControlAction(@NotNull NodeTree<?> nodeTree, @NotNull TreeNode<?> node) {
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
        return Messages.MODEL_NODE_TREE_ACTION_REACTIVATE;
    }

    @Override
    @FxThread
    protected void process() {

        var element = getNode()
                .getElement();

        if (element instanceof RigidBodyControl) {
            var control = (RigidBodyControl) element;
            ExecutorManager.getInstance()
                    .addJmeTask(control::activate);
        }
    }
}
