package com.ss.builder.fx.control.tree.action.impl;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.dialog.sky.CreateSkyDialog;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.dialog.sky.CreateSkyDialog;
import com.ss.builder.fx.control.tree.NodeTree;
import com.ss.builder.fx.control.tree.action.AbstractNodeAction;
import com.ss.builder.fx.control.tree.node.TreeNode;
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
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.SKY_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_SKY;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();

        final CreateSkyDialog dialog = createDialog();
        dialog.show();
    }

    @FxThread
    protected @NotNull CreateSkyDialog createDialog() {
        return new CreateSkyDialog(getNode(), getNodeTree());
    }
}
