package com.ss.editor.ui.control.property.impl;

import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import rlib.function.SixObjectConsumer;
import rlib.ui.util.FXUtils;
import rlib.util.ArrayUtils;
import rlib.util.StringUtils;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit int array values.
 *
 * @author JavaSaBr
 */
public abstract class AbstractIntArrayPropertyControl<C extends ChangeConsumer, T>
        extends AbstractPropertyControl<C, T, int[]> {

    /**
     * The filed with current value.
     */
    private TextField valueField;

    public AbstractIntArrayPropertyControl(@Nullable final int[] propertyValue, @NotNull final String propertyName,
                                           @NotNull final C changeConsumer,
                                           @NotNull final SixObjectConsumer<C, T, String, int[], int[], BiConsumer<T, int[]>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        valueField = new TextField();
        valueField.setId(CSSIds.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        valueField.setOnKeyReleased(this::updateValue);
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
    private TextField getValueField() {
        return valueField;
    }

    @Override
    protected void reload() {

        final int[] element = getPropertyValue();

        final TextField valueField = getValueField();
        final int caretPosition = valueField.getCaretPosition();

        if (element == null) {
            valueField.setText(StringUtils.EMPTY);
        } else {
            valueField.setText(ArrayUtils.toString(element, " ", false, false));
        }

        valueField.positionCaret(caretPosition);
    }

    /**
     * Update the value.
     */
    private void updateValue(final KeyEvent event) {
        if (event != null) event.consume();
        if (isIgnoreListener() || (event != null && event.getCode() != KeyCode.ENTER)) return;

        final String textValue = valueField.getText();
        int[] newValue = null;

        if (!StringUtils.isEmpty(textValue)) {

            final String splitter = textValue.contains(" ") ? " " : ",";
            final String[] splited = textValue.split(splitter);

            newValue = new int[splited.length];

            for (int i = 0; i < splited.length; i++) {
                try {
                    newValue[i] = Integer.parseInt(splited[i]);
                } catch (final NumberFormatException e) {
                    LOGGER.warning(this, e);
                    newValue = getPropertyValue();
                    break;
                }
            }
        }

        changed(newValue, getPropertyValue());
    }
}
