package com.ss.editor.ui.control.property.impl;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.rlib.common.util.ArrayUtils;
import com.ss.rlib.common.util.StringUtils;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * The implementation of the {@link PropertyControl} to edit int array values.
 *
 * @param <C> the change consumer's type.
 * @param <D> the type of an editing object.
 * @author JavaSaBr
 */
public class IntArrayPropertyControl<C extends ChangeConsumer, D> extends StringBasedArrayPropertyControl<C, D, int[]> {

    public IntArrayPropertyControl(
            @Nullable int[] propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
    }

    public IntArrayPropertyControl(
            @Nullable int[] propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @Nullable ChangeHandler<C, D, int[]> changeHandler
    ) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    @FxThread
    protected @NotNull String getTextPresentation() {
        return getPropertyValueOpt()
                .map(ints -> ArrayUtils.toString(ints, " ", false, false))
                .orElse(StringUtils.EMPTY);
    }

    @Override
    @FxThread
    public boolean isDirty() {
        return !Arrays.equals(getCurrentValue(), getPropertyValue());
    }

    @Override
    @FxThread
    protected @Nullable int[] getCurrentValue() {

        var textValue = valueField.getText();

        int[] newValue = null;

        if (!StringUtils.isEmpty(textValue)) {

            var splitter = textValue.contains(" ") ? " " : ",";
            var split = textValue.split(splitter);

            newValue = new int[split.length];

            for (var i = 0; i < split.length; i++) {
                try {
                    newValue[i] = Integer.parseInt(split[i]);
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
