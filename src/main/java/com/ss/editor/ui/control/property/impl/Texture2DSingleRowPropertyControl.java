package com.ss.editor.ui.control.property.impl;

import com.jme3.texture.Texture2D;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The single row implementation of the control to edit textures.
 *
 * @param <C> the type of a {@link ChangeConsumer}
 * @param <T> the type of an editing object.
 * @author JavaSaBr
 */
public class Texture2DSingleRowPropertyControl<C extends ChangeConsumer, T> extends Texture2dPropertyControl<C, T> {

    public Texture2DSingleRowPropertyControl(@Nullable final Texture2D propertyValue,
                                             @NotNull final String propertyName, @NotNull final C changeConsumer) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }
}
