package com.ss.editor.ui.control.property.impl;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The default implementation of the property control.
 *
 * @param <C> the change consumer's type.
 * @param <D> the edited object's type.
 * @param <T> the edited property's type.
 * @author JavaSaBr
 */
public class DefaultSinglePropertyControl<C extends ChangeConsumer, D, T> extends DefaultPropertyControl<C, D, T> {

    public DefaultSinglePropertyControl(@Nullable final T propertyValue, @NotNull final String propertyName,
                                        @NotNull final C changeConsumer) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);
        getPropertyValueLabel().prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }
}
