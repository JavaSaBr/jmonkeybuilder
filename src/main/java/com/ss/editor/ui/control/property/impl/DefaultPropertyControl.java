package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * The default implementation of the property control.
 *
 * @param <C> the type parameter
 * @param <D> the type parameter
 * @param <T> the type parameter
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

    public DefaultPropertyControl(@Nullable final T propertyValue, @NotNull final String propertyName,
                                  @NotNull final C changeConsumer) {
        super(propertyValue, propertyName, changeConsumer);
    }

    /**
     * Sets to string function.
     *
     * @param toStringFunction the string function.
     */
    @FromAnyThread
    public void setToStringFunction(@Nullable final Function<T, String> toStringFunction) {
        this.toStringFunction = toStringFunction;
    }

    /**
     * @return the string function.
     */
    @FromAnyThread
    private @Nullable Function<T, String> getToStringFunction() {
        return toStringFunction;
    }

    /**
     * @return the label with value of the property.
     */
    protected @NotNull Label getPropertyValueLabel() {
        return notNull(propertyValueLabel);
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        propertyValueLabel = new Label();
        propertyValueLabel.prefWidthProperty().bind(container.widthProperty());

        FXUtils.addClassesTo(propertyValueLabel, CssClasses.ABSTRACT_PARAM_CONTROL_LABEL_VALUE,
                CssClasses.TEXT_INPUT_CONTAINER);
        FXUtils.addToPane(propertyValueLabel, container);
    }

    @Override
    @FxThread
    public void reload() {
        super.reload();

        final Function<T, String> function = getToStringFunction();

        final Label propertyValueLabel = getPropertyValueLabel();
        propertyValueLabel.setText(function == null ? String.valueOf(getPropertyValue()) :
                function.apply(getPropertyValue()));
    }
}
