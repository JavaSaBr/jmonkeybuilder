package com.ss.editor.ui.control.model.property;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ModelPropertyControl} for editing float values.
 *
 * @author JavaSaBr
 */
public class FloatModelPropertyControl<T extends Spatial> extends AbstractFloatModelPropertyControl<T> {

    public FloatModelPropertyControl(@Nullable final Float element, @NotNull final String paramName,
                                     @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }
}
