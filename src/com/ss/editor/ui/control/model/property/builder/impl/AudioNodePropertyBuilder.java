package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.AudioKeyModelPropertyEditor;
import com.ss.editor.ui.control.model.property.BooleanModelPropertyControl;
import com.ss.editor.ui.control.model.property.FloatModelPropertyControl;
import com.ss.editor.ui.control.model.property.Vector3fModelPropertyControl;
import com.ss.editor.ui.control.model.property.builder.PropertyBuilder;
import com.ss.editor.util.AudioNodeUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link PropertyBuilder} for building property controls for {@link AudioNode} objects.
 *
 * @author JavaSaBr
 */
public class AudioNodePropertyBuilder extends AbstractPropertyBuilder {

    private static final BiConsumer<AudioNode, AudioKey> AUDIO_APPLY_HANDLER = (audioNode, audioKey) -> {

        final AssetManager assetManager = EDITOR.getAssetManager();

        if (audioKey == null) {
            audioNode.setAudioData(null, null);
        } else {

            assetManager.deleteFromCache(audioKey);

            final AudioData audioData = assetManager.loadAudio(audioKey);

            AudioNodeUtils.updateData(audioNode, audioData, audioKey);
        }
    };

    private static final PropertyBuilder INSTANCE = new AudioNodePropertyBuilder();

    public static PropertyBuilder getInstance() {
        return INSTANCE;
    }

    @Override
    public void buildFor(@NotNull final Object object, @Nullable final Object parent, @NotNull final VBox container,
                         @NotNull final ModelChangeConsumer modelChangeConsumer) {

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

        final BooleanModelPropertyControl<AudioNode> loopControl = new BooleanModelPropertyControl<>(looping, "Looping", modelChangeConsumer);
        loopControl.setApplyHandler(AudioNode::setLooping);
        loopControl.setSyncHandler(AudioNode::isLooping);
        loopControl.setEditObject(audioNode);

        final BooleanModelPropertyControl<AudioNode> reverbControl = new BooleanModelPropertyControl<>(reverbEnabled, "Reverb", modelChangeConsumer);
        reverbControl.setApplyHandler(AudioNode::setReverbEnabled);
        reverbControl.setSyncHandler(AudioNode::isReverbEnabled);
        reverbControl.setEditObject(audioNode);

        final BooleanModelPropertyControl<AudioNode> directionalControl = new BooleanModelPropertyControl<>(directional, "Directional", modelChangeConsumer);
        directionalControl.setApplyHandler(AudioNode::setDirectional);
        directionalControl.setSyncHandler(AudioNode::isDirectional);
        directionalControl.setEditObject(audioNode);

        final BooleanModelPropertyControl<AudioNode> positionalControl = new BooleanModelPropertyControl<>(positional, "Positional", modelChangeConsumer);
        positionalControl.setApplyHandler(AudioNode::setPositional);
        positionalControl.setSyncHandler(AudioNode::isPositional);
        positionalControl.setEditObject(audioNode);

        final FloatModelPropertyControl<AudioNode> pitchControl = new FloatModelPropertyControl<>(pitch, "Pitch", modelChangeConsumer);
        pitchControl.setApplyHandler(AudioNode::setPitch);
        pitchControl.setSyncHandler(AudioNode::getPitch);
        pitchControl.setMinMax(0.5F, 2.0F);
        pitchControl.setScrollPower(2F);
        pitchControl.setEditObject(audioNode);

        final FloatModelPropertyControl<AudioNode> volumeControl = new FloatModelPropertyControl<>(volume, "Volume", modelChangeConsumer);
        volumeControl.setApplyHandler(AudioNode::setVolume);
        volumeControl.setSyncHandler(AudioNode::getVolume);
        volumeControl.setMinMax(0F, Float.MAX_VALUE);
        volumeControl.setScrollPower(5F);
        volumeControl.setEditObject(audioNode);

        final FloatModelPropertyControl<AudioNode> timeOffsetControl = new FloatModelPropertyControl<>(timeOffset, "Time offset", modelChangeConsumer);
        timeOffsetControl.setApplyHandler(AudioNode::setTimeOffset);
        timeOffsetControl.setSyncHandler(AudioNode::getTimeOffset);
        timeOffsetControl.setMinMax(0F, Float.MAX_VALUE);
        timeOffsetControl.setEditObject(audioNode);

        final FloatModelPropertyControl<AudioNode> maxDistanceControl = new FloatModelPropertyControl<>(maxDistance, "Max distance", modelChangeConsumer);
        maxDistanceControl.setApplyHandler(AudioNode::setMaxDistance);
        maxDistanceControl.setSyncHandler(AudioNode::getMaxDistance);
        maxDistanceControl.setMinMax(0F, Float.MAX_VALUE);
        maxDistanceControl.setEditObject(audioNode);

        final FloatModelPropertyControl<AudioNode> refDistanceControl = new FloatModelPropertyControl<>(refDistance, "Ref distance", modelChangeConsumer);
        refDistanceControl.setApplyHandler(AudioNode::setRefDistance);
        refDistanceControl.setSyncHandler(AudioNode::getRefDistance);
        refDistanceControl.setMinMax(0F, Float.MAX_VALUE);
        refDistanceControl.setEditObject(audioNode);

        final FloatModelPropertyControl<AudioNode> innerAngleControl = new FloatModelPropertyControl<>(innerAngle, "Inner angle", modelChangeConsumer);
        innerAngleControl.setApplyHandler(AudioNode::setInnerAngle);
        innerAngleControl.setSyncHandler(AudioNode::getInnerAngle);
        innerAngleControl.setEditObject(audioNode);

        final FloatModelPropertyControl<AudioNode> outerAngleControl = new FloatModelPropertyControl<>(outerAngle, "Outer angle", modelChangeConsumer);
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

        addSplitLine(container);

        final AudioKeyModelPropertyEditor audioKeyControl = new AudioKeyModelPropertyEditor(key, "Audio data", modelChangeConsumer);
        audioKeyControl.setApplyHandler(AUDIO_APPLY_HANDLER);
        audioKeyControl.setSyncHandler(AudioNodeUtils::getAudioKey);
        audioKeyControl.setEditObject(audioNode);

        final Vector3fModelPropertyControl<AudioNode> velocityControl = new Vector3fModelPropertyControl<>(velocity, "Velocity", modelChangeConsumer);
        velocityControl.setApplyHandler(AudioNode::setVelocity);
        velocityControl.setSyncHandler(AudioNode::getVelocity);
        velocityControl.setEditObject(audioNode);

        final Vector3fModelPropertyControl<AudioNode> directionControl = new Vector3fModelPropertyControl<>(direction, "Direction", modelChangeConsumer);
        directionControl.setApplyHandler(AudioNode::setDirection);
        directionControl.setSyncHandler(AudioNode::getDirection);
        directionControl.setEditObject(audioNode);

        FXUtils.addToPane(audioKeyControl, container);
        FXUtils.addToPane(velocityControl, container);
        FXUtils.addToPane(directionControl, container);
    }
}
