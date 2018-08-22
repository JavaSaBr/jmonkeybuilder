package com.ss.builder.ui.control.tree.node.factory.impl;

import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import com.jme3.animation.*;
import com.ss.builder.annotation.FxThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.control.tree.node.impl.control.legacyanim.*;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.control.tree.node.factory.TreeNodeFactory;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of a tree node factory to make animation nodes.
 *
 * @author JavaSaBr
 */
@Deprecated
public class LegacyAnimationTreeNodeFactory implements TreeNodeFactory {

    public static final int PRIORITY = 1;

    @Override
    @FxThread
    public <T, V extends TreeNode<T>> @Nullable V createFor(@Nullable T element, long objectId) {

        if (element instanceof Animation) {
            return unsafeCast(new AnimationTreeNode((Animation) element, objectId));
        } else if (element instanceof BoneTrack) {
            return unsafeCast(new AnimationBoneTrackTreeNode((BoneTrack) element, objectId));
        } else if (element instanceof EffectTrack) {
            return unsafeCast(new AnimationEffectTrackTreeNode((EffectTrack) element, objectId));
        } else if (element instanceof AudioTrack) {
            return unsafeCast(new AnimationAudioTrackTreeNode((AudioTrack) element, objectId));
        } else if (element instanceof SpatialTrack) {
            return unsafeCast(new AnimationSpatialTrackTreeNode((SpatialTrack) element, objectId));
        } else if (element instanceof AnimControl) {
            return unsafeCast(new AnimationControlTreeNode((AnimControl) element, objectId));
        }

        return null;
    }

    @Override
    @FxThread
    public int getPriority() {
        return PRIORITY;
    }
}
