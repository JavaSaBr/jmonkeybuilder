package com.ss.editor.ui.control.model.property;

import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;

/**
 * The implementation of the {@link ModelPropertyControl} for changing integer values.
 *
 * @author JavaSaBr
 */
public class IntegerModelPropertyControl<T extends Spatial> extends AbstractIntegerModelPropertyControl<T> {

    public IntegerModelPropertyControl(final Integer element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }
}
