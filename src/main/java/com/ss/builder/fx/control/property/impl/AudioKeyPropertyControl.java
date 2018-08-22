package com.ss.builder.ui.control.property.impl;

import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.ss.builder.FileExtensions;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.util.EditorUtils;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.util.EditorUtils;
import com.ss.rlib.common.util.array.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The implementation of the {@link PropertyControl} to edit the {@link AudioData}.
 *
 * @param <C> the change consumer's type.
 * @param <E> the edited object's type.
 * @author JavaSaBr
 */
public class AudioKeyPropertyControl<C extends ChangeConsumer, E> extends
        AssetKeyPropertyControl<C, E, AudioData, AudioKey> {

    private static final String NO_AUDIO =
            Messages.AUDIO_KEY_PROPERTY_CONTROL_NO_AUDIO;

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
        changed(EditorUtils.realFileToKey(file, AudioKey::new), getPropertyValue());
        super.applyNewKey(file);
    }
}
