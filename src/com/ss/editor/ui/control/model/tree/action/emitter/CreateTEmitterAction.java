package com.ss.editor.ui.control.model.tree.action.emitter;

import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.impl.AlphaInfluencer;
import tonegod.emitter.influencers.impl.ColorInfluencer;
import tonegod.emitter.influencers.impl.SizeInfluencer;

/**
 * The action for creating new {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class CreateTEmitterAction extends AbstractNodeAction {

    public CreateTEmitterAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.NODE_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_TEMITTER;
    }

    @Override
    protected void process() {

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();

        final ParticleEmitterNode emitter = new ParticleEmitterNode(EDITOR.getAssetManager());
        emitter.addInfluencers(new ColorInfluencer(), new AlphaInfluencer(), new SizeInfluencer());
        emitter.setEnabled(true);

        final SizeInfluencer sizeInfluencer = emitter.getInfluencer(SizeInfluencer.class);

        if (sizeInfluencer != null) {
            sizeInfluencer.addSize(0.1f);
            sizeInfluencer.addSize(0f);
        }

        final ModelNode<?> modelNode = getNode();
        final Node element = (Node) modelNode.getElement();

        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), element);

        modelChangeConsumer.execute(new AddChildOperation(emitter, index));
    }
}
