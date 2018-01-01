package com.ss.editor.ui.control.model.tree.action;

import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.node.spatial.GeometryTreeNode;
import com.ss.editor.ui.control.model.node.spatial.NodeTreeNode;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;

public class CopyNodeAction extends AbstractNodeAction<ModelChangeConsumer> {

    public CopyNodeAction(@NotNull NodeTree<?> nodeTree, @NotNull TreeNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_COPY;
    }

    @Override
    protected void process() {
        super.process();
        if(this.getNode()  instanceof GeometryTreeNode)
            DataCopy.setCopyGeom((GeometryTreeNode)this.getNode());
        else
            DataCopy.setCopySpatial((NodeTreeNode) this.getNode());
    }
}
