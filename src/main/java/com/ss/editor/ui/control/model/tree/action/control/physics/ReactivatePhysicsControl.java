package com.ss.editor.ui.control.model.tree.action.control.physics;

import com.jme3.bullet.control.RigidBodyControl;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
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
public class ReactivatePhysicsControl extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Reactivate physics control.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public ReactivatePhysicsControl(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @FxThread
    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.REPLAY_16;
    }

    @FxThread
    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_REACTIVATE;
    }

    @FxThread
    @Override
    protected void process() {

        final Object element = getNode().getElement();

        if (element instanceof RigidBodyControl) {
            final RigidBodyControl control = (RigidBodyControl) element;
            EXECUTOR_MANAGER.addJMETask(control::activate);
        }
    }
}
