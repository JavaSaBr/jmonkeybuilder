package com.ss.builder.fx.control.property.impl;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
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

    public DefaultSinglePropertyControl(
            @Nullable T propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected void createControls(@NotNull HBox container) {
        super.createControls(container);
        propertyValueLabel.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }
}
