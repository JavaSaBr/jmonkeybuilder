package com.ss.editor.ui.control.model.tree.action.emitter.shape;

import com.jme3.scene.Mesh;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;

import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.shapes.TriangleEmitterShape;

/**
 * The action to switch an emitter shape of the {@link ParticleEmitterNode} to a {@link TriangleEmitterShape}.
 *
 * @author JavaSaBr
 */
public class CreateTriangleShapeEmitterAction extends AbstractCreateShapeEmitterAction {

    /**
     * Instantiates a new Create triangle shape emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateTriangleShapeEmitterAction(@NotNull final AbstractNodeTree<?> nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.TRIANGLE_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_EMITTER_CHANGE_TRIANGLE_SHAPE;
    }

    @NotNull
    @Override
    protected Mesh createMesh() {
        return new TriangleEmitterShape(1);
    }
}
