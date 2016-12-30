package com.ss.editor.ui.control.model.tree.action.animation;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to stop an animation.
 *
 * @author JavaSaBr
 */
public class StopAnimationAction extends AbstractNodeAction {

    public StopAnimationAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return "Stop";
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.STOP_16;
    }

    @Override
    protected void process() {

        final AnimationModelNode modelNode = (AnimationModelNode) getNode();
        if (modelNode.getChannel() < 0) return;

        final AnimControl control = modelNode.getControl();
        if (control.getNumChannels() <= 0) return;

        final AnimChannel channel = control.getChannel(modelNode.getChannel());
        channel.setLoopMode(LoopMode.DontLoop);

        EXECUTOR_MANAGER.addEditorThreadTask(control::clearChannels);

        final ModelNodeTree nodeTree = getNodeTree();
        nodeTree.update(modelNode);
    }
}
