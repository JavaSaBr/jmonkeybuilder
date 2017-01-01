package com.ss.editor.ui.control.model.tree.action.audio;

import com.jme3.audio.AudioNode;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.spatial.AudioModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to stop an audio node.
 *
 * @author JavaSaBr
 */
public class StopAudioNodeAction extends AbstractNodeAction {

    public StopAudioNodeAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.STOP_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return "Stop";
    }

    @Override
    protected void process() {

        final AudioModelNode audioModelNode = (AudioModelNode) getNode();
        final AudioNode audioNode = audioModelNode.getElement();

        EXECUTOR_MANAGER.addEditorThreadTask(audioNode::stop);
    }
}
