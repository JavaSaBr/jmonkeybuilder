package com.ss.builder.util;

import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.rlib.common.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * The utility class to work with audio nodes.
 *
 * @author JavaSaBr
 */
public class AudioNodeUtils {

    private static final Field AUDIO_DATA_FIELD =
            ReflectionUtils.getUnsafeField(AudioNode.class, "audioKey");

    private static final Field AUDIO_KEY_FIELD  =
            ReflectionUtils.getUnsafeField(AudioNode.class, "data");

    /**
     * Update audio data for an audio node.
     *
     * @param audioNode the audio node.
     * @param audioData the audio data.
     * @param audioKey  the audio key.
     */
    @JmeThread
    public static void updateData(
            @NotNull AudioNode audioNode,
            @Nullable AudioData audioData,
            @Nullable AudioKey audioKey
    ) {
        try {
            AUDIO_DATA_FIELD.set(audioNode, audioData);
            AUDIO_KEY_FIELD.set(audioNode, audioKey);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get an audio key from the audio node.
     *
     * @param audioNode the audio node.
     * @return the audio key.
     */
    @FromAnyThread
    public static @Nullable AudioKey getAudioKey(@NotNull AudioNode audioNode) {
        try {
            return (AudioKey) AUDIO_KEY_FIELD.get(audioNode);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
