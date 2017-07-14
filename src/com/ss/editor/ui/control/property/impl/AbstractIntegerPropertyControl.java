package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.ui.control.input.IntegerTextField;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit integer values.
 *
 * @param <C> the type of {@link ChangeConsumer}
 * @param <T> the type of edited object
 * @author JavaSaBr
 */
public abstract class AbstractIntegerPropertyControl<C extends ChangeConsumer, T>
        extends AbstractPropertyControl<C, T, Integer> {

    /**
     * The filed with current value.
     */
    @Nullable
    private IntegerTextField valueField;

    /**
     * Instantiates a new Abstract integer property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     * @param changeHandler  the change handler
     */
    public AbstractIntegerPropertyControl(@Nullable final Integer propertyValue, @NotNull final String propertyName,
                                          @NotNull final C changeConsumer,
                                          @NotNull final SixObjectConsumer<C, T, String, Integer, Integer, BiConsumer<T, Integer>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        valueField = new IntegerTextField();
        valueField.setOnKeyReleased(UIUtils::consumeIfIsNotHotKey);
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
    @NotNull
    private IntegerTextField getValueField() {
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
