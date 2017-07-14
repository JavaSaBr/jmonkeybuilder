package com.ss.editor.ui.control.property.impl;

import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.rlib.function.SixObjectConsumer;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The default implementation of the property control.
 *
 * @param <C> the type parameter
 * @param <D> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractDefaultSinglePropertyControl<C extends ChangeConsumer, D, T>
        extends AbstractDefaultPropertyControl<C, D, T> {

    /**
     * Instantiates a new Abstract default single property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     * @param changeHandler  the change handler
     */
    public AbstractDefaultSinglePropertyControl(@Nullable final T propertyValue, @NotNull final String propertyName,
                                                @NotNull final C changeConsumer,
                                                @NotNull final SixObjectConsumer<C, D, String, T, T, BiConsumer<D, T>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);
        getPropertyValueLabel()
                .prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }
}
