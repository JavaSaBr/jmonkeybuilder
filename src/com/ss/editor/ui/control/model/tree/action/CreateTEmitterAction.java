package com.ss.editor.ui.control.model.tree.action;

import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.operation.AddControlOperation;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

import tonegod.emitter.Emitter;
import tonegod.emitter.EmitterMesh;
import tonegod.emitter.influencers.AlphaInfluencer;
import tonegod.emitter.influencers.ColorInfluencer;
import tonegod.emitter.influencers.SizeInfluencer;
import tonegod.emitter.particle.ParticleDataTriMesh;

/**
 * The action for creating new {@link Emitter}.
 *
 * @author JavaSaBr
 */
public class CreateTEmitterAction extends AbstractNodeAction {

    public CreateTEmitterAction(@NotNull final ModelNodeTree nodeTree, @NotNull final ModelNode<?> node) {
        super(nodeTree, node);
    }

    @NotNull
    @Override
    protected String getName() {
        return "Create TEmitter";
    }

    @Override
    protected void process() {

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();

        final Emitter emitter = new Emitter();
        emitter.setEnabled(true);
        emitter.setName("New Emitter");
        emitter.setMaxParticles(100);
        emitter.addInfluencers(new ColorInfluencer(), new AlphaInfluencer(), new SizeInfluencer());

        // Shape & Emissions
        emitter.setShapeSimpleEmitter();
        emitter.setDirectionType(EmitterMesh.DirectionType.Random);
        emitter.setEmissionsPerSecond(100);
        emitter.setParticlesPerEmission(1);

        // Particle props
        emitter.setParticleType(ParticleDataTriMesh.class, (Mesh) null);
        emitter.setBillboardMode(Emitter.BillboardMode.Camera);
        emitter.setForce(1);
        emitter.setLife(0.999f);
        emitter.setSprite("graphics/textures/sprite/default.png");
        emitter.getInfluencer(SizeInfluencer.class).addSize(0.1f);
        emitter.getInfluencer(SizeInfluencer.class).addSize(0f);
        emitter.initialize(EDITOR.getAssetManager());

        final ModelNode<?> modelNode = getNode();
        final Node element = (Node) modelNode.getElement();

        final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), element);

        modelChangeConsumer.execute(new AddControlOperation(emitter, index));
    }
}
