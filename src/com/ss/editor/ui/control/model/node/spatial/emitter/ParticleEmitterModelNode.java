package com.ss.editor.ui.control.model.node.spatial.emitter;

import static com.ss.editor.ui.control.tree.node.ModelNodeFactory.createFor;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.influencers.ParticleInfluencer;
import com.jme3.effect.shapes.EmitterShape;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.node.spatial.GeometryModelNode;
import com.ss.editor.ui.control.model.node.spatial.NodeModelNode;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
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

        return children;
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
