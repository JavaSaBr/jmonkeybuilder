package com.ss.editor.ui.control.property.impl;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.rlib.common.util.ArrayUtils;
import com.ss.rlib.common.util.StringUtils;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * The implementation of the {@link PropertyControl} to edit float array values.
 *
 * @param <C> the change consumer's type.
 * @param <D> the type of an editing object.
 * @author JavaSaBr
 */
public class FloatArrayPropertyControl<C extends ChangeConsumer, D>
        extends StringBasedArrayPropertyControl<C, D, float[]> {

    public FloatArrayPropertyControl(
            @Nullable float[] propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
    }

    public FloatArrayPropertyControl(
            @Nullable float[] propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @Nullable ChangeHandler<C, D, float[]> changeHandler
    ) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    @FxThread
    protected void reload() {

        var element = getPropertyValue();
        var valueField = getValueField();
        var caretPosition = valueField.getCaretPosition();

        if (element == null) {
            valueField.setText(StringUtils.EMPTY);
        } else {
            valueField.setText(ArrayUtils.toString(element, " ", false, false));
        }

        valueField.positionCaret(caretPosition);
    }

    @Override
    @FxThread
    public boolean isDirty() {
        return !Arrays.equals(getCurrentValue(), getPropertyValue());
    }

    @Override
    @FxThread
    protected void updateValue(@Nullable KeyEvent event) {
        if (!isIgnoreListener() && (event == null || event.getCode() == KeyCode.ENTER)) {
            apply();
        }
    }

    @Override
    @FxThread
    protected void apply() {
        super.apply();
        changed(getCurrentValue(), getPropertyValue());
    }

    private @Nullable float[] getCurrentValue() {

        var textValue = getValueField().getText();

        float[] newValue = null;

        if (!StringUtils.isEmpty(textValue)) {

            var splitter = textValue.contains(" ") ? " " : ",";
            var split = textValue.split(splitter);

            newValue = new float[split.length];

            for (var i = 0; i < split.length; i++) {
                try {
                    newValue[i] = Float.parseFloat(split[i]);
                } catch (NumberFormatException e) {
                    LOGGER.warning(this, e);
                    newValue = getPropertyValue();
                    break;
                }
            }
        }

        return newValue;
    }
}
