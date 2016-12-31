package com.ss.editor.ui.control.model.tree.action.animation;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Animation;
import com.jme3.animation.LoopMode;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationControlModelNode;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import rlib.util.StringUtils;

/**
 * The action to play an animation.
 *
 * @author JavaSaBr
 */
public class PlayAnimationAction extends AbstractNodeAction implements AnimEventListener {

    public PlayAnimationAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ANIMATION_PLAY;
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.PLAY_16;
    }

    @Override
    protected void process() {

        final AnimationModelNode modelNode = (AnimationModelNode) getNode();
        final AnimationControlModelNode controlModelNode = modelNode.getControlModelNode();
        if (controlModelNode == null || modelNode.getChannel() >= 0) return;

        final Animation element = modelNode.getElement();
        final AnimControl control = modelNode.getControl();
        if (control == null || control.getNumChannels() > 0) return;

        modelNode.setChannel(control.getNumChannels());

        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            control.addListener(this);

            final AnimChannel channel = control.createChannel();
            channel.setAnim(element.getName());
            channel.setLoopMode(controlModelNode.getLoopMode());
            channel.setSpeed(controlModelNode.getSpeed());
        });

        final ModelNodeTree nodeTree = getNodeTree();
        nodeTree.update(modelNode);
    }

    @Override
    public void onAnimCycleDone(final AnimControl control, final AnimChannel channel, final String animName) {
        if (channel.getLoopMode() != LoopMode.DontLoop) return;

        final AnimationModelNode modelNode = (AnimationModelNode) getNode();
        final Animation element = modelNode.getElement();
        if (!StringUtils.equals(element.getName(), animName)) return;

        modelNode.setChannel(-1);

        EXECUTOR_MANAGER.addFXTask(() -> getNodeTree().update(modelNode));
        EXECUTOR_MANAGER.addEditorThreadTask(control::clearChannels);
    }

    @Override
    public void onAnimChange(final AnimControl control, final AnimChannel channel, final String animName) {
    }
}
