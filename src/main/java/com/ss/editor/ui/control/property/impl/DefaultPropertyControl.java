package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * The default implementation of the property control.
 *
 * @param <C> the change consumer's type.
 * @param <D> the edited object's type.
 * @param <T> the edited property's type.
 * @author JavaSaBr
 */
public class DefaultPropertyControl<C extends ChangeConsumer, D, T> extends PropertyControl<C, D, T> {

    /**
     * The label with value of the property.
     */
    @Nullable
    protected Label propertyValueLabel;

    /**
     * The string function.
     */
    @Nullable
    private Function<T, String> toStringFunction;

    public DefaultPropertyControl(
            @Nullable T propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
    }

    /**
     * Set the string function.
     *
     * @param toStringFunction the string function.
     */
    @FromAnyThread
    public void setToStringFunction(@Nullable Function<T, String> toStringFunction) {
        this.toStringFunction = toStringFunction;
    }

    /**
     * Get the string function.
     *
     * @return the string function.
     */
    @FromAnyThread
    private @Nullable Function<T, String> getToStringFunction() {
        return toStringFunction;
    }

    /**
     * Get the label with value of the property.
     *
     * @return the label with value of the property.
     */
    protected @NotNull Label getPropertyValueLabel() {
        return notNull(propertyValueLabel);
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull HBox container) {
        super.createComponents(container);

        propertyValueLabel = new Label();
        propertyValueLabel.prefWidthProperty()
                .bind(container.widthProperty());

        FxUtils.addClass(propertyValueLabel,
                CssClasses.ABSTRACT_PARAM_CONTROL_LABEL_VALUE,
                CssClasses.TEXT_INPUT_CONTAINER);

        FxUtils.addChild(container, propertyValueLabel);
    }

    @Override
    @FxThread
    public void reload() {
        super.reload();

        var function = getToStringFunction();
        var propertyValueLabel = getPropertyValueLabel();
        propertyValueLabel.setText(function == null ? String.valueOf(getPropertyValue()) :
                function.apply(getPropertyValue()));
    }
}
