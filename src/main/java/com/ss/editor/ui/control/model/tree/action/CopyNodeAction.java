package com.ss.editor.ui.control.model.tree.action;

import static com.ss.editor.ui.control.tree.NodeTreeCell.DATA_FORMAT;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to copy a node in model.
 *
 * @author JavaSaBr
 */
public class CopyNodeAction extends AbstractNodeAction<ModelChangeConsumer> {

    public CopyNodeAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FXThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_COPY;
    }

    @Override
    @FXThread
    protected @Nullable Image getIcon() {
        return Icons.COPY_16;
    }

    @Override
    @FXThread
    protected void process() {
        super.process();

        final TreeNode<?> node = getNode();

        final ClipboardContent content = new ClipboardContent();
        content.put(DATA_FORMAT, node.getObjectId());

        final Clipboard clipboard = Clipboard.getSystemClipboard();
        clipboard.setContent(content);
    }
}
