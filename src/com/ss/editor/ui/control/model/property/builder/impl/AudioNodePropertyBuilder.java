package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.control.AudioKeyModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.BooleanModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.FloatModelPropertyControl;
import com.ss.editor.ui.control.model.property.control.Vector3fModelPropertyControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilder;
import com.ss.editor.ui.control.property.builder.impl.AbstractPropertyBuilder;
import com.ss.editor.util.AudioNodeUtils;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.ss.rlib.ui.util.FXUtils;

import java.util.function.BiConsumer;

/**
 * The implementation of the {@link PropertyBuilder} to build property controls for {@link AudioNode} objects.
 *
 * @author JavaSaBr
 */
public class AudioNodePropertyBuilder extends AbstractPropertyBuilder<ModelChangeConsumer> {

    @NotNull
    private static final BiConsumer<AudioNode, AudioKey> AUDIO_APPLY_HANDLER = (audioNode, audioKey) -> {

        final AssetManager assetManager = EDITOR.getAssetManager();

        if (audioKey == null) {
            audioNode.setAudioData(null, null);
        } else {
            final AudioData audioData = assetManager.loadAudio(audioKey);
            AudioNodeUtils.updateData(audioNode, audioData, audioKey);
        }
    };

    @NotNull
    private static final PropertyBuilder INSTANCE = new AudioNodePropertyBuilder();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    private AudioNodePropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    protected void buildForImpl(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                                @NotNull final ModelChangeConsumer changeConsumer) {

        if (!(object instanceof AudioNode)) return;

        final AudioNode audioNode = (AudioNode) object;
        final AudioKey key = AudioNodeUtils.getAudioKey(audioNode);

        final Vector3f velocity = audioNode.getVelocity();
        final Vector3f direction = audioNode.getDirection();

        final float pitch = audioNode.getPitch();
        final float volume = audioNode.getVolume();
        final float timeOffset = audioNode.getTimeOffset();
        final float maxDistance = audioNode.getMaxDistance();
        final float refDistance = audioNode.getRefDistance();
        final float innerAngle = audioNode.getInnerAngle();
        final float outerAngle = audioNode.getOuterAngle();

        final boolean looping = audioNode.isLooping();
        final boolean reverbEnabled = audioNode.isReverbEnabled();
        final boolean directional = audioNode.isDirectional();
        final boolean positional = audioNode.isPositional();

        final BooleanModelPropertyControl<AudioNode> loopControl = new BooleanModelPropertyControl<>(looping,
                Messages.MODEL_PROPERTY_IS_LOOPING, changeConsumer);

        loopControl.setApplyHandler(AudioNode::setLooping);
        loopControl.setSyncHandler(AudioNode::isLooping);
        loopControl.setEditObject(audioNode);

        final BooleanModelPropertyControl<AudioNode> reverbControl = new BooleanModelPropertyControl<>(reverbEnabled,
                Messages.MODEL_PROPERTY_IS_REVERB, changeConsumer);

        reverbControl.setApplyHandler(AudioNode::setReverbEnabled);
        reverbControl.setSyncHandler(AudioNode::isReverbEnabled);
        reverbControl.setEditObject(audioNode);

        final BooleanModelPropertyControl<AudioNode> directionalControl = new BooleanModelPropertyControl<>(directional,
                Messages.MODEL_PROPERTY_IS_DIRECTIONAL, changeConsumer);

        directionalControl.setApplyHandler(AudioNode::setDirectional);
        directionalControl.setSyncHandler(AudioNode::isDirectional);
        directionalControl.setEditObject(audioNode);

        final BooleanModelPropertyControl<AudioNode> positionalControl = new BooleanModelPropertyControl<>(positional,
                Messages.MODEL_PROPERTY_IS_POSITIONAL, changeConsumer);

        positionalControl.setApplyHandler(AudioNode::setPositional);
        positionalControl.setSyncHandler(AudioNode::isPositional);
        positionalControl.setEditObject(audioNode);

        final FloatModelPropertyControl<AudioNode> pitchControl = new FloatModelPropertyControl<>(pitch,
                Messages.MODEL_PROPERTY_AUDIO_PITCH, changeConsumer);

        pitchControl.setApplyHandler(AudioNode::setPitch);
        pitchControl.setSyncHandler(AudioNode::getPitch);
        pitchControl.setMinMax(0.5F, 2.0F);
        pitchControl.setScrollPower(2F);
        pitchControl.setEditObject(audioNode);

        final FloatModelPropertyControl<AudioNode> volumeControl = new FloatModelPropertyControl<>(volume,
                Messages.MODEL_PROPERTY_AUDIO_VOLUME, changeConsumer);

        volumeControl.setApplyHandler(AudioNode::setVolume);
        volumeControl.setSyncHandler(AudioNode::getVolume);
        volumeControl.setMinMax(0F, Float.MAX_VALUE);
        volumeControl.setScrollPower(5F);
        volumeControl.setEditObject(audioNode);

        final FloatModelPropertyControl<AudioNode> timeOffsetControl = new FloatModelPropertyControl<>(timeOffset,
                Messages.MODEL_PROPERTY_TIME_OFFSET, changeConsumer);

        timeOffsetControl.setApplyHandler(AudioNode::setTimeOffset);
        timeOffsetControl.setSyncHandler(AudioNode::getTimeOffset);
        timeOffsetControl.setMinMax(0F, Float.MAX_VALUE);
        timeOffsetControl.setEditObject(audioNode);

        final FloatModelPropertyControl<AudioNode> maxDistanceControl = new FloatModelPropertyControl<>(maxDistance,
                Messages.MODEL_PROPERTY_MAX_DISTANCE, changeConsumer);

        maxDistanceControl.setApplyHandler(AudioNode::setMaxDistance);
        maxDistanceControl.setSyncHandler(AudioNode::getMaxDistance);
        maxDistanceControl.setMinMax(0F, Float.MAX_VALUE);
        maxDistanceControl.setEditObject(audioNode);

        final FloatModelPropertyControl<AudioNode> refDistanceControl = new FloatModelPropertyControl<>(refDistance,
                Messages.MODEL_PROPERTY_REF_DISTANCE, changeConsumer);

        refDistanceControl.setApplyHandler(AudioNode::setRefDistance);
        refDistanceControl.setSyncHandler(AudioNode::getRefDistance);
        refDistanceControl.setMinMax(0F, Float.MAX_VALUE);
        refDistanceControl.setEditObject(audioNode);

        final FloatModelPropertyControl<AudioNode> innerAngleControl = new FloatModelPropertyControl<>(innerAngle,
                Messages.MODEL_PROPERTY_INNER_ANGLE, changeConsumer);

        innerAngleControl.setApplyHandler(AudioNode::setInnerAngle);
        innerAngleControl.setSyncHandler(AudioNode::getInnerAngle);
        innerAngleControl.setEditObject(audioNode);

        final FloatModelPropertyControl<AudioNode> outerAngleControl = new FloatModelPropertyControl<>(outerAngle,
                Messages.MODEL_PROPERTY_OUTER_ANGLE, changeConsumer);

        outerAngleControl.setApplyHandler(AudioNode::setOuterAngle);
        outerAngleControl.setSyncHandler(AudioNode::getOuterAngle);
        outerAngleControl.setEditObject(audioNode);

        FXUtils.addToPane(loopControl, container);
        FXUtils.addToPane(reverbControl, container);
        FXUtils.addToPane(directionalControl, container);
        FXUtils.addToPane(positionalControl, container);
        FXUtils.addToPane(pitchControl, container);
        FXUtils.addToPane(volumeControl, container);
        FXUtils.addToPane(timeOffsetControl, container);
        FXUtils.addToPane(maxDistanceControl, container);
        FXUtils.addToPane(refDistanceControl, container);
        FXUtils.addToPane(innerAngleControl, container);
        FXUtils.addToPane(outerAngleControl, container);

        final AudioKeyModelPropertyControl audioKeyControl = new AudioKeyModelPropertyControl(key,
                Messages.MODEL_PROPERTY_AUDIO_DATA, changeConsumer);

        audioKeyControl.setApplyHandler(AUDIO_APPLY_HANDLER);
        audioKeyControl.setSyncHandler(AudioNodeUtils::getAudioKey);
        audioKeyControl.setEditObject(audioNode);

        final Vector3fModelPropertyControl<AudioNode> velocityControl = new Vector3fModelPropertyControl<>(velocity,
                Messages.MODEL_PROPERTY_VELOCITY, changeConsumer);

        velocityControl.setApplyHandler(AudioNode::setVelocity);
        velocityControl.setSyncHandler(AudioNode::getVelocity);
        velocityControl.setEditObject(audioNode);

        final Vector3fModelPropertyControl<AudioNode> directionControl = new Vector3fModelPropertyControl<>(direction,
                Messages.MODEL_PROPERTY_DIRECTION, changeConsumer);

        directionControl.setApplyHandler(AudioNode::setDirection);
        directionControl.setSyncHandler(AudioNode::getDirection);
        directionControl.setEditObject(audioNode);

        buildSplitLine(container);

        FXUtils.addToPane(audioKeyControl, container);
        FXUtils.addToPane(velocityControl, container);
        FXUtils.addToPane(directionControl, container);

        buildSplitLine(container);
    }
}
