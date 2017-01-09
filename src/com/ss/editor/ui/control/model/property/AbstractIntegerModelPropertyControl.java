package com.ss.editor.ui.control.model.property;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.fx.IntegerTextField;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link ModelPropertyControl} for editing integer values.
 *
 * @author JavaSaBr
 */
public abstract class AbstractIntegerModelPropertyControl<T> extends ModelPropertyControl<T, Integer> {

    /**
     * The filed with current value.
     */
    private IntegerTextField valueField;

    public AbstractIntegerModelPropertyControl(@Nullable final Integer element, @NotNull final String paramName,
                                               @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        valueField = new IntegerTextField();
        valueField.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
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
