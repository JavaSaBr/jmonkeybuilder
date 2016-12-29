package com.ss.editor.ui.control.model.tree.node.control.anim;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Bone;
import com.jme3.animation.BoneTrack;
import com.jme3.animation.Skeleton;
import com.ss.editor.ui.Icons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.image.Image;

/**
 * The implementation of node for showing {@link BoneTrack}.
 *
 * @author JavaSaBr
 */
public class AnimationBoneTrackModelNode extends AnimationTrackModelNode<BoneTrack> {

    public AnimationBoneTrackModelNode(final BoneTrack element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    public String getName() {
        final BoneTrack boneTrack = getElement();
        final AnimControl control = getControl();
        final Skeleton skeleton = control.getSkeleton();
        final Bone bone = skeleton.getBone(boneTrack.getTargetBoneIndex());
        return "BoneTrack : " + bone.getName();
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.BONE_16;
    }
}
