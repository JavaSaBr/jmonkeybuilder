package com.ss.editor.ui.control.model.tree.node.spatial.emitter;

import com.ss.editor.Messages;
import com.ss.editor.model.node.ParticleInfluencers;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.emitter.influerencer.CreateAlphaParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.action.emitter.influerencer.CreateColorParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.action.emitter.influerencer.CreateDestinationParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.action.emitter.influerencer.CreateGravityParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.action.emitter.influerencer.CreateImpulseParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.action.emitter.influerencer.CreatePhysicsParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.action.emitter.influerencer.CreateRadialVelocityParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.action.emitter.influerencer.CreateRotationParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.action.emitter.influerencer.CreateSizeParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.action.emitter.influerencer.CreateSpriteParticleInfluencerAction;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import rlib.util.ArrayUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import static com.ss.editor.ui.control.model.tree.node.ModelNodeFactory.createFor;

/**
 * The implementation of the {@link ModelNode} for representing the {@link ParticleInfluencers} in
 * the editor.
 *
 * @author JavaSaBr
 */
public class ParticleInfluencersModelNode extends ModelNode<ParticleInfluencers> {

    public ParticleInfluencersModelNode(@NotNull final ParticleInfluencers element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        return Messages.MODEL_FILE_EDITOR_NODE_EMITTER_INFLUENCERS;
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.NODE_16;
    }

    @Override
    public void fillContextMenu(@NotNull final ModelNodeTree nodeTree, @NotNull final ObservableList<MenuItem> items) {

        final Menu createMenu = new Menu(Messages.MODEL_NODE_TREE_ACTION_CREATE);
        final ObservableList<MenuItem> createItems = createMenu.getItems();
        createItems.add(new CreateAlphaParticleInfluencerAction(nodeTree, this));
        createItems.add(new CreateColorParticleInfluencerAction(nodeTree, this));
        createItems.add(new CreateDestinationParticleInfluencerAction(nodeTree, this));
        createItems.add(new CreateGravityParticleInfluencerAction(nodeTree, this));
        createItems.add(new CreateImpulseParticleInfluencerAction(nodeTree, this));
        createItems.add(new CreatePhysicsParticleInfluencerAction(nodeTree, this));
        createItems.add(new CreateRadialVelocityParticleInfluencerAction(nodeTree, this));
        createItems.add(new CreateRotationParticleInfluencerAction(nodeTree, this));
        createItems.add(new CreateSizeParticleInfluencerAction(nodeTree, this));
        createItems.add(new CreateSpriteParticleInfluencerAction(nodeTree, this));

        items.add(createMenu);

        super.fillContextMenu(nodeTree, items);
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren() {
        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class);
        final ParticleInfluencers element = getElement();
        ArrayUtils.forEach(element.getInfluencers(), influencer -> result.add(createFor(influencer, element)));
        return result;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }
}
