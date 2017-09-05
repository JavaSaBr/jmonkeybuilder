package com.ss.editor.ui.control.model.property.control;

import com.jme3.light.Light;
import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.Vector3FPropertyControl;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link Vector3FPropertyControl} to edit direction's vector of the {@link Light}.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class DirectionLightPropertyControl<T extends Light> extends Vector3FPropertyControl<ModelChangeConsumer, T> {

    public DirectionLightPropertyControl(@NotNull final Vector3f element, @NotNull final String paramName,
                                         @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }
}
