package com.ss.editor.ui.control.property.impl;

import com.ss.editor.model.undo.editor.ChangeConsumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import javafx.scene.layout.HBox;
import rlib.function.SixObjectConsumer;

/**
 * The default implementation of the property control.
 *
 * @author JavaSaBr
 */
public abstract class AbstractDefaultSinglePropertyControl<C extends ChangeConsumer, D, T> extends AbstractDefaultPropertyControl<C, D, T> {

    public AbstractDefaultSinglePropertyControl(@Nullable final T propertyValue, @NotNull final String propertyName,
                                                @NotNull final C changeConsumer,
                                                @NotNull final SixObjectConsumer<C, D, String, T, T, BiConsumer<D, T>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
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
