package com.ss.editor.ui.control.tree.action.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to paste a node in model.
 *
 * @author JavaSaBr
 */
public class PasteNodeAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * The copied node.
     */
    @NotNull
    private final TreeNode<?> copiedNode;

    public PasteNodeAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node,
                           @NotNull final TreeNode<?> copiedNode) {
        super(nodeTree, node);
        this.copiedNode = copiedNode;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PASTE;
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.PASTE_16;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();
        final ModelChangeConsumer changeConsumer = notNull(getNodeTree().getChangeConsumer());
        final TreeNode<?> node = getNode();
        node.accept(changeConsumer, copiedNode.getElement(), true);
    }
}
