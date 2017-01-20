package com.ss.editor.ui.control.model.property.control;

import static com.ss.editor.ui.control.model.property.control.ModelPropertyControl.newChangeHandler;

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.property.impl.AbstractVector3fPropertyControl;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractVector3fPropertyControl} to edot vector3f values.
 *
 * @author JavaSaBr
 */
public class Vector3fModelPropertyControl<T extends Spatial> extends AbstractVector3fPropertyControl<ModelChangeConsumer, T> {

    public Vector3fModelPropertyControl(@NotNull final Vector3f element, @NotNull final String paramName,
                                        @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer, newChangeHandler());
    }
}
