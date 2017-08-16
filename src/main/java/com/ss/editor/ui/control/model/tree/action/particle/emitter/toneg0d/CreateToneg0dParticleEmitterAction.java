package com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d;

import static com.ss.editor.util.EditorUtil.getDefaultLayer;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.scene.Node;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.AbstractNodeAction;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.impl.AlphaInfluencer;
import tonegod.emitter.influencers.impl.ColorInfluencer;
import tonegod.emitter.influencers.impl.SizeInfluencer;

/**
 * The action for creating new {@link ParticleEmitterNode}.
 *
 * @author JavaSaBr
 */
public class CreateToneg0dParticleEmitterAction extends AbstractNodeAction<ModelChangeConsumer> {

    /**
     * Instantiates a new Create toneg 0 d emitter action.
     *
     * @param nodeTree the node tree
     * @param node     the node
     */
    public CreateToneg0dParticleEmitterAction(@NotNull final NodeTree<?> nodeTree, @NotNull final TreeNode<?> node) {
        super(nodeTree, node);
    }

    @Nullable
    @Override
    protected Image getIcon() {
        return Icons.EMITTER_16;
    }

    @NotNull
    @Override
    protected String getName() {
        return Messages.MODEL_NODE_TREE_ACTION_CREATE_TONEG0D_PARTICLE_EMITTER;
    }

    @FXThread
    @Override
    protected void process() {
        super.process();

        final NodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        final SceneLayer defaultLayer = getDefaultLayer(changeConsumer);

        final ParticleEmitterNode emitter = createEmitterNode();
        emitter.addInfluencers(new ColorInfluencer(), new AlphaInfluencer(), new SizeInfluencer());
        emitter.setEnabled(true);

        final SizeInfluencer sizeInfluencer = emitter.getInfluencer(SizeInfluencer.class);

        if (sizeInfluencer != null) {
            sizeInfluencer.addSize(0.1f);
            sizeInfluencer.addSize(0f);
        }

        final TreeNode<?> treeNode = getNode();
        final Node parent = (Node) treeNode.getElement();

        if (defaultLayer != null) {
            SceneLayer.setLayer(defaultLayer, emitter);
        }

        changeConsumer.execute(new AddChildOperation(emitter, parent));
    }

    /**
     * Create emitter node particle emitter node.
     *
     * @return the particle emitter node
     */
    @NotNull
    protected ParticleEmitterNode createEmitterNode() {
        return new ParticleEmitterNode(EDITOR.getAssetManager());
    }
}
