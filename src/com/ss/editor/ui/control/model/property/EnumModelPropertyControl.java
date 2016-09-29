package com.ss.editor.ui.control.model.property;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link ModelPropertyControl} for editing the {@link Enum} values.
 *
 * @author JavaSaBr
 */
public class EnumModelPropertyControl<T extends Spatial, E extends Enum<E>> extends AbstractEnumModelPropertyControl<T, E> {

    public EnumModelPropertyControl(@NotNull final E element, @NotNull final String paramName, @NotNull final ModelChangeConsumer modelChangeConsumer, @NotNull final E[] availableValues) {
        super(element, paramName, modelChangeConsumer, availableValues);
    }
}
