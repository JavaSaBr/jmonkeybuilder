package com.ss.editor.ui.control.tree.node.impl.control.anim;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.Track;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.model.ModelNodeTree;
import com.ss.editor.ui.control.tree.action.impl.RenameNodeAction;
import com.ss.editor.ui.control.tree.action.impl.animation.*;
import com.ss.editor.ui.control.tree.action.impl.operation.animation.RenameAnimationNodeOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.util.AnimationUtils;
import com.ss.rlib.util.ArrayUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link Animation}.
 *
 * @author JavaSaBr
 */
public class AnimationTreeNode extends TreeNode<Animation> {

    /**
     * The node of an animation control.
     */
    @Nullable
    private AnimationControlTreeNode controlModelNode;

    /**
     * The animation control.
     */
    @Nullable
    private AnimControl control;

    /**
     * The speed.
     */
    private float speed;

    /**
     * The index of playing animation.
     */
    private int channel;

    public AnimationTreeNode(@NotNull final Animation element, final long objectId) {
        super(element, objectId);
        this.channel = -1;
    }

    @Override
    @FxThread
    public void fillContextMenu(@NotNull final NodeTree<?> nodeTree,
                                @NotNull final ObservableList<MenuItem> items) {

        final Animation animation = getElement();
        final AnimationControlTreeNode controlModelNode = notNull(getControlModelNode());
        final AnimControl control = controlModelNode.getElement();

        final int frameCount = AnimationUtils.getFrameCount(animation);

        if (getChannel() < 0 && control.getNumChannels() < 1) {
            items.add(new PlayAnimationAction(nodeTree, this));
            items.add(new RemoveAnimationAction(nodeTree, this));
            items.add(new RenameNodeAction(nodeTree, this));
        } else if (getChannel() >= 0 && control.getChannel(getChannel()).getSpeed() < 0.0001F) {
            items.add(new PlayAnimationAction(nodeTree, this));
            items.add(new StopAnimationAction(nodeTree, this));
        } else if (getChannel() >= 0) {
            items.add(new PauseAnimationAction(nodeTree, this));
            items.add(new StopAnimationAction(nodeTree, this));
        }

        if (getChannel() < 0 && frameCount > 0) {
            items.add(new ManualExtractSubAnimationAction(nodeTree, this));
        }

        super.fillContextMenu(nodeTree, items);
    }

    @Override
    @FxThread
    public boolean hasChildren(@NotNull final NodeTree<?> nodeTree) {

        final Animation element = getElement();
        final Track[] tracks = element.getTracks();

        return tracks != null && tracks.length > 0 && nodeTree instanceof ModelNodeTree;
    }

    @Override
    @FxThread
    public @NotNull Array<TreeNode<?>> getChildren(@NotNull final NodeTree<?> nodeTree) {

        final Animation element = getElement();
        final Track[] tracks = element.getTracks();

        final Array<TreeNode<?>> result = ArrayFactory.newArray(TreeNode.class, tracks.length);
        ArrayUtils.forEach(tracks, track -> result.add(FACTORY_REGISTRY.createFor(track)));

        return result;
    }

    @Override
    @FxThread
    public boolean canEditName() {
        return true;
    }

    @Override
    @FxThread
    public void changeName(@NotNull final NodeTree<?> nodeTree, @NotNull final String newName) {
        if (StringUtils.equals(getName(), newName)) return;

        super.changeName(nodeTree, newName);

        final AnimationControlTreeNode controlModelNode = notNull(getControlModelNode());
        final AnimControl control = controlModelNode.getElement();
        final RenameAnimationNodeOperation operation = new RenameAnimationNodeOperation(getName(), newName, control);

        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(operation);
    }

    /**
     * Gets control model node.
     *
     * @return the node of an animation control.
     */
    @FxThread
    public @Nullable AnimationControlTreeNode getControlModelNode() {
        return controlModelNode;
    }

    /**
     * Sets control model node.
     *
     * @param controlModelNode the node of an animation control.
     */
    @FxThread
    public void setControlModelNode(@Nullable final AnimationControlTreeNode controlModelNode) {
        this.controlModelNode = controlModelNode;
    }

    /**
     * Gets control.
     *
     * @return the animation control.
     */
    @FxThread
    public @Nullable AnimControl getControl() {
        return control;
    }

    /**
     * Sets control.
     *
     * @param control the animation control.
     */
    @FxThread
    public void setControl(@Nullable final AnimControl control) {
        this.control = control;
    }

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return getElement().getName();
    }

    /**
     * Gets channel.
     *
     * @return the index of playing animation.
     */
    @FxThread
    public int getChannel() {
        return channel;
    }

    /**
     * Sets channel.
     *
     * @param channel the index of playing animation.
     */
    @FxThread
    public void setChannel(final int channel) {
        this.channel = channel;
    }

    /**
     * Gets speed.
     *
     * @return the speed
     */
    @FxThread
    public float getSpeed() {
        return speed;
    }

    /**
     * Sets speed.
     *
     * @param speed the speed
     */
    @FxThread
    public void setSpeed(final float speed) {
        this.speed = speed;
    }

    @Override
    @FxThread
    public @Nullable Image getIcon() {
        if (getChannel() < 0) return Icons.PLAY_16;
        return getSpeed() < 0.0001F ? Icons.PAUSE_16 : Icons.STOP_16;
    }

    @Override
    @FxThread
    public void notifyChildPreAdd(@NotNull final TreeNode<?> treeNode) {
        final AnimationTrackTreeNode<?> animationTrackModelNode = (AnimationTrackTreeNode<?>) treeNode;
        animationTrackModelNode.setControl(getControl());
        super.notifyChildPreAdd(treeNode);
    }
}
