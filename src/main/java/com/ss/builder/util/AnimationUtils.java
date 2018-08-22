package com.ss.builder.util;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.BoneTrack;
import com.jme3.animation.Track;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * The utility class to work with animations.
 *
 * @author JavaSaBr
 */
public class AnimationUtils {

    private static final Field ANIMATIONS_MAP_FIELD;
    private static final Field ANIMATION_NAME_FIELD;

    static {
        try {

            ANIMATION_NAME_FIELD = Animation.class.getDeclaredField("name");
            ANIMATION_NAME_FIELD.setAccessible(true);

            ANIMATIONS_MAP_FIELD = AnimControl.class.getDeclaredField("animationMap");
            ANIMATIONS_MAP_FIELD.setAccessible(true);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extract an animation from a source animation.
     *
     * @param source     the source animation.
     * @param newName    the new name of a sub animation.
     * @param startFrame the start frame.
     * @param endFrame   the end frame.
     * @return the new sub animation.
     */
    @FromAnyThread
    public static @NotNull Animation extractAnimation(
            @NotNull Animation source,
            @NotNull String newName,
            int startFrame,
            int endFrame
    ) {

        var sourceTracks = source.getTracks();
        var firstSourceTrack = (BoneTrack) sourceTracks[0];
        var sourceTimes = firstSourceTrack.getTimes();

        var newLength = (source.getLength() / (float) sourceTimes.length) * (float) (endFrame - startFrame);
        var result = new Animation(newName, newLength);
        var newTracks = ArrayFactory.<Track>newArray(Track.class);

        for (var sourceTrack : sourceTracks) {
            if (sourceTrack instanceof BoneTrack) {
                newTracks.add(extractBoneTrack((BoneTrack) sourceTrack, startFrame, endFrame));
            }
        }

        result.setTracks(newTracks.toArray(Track.class));

        return result;
    }

    /**
     * Extract a bone track from the source animation to sub animation.
     *
     * @param boneTrack  the source bone track.
     * @param startFrame the start frame.
     * @param endFrame   the end frame.
     * @return the extracted bone track.
     */
    @FromAnyThread
    private static @NotNull BoneTrack extractBoneTrack(@NotNull BoneTrack boneTrack, int startFrame, int endFrame) {

        var sourceTimes = boneTrack.getTimes();

        var newTranslations = new Vector3f[endFrame - startFrame];
        var newRotations = new Quaternion[endFrame - startFrame];
        var newScales = new Vector3f[endFrame - startFrame];
        var newTimes = new float[endFrame - startFrame];

        for (int i = startFrame; i < endFrame; i++) {

            var newFrame = i - startFrame;
            var sourceTranslation = boneTrack.getTranslations()[i];
            var sourceScale = boneTrack.getScales()[i];
            var sourceRotation = boneTrack.getRotations()[i];

            newTimes[newFrame] = sourceTimes[i] - sourceTimes[startFrame];
            newTranslations[newFrame] = sourceTranslation.clone();
            newRotations[newFrame] = sourceRotation.clone();
            newScales[newFrame] = sourceScale.clone();
        }

        return new BoneTrack(boneTrack.getTargetBoneIndex(), newTimes,
                newTranslations, newRotations, newScales);
    }

    /**
     * Change the name of the animation.
     *
     * @param control   the animation control.
     * @param animation the animation.
     * @param oldName   the old name.
     * @param newName   the new name.
     */
    @FromAnyThread
    public static void changeName(
            @NotNull AnimControl control,
            @NotNull Animation animation,
            @NotNull String oldName,
            @NotNull String newName
    ) {
        try {

            var animationMap = ClassUtils.<Map<String, Animation>>unsafeCast(ANIMATIONS_MAP_FIELD.get(control));

            if (!animationMap.containsKey(oldName)) {
                throw new IllegalArgumentException("Given animation does not exist " + "in this AnimControl");
            }

            if (animationMap.containsKey(newName)) {
                throw new IllegalArgumentException("The same animation exist " + "in this AnimControl");
            }

            ANIMATION_NAME_FIELD.set(animation, newName);

            animationMap.remove(oldName);
            animationMap.put(newName, animation);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get frame count of the animation.
     *
     * @param animation the animation.
     * @return the frame count or -1.
     */
    @FromAnyThread
    public static int getFrameCount(@NotNull Animation animation) {

        var min = Integer.MAX_VALUE;

        var tracks = animation.getTracks();
        for (var track : tracks) {
            if (track instanceof BoneTrack) {
                min = Math.min(min, ((BoneTrack) track).getTimes().length);
            }
        }

        return min == Integer.MAX_VALUE ? -1 : min;
    }

    /**
     * Get a free name to make an animation.
     *
     * @param control the animation control.
     * @param base    the base name.
     * @return the free name.
     */
    @FromAnyThread
    public static @NotNull String findFreeName(@NotNull AnimControl control, @NotNull String base) {

        if (control.getAnim(base) == null) {
            return base;
        }

        int i = 1;

        while (control.getAnim(base + "_" + i) != null) {
            i++;
        }

        return base + "_" + i;
    }
}
