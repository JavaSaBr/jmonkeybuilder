package com.ss.editor.ui.control.model.tree.action.geometry;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

/**
 * The action for creating the {@link Geometry} with the {@link Quad} mesh.
 *
 * @author JavaSaBr
 */
public class CreateQuadAction extends AbstractCreateGeometryAction {

    public CreateQuadAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
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