package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CSSClasses;
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
 * The implementation of the {@link PropertyControl} to edit float array values.
 *
 * @param <C> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class FloatArrayPropertyControl<C extends ChangeConsumer, T> extends PropertyControl<C, T, float[]> {

    /**
     * The filed with current value.
     */
    @Nullable
    private TextField valueField;

    public FloatArrayPropertyControl(@Nullable final float[] propertyValue, @NotNull final String propertyName,
                                     @NotNull final C changeConsumer) {
        super(propertyValue, propertyName, changeConsumer);
    }

    public FloatArrayPropertyControl(@Nullable final float[] propertyValue, @NotNull final String propertyName,
                                     @NotNull final C changeConsumer,
                                     @Nullable final SixObjectConsumer<C, T, String, float[], float[], BiConsumer<T, float[]>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    @FXThread
    public void changeControlWidthPercent(final double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        final TextField valueField = getValueField();
        valueField.prefWidthProperty().unbind();
        valueField.prefWidthProperty().bind(widthProperty().multiply(controlWidthPercent));
    }

    @Override
    @FXThread
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

        final float[] element = getPropertyValue();

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
    @FXThread
    private void updateValue(@Nullable final KeyEvent event) {
        if (isIgnoreListener() || (event != null && event.getCode() != KeyCode.ENTER)) return;

        final String textValue = getValueField().getText();
        float[] newValue = null;

        if (!StringUtils.isEmpty(textValue)) {

            final String splitter = textValue.contains(" ") ? " " : ",";
            final String[] splited = textValue.split(splitter);

            newValue = new float[splited.length];

            for (int i = 0; i < splited.length; i++) {
                try {
                    newValue[i] = Float.parseFloat(splited[i]);
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
