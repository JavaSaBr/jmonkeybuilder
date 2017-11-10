package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CSSClasses;
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
 * @param <C> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class StringPropertyControl<C extends ChangeConsumer, T> extends PropertyControl<C, T, String> {

    /**
     * The filed with current value.
     */
    @Nullable
    private TextField valueField;

    public StringPropertyControl(@Nullable final String propertyValue, @NotNull final String propertyName,
                                 @NotNull final C changeConsumer) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FXThread
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        valueField = new TextField();
        valueField.setOnKeyReleased(this::updateValue);
        valueField.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addClassTo(valueField, CSSClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addToPane(valueField, container);
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * @return the filed with current value.
     */
    @FXThread
    private @NotNull TextField getValueField() {
        return notNull(valueField);
    }

    @Override
    @FXThread
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
    @FXThread
    private void updateValue(@NotNull final KeyEvent event) {
        if (isIgnoreListener() || event.getCode() != KeyCode.ENTER) return;

        final TextField valueField = getValueField();

        final String oldValue = getPropertyValue();
        final String newValue = valueField.getText();

        changed(newValue, oldValue);
    }
}
