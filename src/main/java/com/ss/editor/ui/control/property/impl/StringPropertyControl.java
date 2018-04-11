package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit string values.
 *
 * @param <C> the type of a change consumer.
 * @param <T> the type of an editing property.
 * @author JavaSaBr
 */
public class StringPropertyControl<C extends ChangeConsumer, T> extends PropertyControl<C, T, String> {

    /**
     * The filed with current value.
     */
    @Nullable
    private TextField valueField;

    public StringPropertyControl(
            @Nullable String propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull HBox container) {
        super.createComponents(container);

        valueField = new TextField();
        valueField.setOnKeyReleased(this::updateValue);
        valueField.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addClassTo(valueField, CssClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addToPane(valueField, container);
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * Get the filed with current value.
     *
     * @return the filed with current value.
     */
    @FxThread
    private @NotNull TextField getValueField() {
        return notNull(valueField);
    }

    @Override
    @FxThread
    protected void reload() {
        var value = getPropertyValue();
        var valueField = getValueField();
        var caretPosition = valueField.getCaretPosition();
        valueField.setText(value == null ? "" : value);
        valueField.positionCaret(caretPosition);
    }

    /**
     * Update the value.
     */
    @FxThread
    private void updateValue(@NotNull KeyEvent event) {

        if (isIgnoreListener() || event.getCode() != KeyCode.ENTER) {
            return;
        }

        var valueField = getValueField();
        var oldValue = getPropertyValue();
        var newValue = valueField.getText();

        changed(newValue, oldValue);
    }
}
