package com.ss.editor.ui.control.model.node.spatial.particle.emitter.toneg0d.influencer;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.action.particle.emitter.toneg0d.influerencer.RemoveParticleInfluencerAction;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.influencers.ParticleInfluencer;

/**
 * The implementation of the {@link TreeNode} for representing the {@link ParticleInfluencer} in the editor.
 *
 * @author JavaSaBr
 */
public class Toneg0DParticleInfluencerTreeNode extends TreeNode<ParticleInfluencer> {

    public Toneg0DParticleInfluencerTreeNode(@NotNull final ParticleInfluencer element, final long objectId) {
        super(element, objectId);
    }

    @Override
    @FXThread
    public @Nullable Image getIcon() {
        return Icons.INFLUENCER_16;
    }

    @Override
    @FXThread
    public void fillContextMenu(@NotNull final NodeTree<?> nodeTree, @NotNull final ObservableList<MenuItem> items) {
        items.add(new RemoveParticleInfluencerAction(nodeTree, this));
        super.fillContextMenu(nodeTree, items);
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        final ParticleInfluencer element = getElement();
        return element.getName();
    }
}
