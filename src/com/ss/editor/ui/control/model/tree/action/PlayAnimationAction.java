package com.ss.editor.ui.control.model.tree.action;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Animation;
import com.jme3.animation.LoopMode;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.control.anim.AnimationModelNode;

import org.jetbrains.annotations.NotNull;

import rlib.util.StringUtils;

/**
 * Реализация действия по запуску анимации.
 *
 * @author Ronn
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

    @Override
    protected void process() {

        final AnimationModelNode modelNode = (AnimationModelNode) getNode();
        if (modelNode.getChannel() >= 0) return;

        final Animation element = modelNode.getElement();
        final AnimControl control = modelNode.getControl();
        if (control.getNumChannels() > 0) return;

        modelNode.setChannel(control.getNumChannels());

        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            control.addListener(this);

            final AnimChannel channel = control.createChannel();
            channel.setAnim(element.getName());
            channel.setLoopMode(LoopMode.DontLoop);
            channel.setSpeed(1);
        });

        final ModelNodeTree nodeTree = getNodeTree();
        nodeTree.update(modelNode);
    }

    @Override
    public void onAnimCycleDone(final AnimControl control, final AnimChannel channel, final String animName) {

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
