package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.ArrayUtils;
import com.ss.rlib.util.StringUtils;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit int array values.
 *
 * @param <C> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractIntArrayPropertyControl<C extends ChangeConsumer, T>
        extends AbstractPropertyControl<C, T, int[]> {

    /**
     * The filed with current value.
     */
    @Nullable
    private TextField valueField;

    /**
     * Instantiates a new Abstract int array property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     * @param changeHandler  the change handler
     */
    public AbstractIntArrayPropertyControl(@Nullable final int[] propertyValue, @NotNull final String propertyName,
                                           @NotNull final C changeConsumer,
                                           @NotNull final SixObjectConsumer<C, T, String, int[], int[], BiConsumer<T, int[]>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        valueField = new TextField();
        valueField.setOnKeyReleased(this::updateValue);
        valueField.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

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
    private void updateValue(@Nullable final KeyEvent event) {
        UIUtils.consumeIfIsNotHotKey(event);

        if (isIgnoreListener() || (event != null && event.getCode() != KeyCode.ENTER)) return;

        final String textValue = getValueField().getText();
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
