package com.ss.editor.ui.control.model.tree.action.emitter;

import com.jme3.scene.shape.Dome;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.ChangeEmitterShapeOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.ParticleEmitterNode;

/**
 * The action for switching the emitter shape of the {@link ParticleEmitterNode} to {@link Dome}.
 *
 * @author JavaSaBr
 */
public class JMEDomeShapeEmitterAction extends AbstractNodeAction {

    public JMEDomeShapeEmitterAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return "Dome";
    }

    @Override
    protected void process() {

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();

        final ModelNode<?> modelNode = getNode();
        final ParticleEmitterNode element = (ParticleEmitterNode) modelNode.getElement();

        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), element);
        final Dome shape = new Dome(10, 10, 1);

        modelChangeConsumer.execute(new ChangeEmitterShapeOperation(shape, index));
    }
}
