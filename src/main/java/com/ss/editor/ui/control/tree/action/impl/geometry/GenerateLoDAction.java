package com.ss.editor.ui.control.tree.action.impl.geometry;

import com.jme3.scene.Geometry;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.impl.spatial.GeometryTreeNode;
import com.ss.editor.ui.control.tree.action.AbstractNodeAction;
import com.ss.editor.ui.dialog.geometry.lod.GenerateLodLevelsDialog;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.ClassUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The action to generate levels of details for the geometry.
 *
 * @author JavaSaBr
 */
public class GenerateLoDAction extends AbstractNodeAction<ModelChangeConsumer> {

    public GenerateLoDAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.MESH_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_LOD_GENERATOR;
    }

    @Override
    @FxThread
    protected void process() {
        final GeometryTreeNode<Geometry> modelNode = ClassUtils.unsafeCast(getNode());
        final Geometry geometry = modelNode.getElement();
        final GenerateLodLevelsDialog dialog = new GenerateLodLevelsDialog(getNodeTree(), geometry);
        dialog.show();
    }
}
