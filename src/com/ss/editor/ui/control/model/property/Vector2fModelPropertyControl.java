package com.ss.editor.ui.control.model.property;

import com.jme3.math.Vector2f;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;

/**
 * The implementation of the {@link ModelPropertyControl} for editing {@link Vector2f} values.
 *
 * @author JavaSaBr.
 */
public class Vector2fModelPropertyControl<T extends Spatial> extends AbstractVector2fModelPropertyControl<T> {

    public Vector2fModelPropertyControl(final Vector2f element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }
}
