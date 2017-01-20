package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractColorPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ModelPropertyControl} for editing a color.
 *
 * @author JavaSaBr
 */
public class ColorModelPropertyControl<T extends Spatial> extends AbstractColorPropertyControl<ModelChangeConsumer, T> {


    public ColorModelPropertyControl(@Nullable final ColorRGBA propertyValue, @NotNull final String propertyName,
                                     @NotNull final ModelChangeConsumer changeConsumer) {
        super(propertyValue, propertyName, changeConsumer, newChangeHandler());
    }
}
