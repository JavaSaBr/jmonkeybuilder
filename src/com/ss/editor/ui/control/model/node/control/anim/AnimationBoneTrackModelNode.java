package com.ss.editor.ui.control.model.node.control.anim;

import static java.util.Objects.requireNonNull;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Bone;
import com.jme3.animation.BoneTrack;
import com.jme3.animation.Skeleton;
import com.ss.editor.ui.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link BoneTrack}.
 *
 * @author JavaSaBr
 */
public class AnimationBoneTrackModelNode extends AnimationTrackModelNode<BoneTrack> {

    /**
     * Instantiates a new Animation bone track model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public AnimationBoneTrackModelNode(@NotNull final BoneTrack element, final long objectId) {
        super(element, objectId);
    }

    @NotNull
    @Override
    protected String computeName() {
        final BoneTrack boneTrack = getElement();
        final AnimControl control = requireNonNull(getControl());
        final Skeleton skeleton = control.getSkeleton();
        final Bone bone = skeleton.getBone(boneTrack.getTargetBoneIndex());
        return "Bone track : " + bone.getName();
    }

    @Nullable
    @Override
    public Image getIcon() {
        return Icons.BONE_16;
    }
}
