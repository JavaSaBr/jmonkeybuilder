package com.ss.editor.ui.control.model.property;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

/**
 * The default implementation of the property control.
 *
 * @author JavaSaBr
 */
public class DefaultModelPropertyControl<T> extends ModelPropertyControl<Object, T> {

    /**
     * The label with value of the property.
     */
    protected Label propertyValueLabel;

    /**
     * The string function.
     */
    private Function<T, String> toStringFunction;

    public DefaultModelPropertyControl(final T element, final String paramName, final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    /**
     * @param toStringFunction the string function.
     */
    public void setToStringFunction(final Function<T, String> toStringFunction) {
        this.toStringFunction = toStringFunction;
    }

    /**
     * @return the string function.
     */
    private Function<T, String> getToStringFunction() {
        return toStringFunction;
    }

    /**
     * @return the label with value of the property.
     */
    private Label getPropertyValueLabel() {
        return propertyValueLabel;
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        propertyValueLabel = new Label();
        propertyValueLabel.setId(CSSIds.MODEL_PARAM_CONTROL_LABEL_VALUE);

        FXUtils.addClassTo(propertyValueLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addToPane(propertyValueLabel, container);
    }

    @Override
    public void reload() {
        super.reload();

        final Function<T, String> func = getToStringFunction();

        final Label propertyValueLabel = getPropertyValueLabel();
        propertyValueLabel.setText(func == null ? String.valueOf(getPropertyValue()) : func.apply(getPropertyValue()));
    }
}
