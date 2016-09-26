package com.ss.editor.ui.control.model.tree.action.geometry;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.ss.editor.Messages;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;

/**
 * The action for creating the {@link Geometry} with the {@link Box} mesh.
 *
 * @author JavaSaBr
 */
public class CreateBoxAction extends AbstractCreateGeometryAction {

    public CreateBoxAction(final ModelNodeTree nodeTree, final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_PRIMITIVE_BOX;
    }

    @NotNull
    @Override
    protected Geometry createGeometry() {
        return new Geometry("Box", new Box(1, 1, 1));
    }
}
