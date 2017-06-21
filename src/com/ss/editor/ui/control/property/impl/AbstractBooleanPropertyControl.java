package com.ss.editor.ui.control.property.impl;

import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link AbstractPropertyControl} to change boolean values.
 *
 * @param <C> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractBooleanPropertyControl<C extends ChangeConsumer, T>
        extends AbstractPropertyControl<C, T, Boolean> {

    /**
     * The {@link CheckBox} with current value.
     */
    private CheckBox checkBox;

    /**
     * Instantiates a new Abstract boolean property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     * @param changeHandler  the change handler
     */
    public AbstractBooleanPropertyControl(@Nullable final Boolean propertyValue, @NotNull final String propertyName,
                                          @NotNull final C changeConsumer,
                                          @NotNull final SixObjectConsumer<C, T, String, Boolean, Boolean, BiConsumer<T, Boolean>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        checkBox = new CheckBox();
        checkBox.setId(CSSIds.ABSTRACT_PARAM_CONTROL_CHECK_BOX);
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> updateValue());
        checkBox.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addToPane(checkBox, container);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * @return the {@link CheckBox} with current value.
     */
    private CheckBox getCheckBox() {
        return checkBox;
    }

    @Override
    protected void reload() {
        final Boolean value = getPropertyValue();
        final CheckBox checkBox = getCheckBox();
        checkBox.setSelected(value == Boolean.TRUE);
    }

    /**
     * Update the value.
     */
    private void updateValue() {
        if (isIgnoreListener()) return;
        final CheckBox checkBox = getCheckBox();
        changed(checkBox.isSelected(), getPropertyValue());
    }
}
