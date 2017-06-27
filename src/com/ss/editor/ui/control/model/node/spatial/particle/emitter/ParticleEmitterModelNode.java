package com.ss.editor.ui.control.model.node.spatial.particle.emitter;

import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.jme3.effect.shapes.EmitterShape;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.spatial.GeometryModelNode;
import com.ss.editor.ui.control.model.node.spatial.NodeModelNode;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.ResetParticleEmittersAction;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.influencer.CreateDefaultParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.influencer.CreateEmptyParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.influencer.CreateRadialParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.shape.*;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
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
 * The implementation of the {@link NodeModelNode} to represent the {@link ParticleEmitter} in the editor.
 *
 * @author JavaSaBr
 */
public class ParticleEmitterModelNode extends GeometryModelNode<ParticleEmitter> {

    /**
     * Instantiates a new particle emitter model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public ParticleEmitterModelNode(@NotNull final ParticleEmitter element, final long objectId) {
        super(element, objectId);
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.PARTICLES_16;
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren(@NotNull final AbstractNodeTree<?> nodeTree) {

        final ParticleEmitter element = getElement();
        final ModelNode<ParticleInfluencer> influencerModelNode = createFor(element.getParticleInfluencer());
        final ModelNode<EmitterShape> shapeModelNode = createFor(element.getShape());

        final Array<ModelNode<?>> children = ArrayFactory.newArray(ModelNode.class);
        if (influencerModelNode != null) children.add(influencerModelNode);
        if (shapeModelNode != null) children.add(shapeModelNode);
        children.addAll(super.getChildren(nodeTree));

        return children;
    }

    @Override
    public void fillContextMenu(@NotNull final AbstractNodeTree<?> nodeTree,
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
    protected Menu createToolMenu(@NotNull final AbstractNodeTree<?> nodeTree) {
        return null;
    }

    @Nullable
    @Override
    protected Menu createCreationMenu(@NotNull final AbstractNodeTree<?> nodeTree) {
        return null;
    }
}
