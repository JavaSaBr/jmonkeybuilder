package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static com.ss.rlib.common.util.StringUtils.emptyIfNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import com.ss.rlib.common.util.StringUtils;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit string values.
 *
 * @param <C> the type of a change consumer.
 * @param <D> the type of an editing object.
 * @author JavaSaBr
 */
public class StringPropertyControl<C extends ChangeConsumer, D> extends PropertyControl<C, D, String> {

    /**
     * The filed with current value.
     */
    @NotNull
    private final TextField valueField;

    public StringPropertyControl(
            @Nullable String propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
        this.valueField = new TextField();
    }

    @Override
    @FxThread
    protected void createControls(@NotNull HBox container) {
        super.createControls(container);

        valueField.setOnKeyReleased(this::updateValue);
        valueField.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FxControlUtils.onFocusChange(valueField, this::applyOnLostFocus);

        FxUtils.addClass(valueField, CssClasses.PROPERTY_CONTROL_COMBO_BOX);
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

        valueField.setText(emptyIfNull(getPropertyValue()));
        valueField.positionCaret(caretPosition);

        super.reloadImpl();
    }

    /**
     * Update the value.
     */
    @FxThread
    private void updateValue(@NotNull KeyEvent event) {
        if (!isIgnoreListener() && event.getCode() == KeyCode.ENTER) {
            apply();
        }
    }

    @FxThread
    @Override
    public boolean isDirty() {
        return !StringUtils.equals(getPropertyValue(), valueField.getText());
    }

    @Override
    @FxThread
    protected void apply() {
        super.apply();
        changed(valueField.getText(), getPropertyValue());
    }
}
