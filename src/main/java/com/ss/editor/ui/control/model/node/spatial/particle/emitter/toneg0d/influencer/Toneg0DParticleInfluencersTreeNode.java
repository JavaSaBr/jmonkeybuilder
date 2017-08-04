package com.ss.editor.ui.control.model.node.spatial.particle.emitter.toneg0d.influencer;

import static com.ss.rlib.util.ClassUtils.getConstructor;
import static com.ss.rlib.util.ClassUtils.newInstance;
import com.ss.editor.Messages;
import com.ss.editor.model.node.Toneg0dParticleInfluencers;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.influerencer.*;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.ParticleEmitterNode;
import tonegod.emitter.influencers.ParticleInfluencer;
import tonegod.emitter.influencers.impl.*;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * The implementation of the {@link TreeNode} for representing the {@link Toneg0dParticleInfluencers} in the editor.
 *
 * @author JavaSaBr
 */
public class Toneg0DParticleInfluencersTreeNode extends TreeNode<Toneg0dParticleInfluencers> {

    @NotNull
    private static final ObjectDictionary<Class<? extends ParticleInfluencer>, Constructor<? extends MenuItem>> CONSTRUCTORS =
            DictionaryFactory.newObjectDictionary();

    static {
        CONSTRUCTORS.put(AlphaInfluencer.class, getConstructor(CreateAlphaParticleInfluencerAction.class, NodeTree.class, TreeNode.class));
        CONSTRUCTORS.put(ColorInfluencer.class, getConstructor(CreateColorParticleInfluencerAction.class, NodeTree.class, TreeNode.class));
        CONSTRUCTORS.put(DestinationInfluencer.class, getConstructor(CreateDestinationParticleInfluencerAction.class, NodeTree.class, TreeNode.class));
        CONSTRUCTORS.put(GravityInfluencer.class, getConstructor(CreateGravityParticleInfluencerAction.class, NodeTree.class, TreeNode.class));
        CONSTRUCTORS.put(ImpulseInfluencer.class, getConstructor(CreateImpulseParticleInfluencerAction.class, NodeTree.class, TreeNode.class));
        CONSTRUCTORS.put(PhysicsInfluencer.class, getConstructor(CreatePhysicsParticleInfluencerAction.class, NodeTree.class, TreeNode.class));
        CONSTRUCTORS.put(RadialVelocityInfluencer.class, getConstructor(CreateRadialVelocityParticleInfluencerAction.class, NodeTree.class, TreeNode.class));
        CONSTRUCTORS.put(RotationInfluencer.class, getConstructor(CreateRotationParticleInfluencerAction.class, NodeTree.class, TreeNode.class));
        CONSTRUCTORS.put(SizeInfluencer.class, getConstructor(CreateSizeParticleInfluencerAction.class, NodeTree.class, TreeNode.class));
        CONSTRUCTORS.put(SpriteInfluencer.class, getConstructor(CreateSpriteParticleInfluencerAction.class, NodeTree.class, TreeNode.class));
    }

    /**
     * Instantiates a new Particle influencers model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public Toneg0DParticleInfluencersTreeNode(@NotNull final Toneg0dParticleInfluencers element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_PARTICLE_EMITTER_INFLUENCERS;
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.NODE_16;
    }

    @Override
    public void fillContextMenu(@NotNull final NodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {

        final Toneg0dParticleInfluencers element = getElement();
        final ParticleEmitterNode emitterNode = element.getEmitterNode();

        final Menu createMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_CREATE, new ImageView(Icons.ADD_12));
        final ObservableList<MenuItem> createItems = createMenu.getItems();

        CONSTRUCTORS.forEach((type, constructor) -> {
            if (emitterNode.getInfluencer(type) != null) return;
            createItems.add(newInstance(constructor, nodeTree, this));
        });

        items.add(createMenu);

        super.fillContextMenu(nodeTree, items);
    }

    @NotNull
    @Override
    public Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {
        final Array<TreeNode<?>> result = ArrayFactory.newArray(TreeNode.class);
        final Toneg0dParticleInfluencers element = getElement();
        final List<ParticleInfluencer> influencers = element.getInfluencers();
        influencers.forEach(influencer -> result.add(FACTORY_REGISTRY.createFor(influencer)));
        return result;
    }

    @Override
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {
        return nodeTree instanceof ModelNodeTree;
    }
}
