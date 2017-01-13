package com.ss.editor.ui.control.model.property;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractVector3fModelPropertyControl} for editing vector3f values in the {@link
 * Spatial}.
 *
 * @author JavaSaBr
 */
public class Vector3fModelPropertyControl<T extends Spatial> extends AbstractVector3fModelPropertyControl<T> {

    public Vector3fModelPropertyControl(@NotNull final Vector3f element, @NotNull final String paramName,
                                        @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }
}
