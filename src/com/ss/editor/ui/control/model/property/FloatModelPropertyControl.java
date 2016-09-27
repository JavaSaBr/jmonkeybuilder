package com.ss.editor.ui.control.model.property;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;

/**
 * The implementation of the {@link ModelPropertyControl} for editing float values.
 *
 * @author JavaSaBr
 */
public class FloatModelPropertyControl<T extends Spatial> extends AbstractFloatModelPropertyControl<T> {

    public FloatModelPropertyControl(final Float element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }
}
