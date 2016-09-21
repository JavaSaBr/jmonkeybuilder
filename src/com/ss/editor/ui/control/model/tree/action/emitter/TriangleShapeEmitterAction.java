package com.ss.editor.ui.control.model.tree.action.emitter;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.ChangeEmitterShapeOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.shapes.TriangleEmitterShape;

/**
 * The action for switching the emitter shape of the {@link ParticleEmitterNode} to {@link
 * TriangleEmitterShape}.
 *
 * @author JavaSaBr
 */
public class TriangleShapeEmitterAction extends AbstractNodeAction {

    public TriangleShapeEmitterAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return "Triangle shape";
    }

    @Override
    protected void process() {

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();

        final ModelNode<?> modelNode = getNode();
        final ParticleEmitterNode element = (ParticleEmitterNode) modelNode.getElement();

        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), element);
        final TriangleEmitterShape shape = new TriangleEmitterShape();
        shape.init(1);

        modelChangeConsumer.execute(new ChangeEmitterShapeOperation(shape, index));
    }
}
