package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.common.util.ArrayUtils;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit array values.
 *
 * @param <C> the change consumer's type.
 * @param <D> the type of an editing object.
 * @param <T> the type of an editing property.
 * @author JavaSaBr
 */
public class StringBasedArrayPropertyControl<C extends ChangeConsumer, D, T> extends PropertyControl<C, D, T> {

    /**
     * The filed with current value.
     */
    @NotNull
    protected final TextField valueField;

    public StringBasedArrayPropertyControl(
            @Nullable T propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
        this.valueField = new TextField();
    }

    public StringBasedArrayPropertyControl(
            @Nullable T propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @Nullable ChangeHandler<C, D, T> changeHandler
    ) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
        this.valueField = new TextField();
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

        valueField.setOnKeyReleased(this::updateValue);
        valueField.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FxUtils.addClass(valueField, CssClasses.PROPERTY_CONTROL_COMBO_BOX);
        FxUtils.addChild(container, valueField);
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * Update the value.
     */
    @FxThread
    protected void updateValue(@Nullable KeyEvent event) {
        if (!isIgnoreListener() && (event == null || event.getCode() == KeyCode.ENTER)) {
            apply();
        }
    }

    @Override
    @FxThread
    protected void reloadImpl() {

        var caretPosition = valueField.getCaretPosition();
        valueField.setText(getTextPresentation());
        valueField.positionCaret(caretPosition);

        super.reloadImpl();
    }

    @FxThread
    protected @NotNull String getTextPresentation() {
        throw new UnsupportedOperationException();
    }

    @Override
    @FxThread
    protected void apply() {
        super.apply();
        changed(getCurrentValue(), getPropertyValue());
    }

    @FxThread
    protected @Nullable T getCurrentValue() {
        throw new UnsupportedOperationException();
    }
}
