package com.ss.editor.ui.control.property.impl;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The default implementation of the property control.
 *
 * @param <C> the type parameter
 * @param <D> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class DefaultSinglePropertyControl<C extends ChangeConsumer, D, T> extends DefaultPropertyControl<C, D, T> {

    public DefaultSinglePropertyControl(@Nullable final T propertyValue, @NotNull final String propertyName,
                                        @NotNull final C changeConsumer) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FXThread
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
