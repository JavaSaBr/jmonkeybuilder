package com.ss.editor.ui.control.model.tree.action.geometry;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action for creating the {@link Geometry} with the {@link Sphere} mesh.
 *
 * @author JavaSaBr
 */
public class CreateSphereAction extends AbstractCreateGeometryAction {

    public CreateSphereAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.SPHERE_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_SPHERE;
    }

    @NotNull
    @Override
    protected Geometry createGeometry() {
        return new Geometry("Sphere", new Sphere(30, 30, 1));
    }
}
