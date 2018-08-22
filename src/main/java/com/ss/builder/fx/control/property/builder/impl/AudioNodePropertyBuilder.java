package com.ss.builder.fx.control.property.builder.impl;

import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.util.AudioNodeUtils;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.editor.extension.property.*;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.fx.control.property.builder.PropertyBuilder;
import com.ss.builder.util.AudioNodeUtils;
import com.ss.builder.util.EditorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.ss.editor.extension.property.EditablePropertyType.*;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link AudioNode} objects.
 *
 * @author JavaSaBr
 */
public class AudioNodePropertyBuilder extends EditableModelObjectPropertyBuilder {

    private static final Setter<AudioNode, AudioKey> AUDIO_APPLY_HANDLER = (audioNode, audioKey) -> {

        var assetManager = EditorUtils.getAssetManager();

        if (audioKey == null) {
            AudioNodeUtils.updateData(audioNode, null, null);
        } else {
            AudioNodeUtils.updateData(audioNode, assetManager.loadAudio(audioKey), audioKey);
        }
    };

    private static final PropertyBuilder INSTANCE = new AudioNodePropertyBuilder();

    @FromAnyThread
    public static @NotNull PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private AudioNodePropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull Object object) {

        if (!(object instanceof AudioNode)) {
            return null;
        }

        var audioNode = (AudioNode) object;
        var properties = new ArrayList<EditableProperty<?, ?>>();

        properties.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_LOOPING, audioNode,
                AudioNode::isLooping, AudioNode::setLooping));
        properties.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_REVERB, audioNode,
                AudioNode::isReverbEnabled, AudioNode::setReverbEnabled));
        properties.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_DIRECTIONAL, audioNode,
                AudioNode::isDirectional, AudioNode::setDirectional));
        properties.add(new SimpleProperty<>(BOOLEAN, Messages.MODEL_PROPERTY_IS_DIRECTIONAL, audioNode,
                AudioNode::isPositional, AudioNode::setPositional));
        properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_AUDIO_PITCH, 2F, 0.5F, 2F, audioNode,
                AudioNode::getPitch, AudioNode::setPitch));
        properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_AUDIO_VOLUME, 5F, 0F, Integer.MAX_VALUE, audioNode,
                AudioNode::getVolume, AudioNode::setVolume));
        properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_TIME_OFFSET, 1F, 0F, Integer.MAX_VALUE, audioNode,
                AudioNode::getTimeOffset, AudioNode::setTimeOffset));
        properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_MAX_DISTANCE, 1F, 0F, Integer.MAX_VALUE, audioNode,
                AudioNode::getMaxDistance, AudioNode::setMaxDistance));
        properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_REF_DISTANCE, 1F, 0F, Integer.MAX_VALUE, audioNode,
                AudioNode::getRefDistance, AudioNode::setRefDistance));
        properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_INNER_ANGLE, audioNode,
                AudioNode::getInnerAngle, AudioNode::setInnerAngle));
        properties.add(new SimpleProperty<>(FLOAT, Messages.MODEL_PROPERTY_OUTER_ANGLE, audioNode,
                AudioNode::getOuterAngle, AudioNode::setOuterAngle));

        properties.add(SeparatorProperty.getInstance());

        properties.add(new SimpleProperty<>(AUDIO_KEY, Messages.MODEL_PROPERTY_AUDIO_DATA, audioNode,
                AudioNodeUtils::getAudioKey, AUDIO_APPLY_HANDLER));
        properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_VELOCITY, audioNode,
                AudioNode::getVelocity, AudioNode::setVelocity));
        properties.add(new SimpleProperty<>(VECTOR_3F, Messages.MODEL_PROPERTY_DIRECTION, audioNode,
                AudioNode::getDirection, AudioNode::setDirection));

        return properties;
    }
}
