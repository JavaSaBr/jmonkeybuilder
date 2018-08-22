package com.ss.builder.ui.control.tree.action.impl.geometry;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.Icons;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to create a {@link Geometry} with a {@link Sphere} mesh.
 *
 * @author JavaSaBr
 */
public class CreateSphereAction extends AbstractCreateGeometryAction {

    public CreateSphereAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Override
    @FxThread
    protected @Nullable Image getIcon() {
        return Icons.SPHERE_16;
    }

    @Override
    @FxThread
    protected @NotNull String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_SPHERE;
    }

    @Override
    @FxThread
    protected @NotNull Geometry createGeometry() {
        return new Geometry("Sphere", new Sphere(30, 30, 1));
    }
}
