package com.ss.editor.ui.control.tree.action.impl.terrain;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.dialog.terrain.CreateTerrainDialog;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to create terrain.
 *
 * @author JavaSaBr
 */
public class CreateTerrainAction extends AbstractNodeAction<ModelChangeConsumer> {

    public CreateTerrainAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.TERRAIN_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_ADD_TERRAIN;
    }

    @Override
    @FxThread
    protected void process() {
        super.process();
        final CreateTerrainDialog dialog = new CreateTerrainDialog(getNode(), getNodeTree());
        dialog.show();
    }
}
