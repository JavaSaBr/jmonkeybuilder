package com.ss.editor.ui.control.property.impl;

import com.jme3.texture.Texture2D;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The single row implementation of the control to edit textures.
 *
 * @param <C> the type of a change consumer.
 * @param <D> the type of an editing object.
 * @author JavaSaBr
 */
public class Texture2dSingleRowPropertyControl<C extends ChangeConsumer, D> extends Texture2dPropertyControl<C, D> {

    public Texture2dSingleRowPropertyControl(
            @Nullable Texture2D propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }
}
