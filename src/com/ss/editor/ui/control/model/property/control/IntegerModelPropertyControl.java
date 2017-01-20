package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractIntegerPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ModelPropertyControl} to edit integer values.
 *
 * @author JavaSaBr
 */
public class IntegerModelPropertyControl<T extends Spatial> extends AbstractIntegerPropertyControl<ModelChangeConsumer, T> {

    public IntegerModelPropertyControl(@Nullable final Integer element, @NotNull final String paramName,
                                       @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer, newChangeHandler());
    }
}
