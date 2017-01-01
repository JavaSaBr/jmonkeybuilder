package com.ss.editor.ui.control.model.property;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link ModelPropertyControl} for changing boolean values.
 *
 * @author JavaSaBr
 */
public class BooleanModelPropertyControl<T extends Spatial> extends AbstractBooleanModelPropertyControl<T> {

    public BooleanModelPropertyControl(@NotNull final Boolean element, @NotNull final String paramName,
                                       @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }
}
