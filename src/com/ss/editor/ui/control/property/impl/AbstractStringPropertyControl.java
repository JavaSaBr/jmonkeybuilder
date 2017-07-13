package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit string values.
 *
 * @param <C> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractStringPropertyControl<C extends ChangeConsumer, T>
        extends AbstractPropertyControl<C, T, String> {

    /**
     * The filed with current value.
     */
    @Nullable
    private TextField valueField;

    /**
     * Instantiates a new Abstract string property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     * @param changeHandler  the change handler
     */
    public AbstractStringPropertyControl(@Nullable final String propertyValue, @NotNull final String propertyName,
                                         @NotNull final C changeConsumer,
                                         @NotNull final SixObjectConsumer<C, T, String, String, String, BiConsumer<T, String>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        valueField = new TextField();
        valueField.setOnKeyReleased(this::updateValue);
        valueField.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addClassTo(valueField, CSSClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addToPane(valueField, container);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * @return the filed with current value.
     */
    @NotNull
    private TextField getValueField() {
        return notNull(valueField);
    }

    @Override
    protected void reload() {
        final String value = getPropertyValue();
        final TextField valueField = getValueField();
        final int caretPosition = valueField.getCaretPosition();
        valueField.setText(value == null ? "" : value);
        valueField.positionCaret(caretPosition);
    }

    /**
     * Update the value.
     */
    private void updateValue(@NotNull final KeyEvent event) {
        UIUtils.consumeIfIsNotHotKey(event);

        if (isIgnoreListener() || event.getCode() != KeyCode.ENTER) return;

        final TextField valueField = getValueField();

        final String oldValue = getPropertyValue();
        final String newValue = valueField.getText();

        changed(newValue, oldValue);
    }
}
