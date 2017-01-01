package com.ss.editor.ui.control.model.property.builder.impl;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.property.AudioKeyModelPropertyEditor;
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

        final AudioKeyModelPropertyEditor audioKeyControl = new AudioKeyModelPropertyEditor(key, "Audio data", modelChangeConsumer);
        audioKeyControl.setApplyHandler(AUDIO_APPLY_HANDLER);
        audioKeyControl.setSyncHandler(AudioNodeUtils::getAudioKey);
        audioKeyControl.setEditObject(audioNode);

        FXUtils.addToPane(audioKeyControl, container);

        addSplitLine(container);
    }
}
