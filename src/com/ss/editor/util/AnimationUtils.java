package com.ss.editor.util;

import static com.ss.rlib.util.ClassUtils.unsafeCast;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.BoneTrack;
import com.jme3.animation.Track;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
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

        } catch (final NoSuchFieldException e) {
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
    @NotNull
    @FromAnyThread
    public static Animation extractAnimation(@NotNull final Animation source, @NotNull final String newName,
                                             final int startFrame, final int endFrame) {

        final Track[] sourceTracks = source.getTracks();
        final BoneTrack firstSourceTrack = (BoneTrack) sourceTracks[0];
        final float[] sourceTimes = firstSourceTrack.getTimes();

        final float newLength = (source.getLength() / (float) sourceTimes.length) * (float) (endFrame - startFrame);
        final Animation result = new Animation(newName, newLength);
        final Array<Track> newTracks = ArrayFactory.newArray(Track.class);

        for (final Track sourceTrack : sourceTracks) {
            if (sourceTrack instanceof BoneTrack) {
                newTracks.add(extractBoneTrack((BoneTrack) sourceTrack, startFrame, endFrame));
            }
        }

        result.setTracks(newTracks.toArray(Track.class));

        return result;
    }

    /**
     * Extract a bone track from a source animation to sub animation.
     *
     * @param boneTrack  the source bone track.
     * @param startFrame the start frame.
     * @param endFrame   the end frame.
     * @return the extracted bone track.
     */
    @NotNull
    private static BoneTrack extractBoneTrack(@NotNull final BoneTrack boneTrack, final int startFrame,
                                              final int endFrame) {

        final float[] sourceTimes = boneTrack.getTimes();

        final Vector3f[] newTranslations = new Vector3f[endFrame - startFrame];
        final Quaternion[] newRotations = new Quaternion[endFrame - startFrame];
        final Vector3f[] newScales = new Vector3f[endFrame - startFrame];
        final float[] newTimes = new float[endFrame - startFrame];

        for (int i = startFrame; i < endFrame; i++) {

            final int newFrame = i - startFrame;

            final Vector3f sourceTranslation = boneTrack.getTranslations()[i];
            final Vector3f sourceScale = boneTrack.getScales()[i];
            final Quaternion sourceRotation = boneTrack.getRotations()[i];

            newTimes[newFrame] = sourceTimes[i] - sourceTimes[startFrame];
            newTranslations[newFrame] = sourceTranslation.clone();
            newRotations[newFrame] = sourceRotation.clone();
            newScales[newFrame] = sourceScale.clone();
        }

        return new BoneTrack(boneTrack.getTargetBoneIndex(), newTimes, newTranslations, newRotations, newScales);
    }

    /**
     * Chane a name of an animation.
     *
     * @param control   the animation control.
     * @param animation the animation.
     * @param oldName   the old name.
     * @param newName   the new name.
     */
    @FromAnyThread
    public static void changeName(@NotNull final AnimControl control, @NotNull final Animation animation,
                                  @NotNull final String oldName, @NotNull final String newName) {
        try {

            final Map<String, Animation> animationMap = unsafeCast(ANIMATIONS_MAP_FIELD.get(control));

            if (!animationMap.containsKey(oldName)) {
                throw new IllegalArgumentException("Given animation does not exist " + "in this AnimControl");
            }

            if (animationMap.containsKey(newName)) {
                throw new IllegalArgumentException("The same animation exist " + "in this AnimControl");
            }

            ANIMATION_NAME_FIELD.set(animation, newName);

            animationMap.remove(oldName);
            animationMap.put(newName, animation);

        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get frame count of an animation/
     *
     * @param animation the animation.
     * @return the frame count or -1.
     */
    @FromAnyThread
    public static int getFrameCount(@NotNull final Animation animation) {

        int min = Integer.MAX_VALUE;

        final Track[] tracks = animation.getTracks();
        for (final Track track : tracks) {
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
    @NotNull
    @FromAnyThread
    public static String findFreeName(@NotNull final AnimControl control, @NotNull final String base) {
        if (control.getAnim(base) == null) return base;

        int i = 1;

        while (control.getAnim(base + "_" + i) != null) {
            i++;
        }

        return base + "_" + i;
    }
}
