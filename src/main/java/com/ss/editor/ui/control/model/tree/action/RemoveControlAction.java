package com.ss.editor.ui.control.model.tree.action;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.operation.RemoveControlOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The implementation of the {@link AbstractNodeAction} to remove a control from a {@link Spatial}.
 *
 * @author JavaSaBr
 */
public class RemoveControlAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Remove control action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public RemoveControlAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_REMOVE;
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.REMOVE_12;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final TreeNode<?> node = getNode();
        final Object element = node.getElement();

        if (!(element instanceof Control)) return;
        final Control control = (Control) element;

        final TreeNode<?> parentNode = node.getParent();

        if (parentNode == null) {
            LOGGER.warning("not found parent node for " + node);
            return;
        }

        final Object parent = parentNode.getElement();

        final NodeTree<ModelChangeConsumer> nodeTree = getNodeTree();
        final ModelChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new RemoveControlOperation(control, (Spatial) parent));
    }
}
