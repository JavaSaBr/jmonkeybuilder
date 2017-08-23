package com.ss.editor.ui.control.model.tree.action.audio;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to create an audio node.
 *
 * @author JavaSaBr
 */
public class CreateAudioNodeAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Create audio node action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateAudioNodeAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.AUDIO_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_AUDIO_NODE;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final NodeTree<?> nodeTree = getNodeTree();

        final AudioNode node = new AudioNode();
        node.setName("New audio");

        final TreeNode<?> treeNode = getNode();
        final Node parent = (Node) treeNode.getElement();

        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new AddChildOperation(node, parent));
    }
}
