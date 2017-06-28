package com.ss.editor.ui.control.model.tree.action.control.physics;

import com.jme3.bullet.control.RigidBodyControl;
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
    public ReactivatePhysicsControl(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
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
        return Messages.MODEL_NODE_TREE_ACTION_REACTIVATE;
    }

    @FXThread
    @Override
    protected void process() {

        final Object element = getNode().getElement();

        if (element instanceof RigidBodyControl) {
            final RigidBodyControl control = (RigidBodyControl) element;
            EXECUTOR_MANAGER.addEditorThreadTask(control::activate);
        }
    }
}
