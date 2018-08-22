package com.ss.builder.ui.dialog.sky;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;

/**
 * The dialog to create sky using SS Sky Factory.
 *
 * @author JavaSaBr
 */
public class CreateEditableSkyDialog extends CreateSkyDialog {

    public CreateEditableSkyDialog(@NotNull final TreeNode<?> parentNode,
                                   @NotNull final NodeTree<ModelChangeConsumer> nodeTree) {
        super(parentNode, nodeTree);
    }

    @Override
    protected boolean isEditableSky() {
        return true;
    }
}
