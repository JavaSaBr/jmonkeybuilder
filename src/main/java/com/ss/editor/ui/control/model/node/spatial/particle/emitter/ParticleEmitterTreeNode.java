package com.ss.editor.ui.control.model.node.spatial.particle.emitter;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.jme3.effect.shapes.EmitterShape;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.spatial.GeometryTreeNode;
import com.ss.editor.ui.control.model.node.spatial.NodeTreeNode;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.ResetParticleEmittersAction;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.influencer.CreateDefaultParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.influencer.CreateEmptyParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.influencer.CreateRadialParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.shape.*;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link NodeTreeNode} to represent the {@link ParticleEmitter} in the editor.
 *
 * @author JavaSaBr
 */
public class ParticleEmitterTreeNode extends GeometryTreeNode<ParticleEmitter> {

    /**
     * Instantiates a new particle emitter model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public ParticleEmitterTreeNode(@NotNull final ParticleEmitter element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.PARTICLES_16;
    }

    @NotNull
    @Override
    public Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {

        final ParticleEmitter element = getElement();
        final TreeNode<ParticleInfluencer> influencerTreeNode = FACTORY_REGISTRY.createFor(element.getParticleInfluencer());
        final TreeNode<EmitterShape> shapeTreeNode = FACTORY_REGISTRY.createFor(element.getShape());

        final Array<TreeNode<?>> children = ArrayFactory.newArray(TreeNode.class);
        if (influencerTreeNode != null) children.add(influencerTreeNode);
        if (shapeTreeNode != null) children.add(shapeTreeNode);
        children.addAll(super.getChildren(nodeTree));

        return children;
    }

    @Override
    public void fillContextMenu(@NotNull final NodeTree<?> nodeTree,
                                @NotNull final ObservableList<MenuItem> items) {

        final Menu changeShapeMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CHANGE_SHAPE,
                new ImageView(Icons.EDIT_16));

        changeShapeMenu.getItems().addAll(new CreateBoxShapeEmitterAction(nodeTree, this),
                new CreateSphereShapeEmitterAction(nodeTree, this),
                new CreatePointShapeEmitterAction(nodeTree, this),
                new CreateMeshVertexShapeEmitterAction(nodeTree, this),
                new CreateMeshFaceShapeEmitterAction(nodeTree, this),
                new CreateMeshConvexHullShapeEmitterAction(nodeTree, this));

        final Menu changeInfluencerMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CHANGE_INFLUENCER,
                new ImageView(Icons.EDIT_16));

        changeInfluencerMenu.getItems().addAll(new CreateEmptyParticleInfluencerAction(nodeTree, this),
                new CreateDefaultParticleInfluencerAction(nodeTree, this),
                new CreateRadialParticleInfluencerAction(nodeTree, this));

        items.add(new ResetParticleEmittersAction(nodeTree, this));
        items.add(changeShapeMenu);
        items.add(changeInfluencerMenu);

        super.fillContextMenu(nodeTree, items);
    }

    @Nullable
    @Override
    protected Menu createToolMenu(@NotNull final NodeTree<?> nodeTree) {
        return null;
    }

    @Nullable
    @Override
    protected Menu createCreationMenu(@NotNull final NodeTree<?> nodeTree) {
        return null;
    }
}
