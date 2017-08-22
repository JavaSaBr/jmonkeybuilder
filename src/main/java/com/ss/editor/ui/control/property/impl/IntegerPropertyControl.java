package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.control.input.IntegerTextField;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit integer values.
 *
 * @param <C> the type of {@link ChangeConsumer}
 * @param <T> the type of edited object
 * @author JavaSaBr
 */
public class IntegerPropertyControl<C extends ChangeConsumer, T> extends PropertyControl<C, T, Integer> {

    /**
     * The filed with current value.
     */
    @Nullable
    private IntegerTextField valueField;

    public IntegerPropertyControl(@Nullable final Integer propertyValue, @NotNull final String propertyName,
                                  @NotNull final C changeConsumer) {
        super(propertyValue, propertyName, changeConsumer);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        valueField = new IntegerTextField();
        valueField.addChangeListener((observable, oldValue, newValue) -> updateValue());
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
    private @NotNull IntegerTextField getValueField() {
        return notNull(valueField);
    }

    @Override
    protected void reload() {
        final Integer element = getPropertyValue();
        final IntegerTextField valueField = getValueField();
        final int caretPosition = valueField.getCaretPosition();
        valueField.setText(String.valueOf(element));
        valueField.positionCaret(caretPosition);
    }

    /**
     * Update the value.
     */
    private void updateValue() {
        if (isIgnoreListener()) return;

        final IntegerTextField valueField = getValueField();
        final int value = valueField.getValue();
        final Integer oldValue = getPropertyValue();

        changed(value, oldValue);
    }
}
