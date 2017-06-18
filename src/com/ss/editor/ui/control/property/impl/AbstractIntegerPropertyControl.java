package com.ss.editor.ui.control.property.impl;

import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.util.UIUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import javafx.scene.layout.HBox;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.ui.control.input.IntegerTextField;
import com.ss.rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit integer values.
 *
 * @author JavaSaBr
 */
public abstract class AbstractIntegerPropertyControl<C extends ChangeConsumer, T>
        extends AbstractPropertyControl<C, T, Integer> {

    /**
     * The filed with current value.
     */
    private IntegerTextField valueField;

    public AbstractIntegerPropertyControl(@Nullable final Integer propertyValue, @NotNull final String propertyName,
                                          @NotNull final C changeConsumer,
                                          @NotNull final SixObjectConsumer<C, T, String, Integer, Integer, BiConsumer<T, Integer>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        valueField = new IntegerTextField();
        valueField.setId(CSSIds.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        valueField.setOnKeyReleased(UIUtils::consumeIfIsNotHotKey);
        valueField.addChangeListener((observable, oldValue, newValue) -> updateValue());
        valueField.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addClassTo(valueField, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addToPane(valueField, container);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * @return the filed with current value.
     */
    private IntegerTextField getValueField() {
        return valueField;
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
