package com.ss.editor.ui.control.model.tree.node.control.anim;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.Track;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.PlayAnimationAction;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.control.model.tree.node.ModelNodeFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import rlib.util.ArrayUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of node for showing {@link Animation}.
 *
 * @author JavaSaBr
 */
public class AnimationModelNode extends ModelNode<Animation> {

    /**
     * The animation control.
     */
    private AnimControl control;

    /**
     * The index of playing animation.
     */
    private int channel;

    public AnimationModelNode(final Animation element, final long objectId) {
        super(element, objectId);
        this.channel = -1;
    }

    @Override
    public void fillContextMenu(@NotNull final ModelNodeTree nodeTree, @NotNull final ObservableList<MenuItem> items) {
        if (getChannel() < 0) items.add(new PlayAnimationAction(nodeTree, this));
        super.fillContextMenu(nodeTree, items);
    }

    @NotNull
    @Override
    public Array<ModelNode<?>> getChildren() {

        final Animation element = getElement();
        final Track[] tracks = element.getTracks();

        final Array<ModelNode<?>> result = ArrayFactory.newArray(ModelNode.class, tracks.length);

        ArrayUtils.forEach(tracks, track -> {
            final AnimationTrackModelNode<Track> modelNode = ModelNodeFactory.createFor(track);
            modelNode.setControl(getControl());
            result.add(modelNode);
        });

        return result;
    }

    @Override
    public boolean hasChildren() {
        final Animation element = getElement();
        final Track[] tracks = element.getTracks();
        return tracks != null && tracks.length > 0;
    }

    /**
     * @param control the animation control.
     */
    public void setControl(@NotNull final AnimControl control) {
        this.control = control;
    }

    /**
     * @return the animation control.
     */
    public AnimControl getControl() {
        return control;
    }

    @NotNull
    @Override
    public String getName() {
        return getElement().getName();
    }

    /**
     * @return the index of playing animation.
     */
    public int getChannel() {
        return channel;
    }

    /**
     * @param channel the index of playing animation.
     */
    public void setChannel(final int channel) {
        this.channel = channel;
    }

    @Nullable
    @Override
    public Image getIcon() {
        return getChannel() < 0 ? Icons.PLAY_16 : Icons.STOP_16;
    }
}
