package com.ss.editor.ui.control.model.property;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;

/**
 * The default implementation of the property control.
 *
 * @author JavaSaBr
 */
public class DefaultModelSinglePropertyControl<T> extends DefaultModelPropertyControl<T> {

    public DefaultModelSinglePropertyControl(final T element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }
}
