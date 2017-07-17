package com.ss.editor.ui.control.model.tree.action.geometry;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to create a {@link Geometry} with a {@link Sphere} mesh.
 *
 * @author JavaSaBr
 */
public class CreateSphereAction extends AbstractCreateGeometryAction {

    /**
     * Instantiates a new Create sphere action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateSphereAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
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
