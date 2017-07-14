package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * The default implementation of the property control.
 *
 * @param <C> the type parameter
 * @param <D> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractDefaultPropertyControl<C extends ChangeConsumer, D, T>
        extends AbstractPropertyControl<C, D, T> {

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

    /**
     * Instantiates a new Abstract default property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     * @param changeHandler  the change handler
     */
    public AbstractDefaultPropertyControl(@Nullable final T propertyValue, @NotNull final String propertyName,
                                          @NotNull final C changeConsumer,
                                          @NotNull final SixObjectConsumer<C, D, String, T, T, BiConsumer<D, T>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    /**
     * Sets to string function.
     *
     * @param toStringFunction the string function.
     */
    public void setToStringFunction(@Nullable final Function<T, String> toStringFunction) {
        this.toStringFunction = toStringFunction;
    }

    /**
     * @return the string function.
     */
    @Nullable
    private Function<T, String> getToStringFunction() {
        return toStringFunction;
    }

    /**
     * @return the label with value of the property.
     */
    @NotNull
    protected Label getPropertyValueLabel() {
        return notNull(propertyValueLabel);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        propertyValueLabel = new Label();
        propertyValueLabel.prefWidthProperty().bind(container.widthProperty());

        FXUtils.addClassesTo(propertyValueLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_LABEL_VALUE,
                CSSClasses.TEXT_INPUT_CONTAINER);

        FXUtils.addToPane(propertyValueLabel, container);
    }

    @Override
    public void reload() {
        super.reload();

        final Function<T, String> function = getToStringFunction();

        final Label propertyValueLabel = getPropertyValueLabel();
        propertyValueLabel.setText(function == null ? String.valueOf(getPropertyValue()) :
                function.apply(getPropertyValue()));
    }
}
