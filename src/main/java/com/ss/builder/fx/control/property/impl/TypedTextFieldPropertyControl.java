package com.ss.builder.ui.control.property.impl;

import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.ui.control.property.PropertyControl;
import com.ss.builder.ui.css.CssClasses;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.control.input.TypedTextField;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The typed text field based implementation of the {@link PropertyControl}.
 *
 * @param <C> the change consumer's type.
 * @param <D> the editing object's type.
 * @param <T> the value's type.
 * @param <F> the field's type.
 * @author JavaSaBr
 */
public class TypedTextFieldPropertyControl<C extends ChangeConsumer, D, T, F extends TypedTextField<T>> extends PropertyControl<C, D, T> {

    /**
     * The filed with current value.
     */
    @NotNull
    protected final F valueField;

    public TypedTextFieldPropertyControl(
            @Nullable T propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        this(propertyValue, propertyName, changeConsumer, null);
    }

    public TypedTextFieldPropertyControl(
            @Nullable T propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @Nullable ChangeHandler<C, D, T> changeHandler
    ) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
        this.valueField = createFieldControl();
    }

    /**
     * Create a text field control.
     *
     * @return the text field control.
     */
    @FromAnyThread
    protected  @NotNull F createFieldControl() {
        throw new UnsupportedOperationException();
    }

    @Override
    @FxThread
    public void changeControlWidthPercent(double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        FxUtils.rebindPrefWidth(valueField,
                widthProperty().multiply(controlWidthPercent));
    }

    @Override
    @FxThread
    protected void createControls(@NotNull HBox container) {
        super.createControls(container);

        valueField.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FxControlUtils.onValueChange(valueField, this::updateValue);
        FxControlUtils.onFocusChange(valueField, this::applyOnLostFocus);

        FxUtils.addClass(valueField,
                CssClasses.PROPERTY_CONTROL_COMBO_BOX);

        FxUtils.addChild(container, valueField);
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    @Override
    @FxThread
    protected void reloadImpl() {

        var caretPosition = valueField.getCaretPosition();

        valueField.setValue(getPropertyValue());
        valueField.positionCaret(caretPosition);

        super.reloadImpl();
    }

    @Override
    @FxThread
    public boolean isDirty() {
        return !Objects.equals(valueField.getValue(), getPropertyValue());
    }

    /**
     * Update the value.
     */
    @FxThread
    private void updateValue() {
        if (!isIgnoreListener()) {
            apply();
        }
    }

    @Override
    @FxThread
    protected void apply() {
        super.apply();
        changed(valueField.getValue(), getPropertyValue());
    }
}
