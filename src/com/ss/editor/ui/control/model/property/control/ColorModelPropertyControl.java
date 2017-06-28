package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.jme3.math.ColorRGBA;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractColorPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ModelPropertyControl} for editing a color.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class ColorModelPropertyControl<T> extends AbstractColorPropertyControl<ModelChangeConsumer, T> {


    /**
     * Instantiates a new Color model property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     */
    public ColorModelPropertyControl(@Nullable final ColorRGBA propertyValue, @NotNull final String propertyName,
                                     @NotNull final ModelChangeConsumer changeConsumer) {
        super(propertyValue, propertyName, changeConsumer, newChangeHandler());
    }
}
