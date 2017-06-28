package com.ss.editor.ui.control.model.tree.action.animation;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.control.anim.AnimationModelNode;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to pause an animation.
 *
 * @author JavaSaBr
 */
public class PauseAnimationAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Pause animation action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public PauseAnimationAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return "Pause"; //Messages.MODEL_NODE_TREE_ACTION_ANIMATION_STOP;
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.PAUSE_16;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final AnimationModelNode modelNode = (AnimationModelNode) getNode();
        if (modelNode.getChannel() < 0) return;

        final AnimControl control = modelNode.getControl();
        if (control == null || control.getNumChannels() <= 0) return;

        final AnimChannel channel = control.getChannel(modelNode.getChannel());
        channel.setSpeed(0);
        modelNode.setSpeed(0);

        final AbstractNodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        nodeTree.update(modelNode);
    }
}
