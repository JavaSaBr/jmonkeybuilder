package com.ss.editor.ui.control.model.tree.action.geometry;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to create a {@link Geometry} with a {@link Quad} mesh.
 *
 * @author JavaSaBr
 */
public class CreateQuadAction extends AbstractCreateGeometryAction {

    /**
     * Instantiates a new Create quad action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateQuadAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.PLANE_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_QUAD;
    }

    @NotNull
    @Override
    protected Geometry createGeometry() {
        return new Geometry("Quad", new Quad(2, 2));
    }
}
