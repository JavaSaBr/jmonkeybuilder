package com.ss.editor.ui.control.model.property;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;

import org.jetbrains.annotations.NotNull;

import javafx.scene.layout.HBox;

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
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);
        propertyValueLabel.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }
}
