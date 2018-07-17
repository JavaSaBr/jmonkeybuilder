package com.ss.editor.ui.control.property.impl;

import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The implementation of the {@link PropertyControl} to edit the {@link AudioData}.
 *
 * @param <C> the type of a change consumer.
 * @author JavaSaBr
 */
public class AudioKeyPropertyControl<C extends ChangeConsumer> extends
        AssetKeyPropertyControl<C, AudioNode, AudioData, AudioKey> {

    private static final String NO_AUDIO = Messages.AUDIO_KEY_PROPERTY_CONTROL_NO_AUDIO;

    public AudioKeyPropertyControl(
            @Nullable AudioKey element,
            @NotNull String paramName,
            @NotNull C changeConsumer
    ) {
        super(element, paramName, changeConsumer);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getNoKeyLabel() {
        return NO_AUDIO;
    }

    @Override
    @FromAnyThread
    protected @NotNull Array<String> getExtensions() {
        return FileExtensions.AUDIO_EXTENSIONS;
    }

    @Override
    @FxThread
    protected void applyNewKey(@NotNull Path file) {
        changed(EditorUtil.realFileToKey(file, AudioKey::new), getPropertyValue());
        super.applyNewKey(file);
    }
}
