package com.ss.editor.ui.control.tree.action.impl.geometry;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to create a {@link Geometry} with a {@link Box} mesh.
 *
 * @author JavaSaBr
 */
public class CreateBoxAction extends AbstractCreateGeometryAction {

    public CreateBoxAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.CUBE_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_BOX;
    }

    @Override
    @FxThread
    protected @NotNull Geometry createGeometry() {
        return new Geometry("Box", new Box(1, 1, 1));
    }
}
