package com.ss.editor.ui.control.model.tree.action;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.Messages;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.node.spatial.NodeTreeNode;
import com.ss.editor.ui.control.model.node.spatial.SpatialTreeNode;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;

import static com.ss.editor.util.EditorUtil.getDefaultLayer;
import static com.ss.rlib.util.ObjectUtils.notNull;

public class PasteNodeAction extends AbstractNodeAction<ModelChangeConsumer> {

    public PasteNodeAction(@NotNull NodeTree<?> nodeTree, @NotNull TreeNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_PASTE;
    }

    @Override
    protected void process() {
        super.process();

        if(DataCopy.copySpatial != null){
            final ChangeConsumer consumer = notNull(this.getNodeTree().getChangeConsumer());
            consumer.execute(new AddChildOperation(((Spatial)DataCopy.copySpatial.getElement()).clone(),(Node)this.getNode().getElement()));
        }

    }
}
