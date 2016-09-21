package com.ss.editor.ui.control.model.tree.action.emitter;

import com.jme3.scene.shape.Torus;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.ChangeEmitterShapeOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;

/**
 * The action for switching the emitter shape of the {@link ParticleEmitterNode} to {@link Torus}.
 *
 * @author JavaSaBr
 */
public class JMETorusShapeEmitterAction extends AbstractNodeAction {

    public JMETorusShapeEmitterAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return "Torus";
    }

    @Override
    protected void process() {

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();

        final ModelNode<?> modelNode = getNode();
        final ParticleEmitterNode element = (ParticleEmitterNode) modelNode.getElement();

        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), element);
        final Torus shape = new Torus(10, 10, 0.1F, 1F);

        modelChangeConsumer.execute(new ChangeEmitterShapeOperation(shape, index));
    }
}
