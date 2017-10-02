package com.ss.editor.ui.control.model.tree.action;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.dialog.sky.CreateSkyDialog;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create sky.
 *
 * @author JavaSaBr
 */
public class CreateSkyAction extends AbstractNodeAction<ModelChangeConsumer> {

    public CreateSkyAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FXThread
    protected @Nullable Image getIcon() {
        return Icons.SKY_16;
    }

    @Override
    @FXThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_SKY;
    }

    @Override
    @FXThread
    protected void process() {
        super.process();

        final CreateSkyDialog dialog = createDialog();
        dialog.show();
    }

    @FXThread
    protected @NotNull CreateSkyDialog createDialog() {
        return new CreateSkyDialog(getNode(), getNodeTree());
    }
}
