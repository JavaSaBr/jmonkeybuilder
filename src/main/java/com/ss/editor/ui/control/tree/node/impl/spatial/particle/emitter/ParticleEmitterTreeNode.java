package com.ss.editor.ui.control.tree.node.impl.spatial.particle.emitter;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.jme3.effect.shapes.EmitterShape;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.tree.node.impl.spatial.GeometryTreeNode;
import com.ss.editor.ui.control.tree.node.impl.spatial.NodeTreeNode;
import com.ss.editor.ui.control.tree.action.impl.particle.emitter.ResetParticleEmittersAction;
import com.ss.editor.ui.control.tree.action.impl.particle.emitter.influencer.CreateDefaultParticleInfluencerAction;
import com.ss.editor.ui.control.tree.action.impl.particle.emitter.influencer.CreateEmptyParticleInfluencerAction;
import com.ss.editor.ui.control.tree.action.impl.particle.emitter.influencer.CreateRadialParticleInfluencerAction;
import com.ss.editor.ui.control.tree.action.impl.particle.emitter.shape.*;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The implementation of the {@link NodeTreeNode} to represent the {@link ParticleEmitter} in the editor.
 *
 * @author JavaSaBr
 */
public class ParticleEmitterTreeNode extends GeometryTreeNode<ParticleEmitter> {

    public ParticleEmitterTreeNode(@NotNull ParticleEmitter element, long objectId) {
        super(element, objectId);
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        return Icons.PARTICLES_16;
    }

    @Override
    @FxThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull NodeTree<?> nodeTree) {

        var element = getElement();
        var influencerTreeNode = FACTORY_REGISTRY.createFor(element.getParticleInfluencer());
        var shapeTreeNode = FACTORY_REGISTRY.createFor(element.getShape());

        var children = ArrayFactory.<TreeNode<?>>newArray(TreeNode.class);

        if (influencerTreeNode != null) {
            children.add(influencerTreeNode);
        }

        if (shapeTreeNode != null) {
            children.add(shapeTreeNode);
        }

        children.addAll(super.getChildren(nodeTree));

        return children;
    }

    @Override
    @FxThread
    public void fillContextMenu(@NotNull NodeTree<?> nodeTree, @NotNull ObservableList<MenuItem> items) {

        final Menu changeShapeMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CHANGE_SHAPE,
                new ImageView(Icons.EDIT_16));

        changeShapeMenu.getItems()
                .addAll(new CreateBoxShapeEmitterAction(nodeTree, this),
                        new CreateSphereShapeEmitterAction(nodeTree, this),
                        new CreatePointShapeEmitterAction(nodeTree, this),
                        new CreateMeshVertexShapeEmitterAction(nodeTree, this),
                        new CreateMeshFaceShapeEmitterAction(nodeTree, this),
                        new CreateMeshConvexHullShapeEmitterAction(nodeTree, this));

        final Menu changeInfluencerMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_PARTICLE_EMITTER_CHANGE_INFLUENCER,
                new ImageView(Icons.EDIT_16));

        changeInfluencerMenu.getItems()
                .addAll(new CreateEmptyParticleInfluencerAction(nodeTree, this),
                        new CreateDefaultParticleInfluencerAction(nodeTree, this),
                        new CreateRadialParticleInfluencerAction(nodeTree, this));

        items.add(new ResetParticleEmittersAction(nodeTree, this));
        items.add(changeShapeMenu);
        items.add(changeInfluencerMenu);

        super.fillContextMenu(nodeTree, items);
    }

    @Override
    @FxThread
    protected @NotNull Optional<Menu> createToolMenu(@NotNull NodeTree<?> nodeTree) {
        return Optional.empty();
    }

    @Override
    @FxThread
    protected @NotNull Optional<Menu> createCreationMenu(@NotNull NodeTree<?> nodeTree) {
        return Optional.empty();
    }
}
