package com.ss.editor.ui.control.model.property;

import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link ModelPropertyControl} for editing {@link Vector2f} values.
 *
 * @author JavaSaBr.
 */
public class Vector2fModelPropertyControl<T extends Spatial> extends AbstractVector2fModelPropertyControl<T> {

    public Vector2fModelPropertyControl(@NotNull final Vector2f element, @NotNull final String paramName,
                                        @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }
}
