package com.ss.editor.ui.control.model.tree.action.geometry;

import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The action to create a {@link Geometry} with a {@link Box} mesh.
 *
 * @author JavaSaBr
 */
public class CreateBoxAction extends AbstractCreateGeometryAction {

    /**
     * Instantiates a new Create box action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateBoxAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.CUBE_16;
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
