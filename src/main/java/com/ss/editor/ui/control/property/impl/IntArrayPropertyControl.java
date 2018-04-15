package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FxUtils;
import com.ss.rlib.common.util.ArrayUtils;
import com.ss.rlib.common.util.StringUtils;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit int array values.
 *
 * @param <C> the change consumer's type.
 * @param <D> the type of an editing object.
 * @author JavaSaBr
 */
public class IntArrayPropertyControl<C extends ChangeConsumer, D> extends PropertyControl<C, D, int[]> {

    /**
     * The filed with current value.
     */
    @Nullable
    private TextField valueField;

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
    public void changeControlWidthPercent(double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        var valueField = getValueField();
        valueField.prefWidthProperty().unbind();
        valueField.prefWidthProperty().bind(widthProperty().multiply(controlWidthPercent));
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull HBox container) {
        super.createComponents(container);

        valueField = new TextField();
        valueField.setOnKeyReleased(this::updateValue);
        valueField.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FxUtils.addClass(valueField, CssClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FxUtils.addChild(container, valueField);
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * Get the filed with current value.
     *
     * @return the filed with current value.
     */
    @FxThread
    private @NotNull TextField getValueField() {
        return notNull(valueField);
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
        return super.isDirty();
    }

    /**
     * Update the value.
     */
    @FxThread
    private void updateValue(@Nullable KeyEvent event) {
        if (!isIgnoreListener() && (event == null || event.getCode() == KeyCode.ENTER)) {
            apply();
        }
    }

    @Override
    @FxThread
    protected void apply() {
        super.apply();

        var textValue = getValueField().getText();

        int[] newValue = null;

        if (!StringUtils.isEmpty(textValue)) {

            var splitter = textValue.contains(" ") ? " " : ",";
            var split = textValue.split(splitter);

            newValue = new int[split.length];

            for (int i = 0; i < split.length; i++) {
                try {
                    newValue[i] = Integer.parseInt(split[i]);
                } catch (NumberFormatException e) {
                    LOGGER.warning(this, e);
                    newValue = getPropertyValue();
                    break;
                }
            }
        }

        changed(newValue, getPropertyValue());
    }
}
