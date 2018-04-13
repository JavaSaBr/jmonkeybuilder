package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * The implementation of the {@link PropertyControl} to change boolean values.
 *
 * @param <C> the type of a change consumer.
 * @param <D> the type of an editing object.
 * @author JavaSaBr
 */
public class BooleanPropertyControl<C extends ChangeConsumer, D> extends PropertyControl<C, D, Boolean> {

    /**
     * The field with current value.
     */
    @Nullable
    private CheckBox checkBox;

    public BooleanPropertyControl(
            @Nullable Boolean propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        super(propertyValue, propertyName, changeConsumer);
    }

    public BooleanPropertyControl(
            @Nullable Boolean propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @Nullable ChangeHandler<C, D, Boolean> changeHandler
    ) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull HBox container) {
        super.createComponents(container);

        checkBox = new CheckBox();
        checkBox.selectedProperty().addListener(this::updateValue);
        checkBox.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addToPane(checkBox, container);
        FXUtils.addClassTo(checkBox, CssClasses.ABSTRACT_PARAM_CONTROL_CHECK_BOX);
    }

    @Override
    @FxThread
    public void changeControlWidthPercent(double controlWidthPercent) {

        var checkBox = getCheckBox();
        var widthProperty = checkBox.prefWidthProperty();

        if (widthProperty.isBound()) {
            super.changeControlWidthPercent(controlWidthPercent);
            widthProperty.unbind();
            widthProperty.bind(widthProperty().multiply(controlWidthPercent));
        }
    }

    /**
     * Disable the offset of checkbox control.
     */
    @FxThread
    public void disableCheckboxOffset() {

        var checkBox = getCheckBox();
        checkBox.prefWidthProperty().unbind();

        var propertyNameLabel = getPropertyNameLabel();
        propertyNameLabel.maxWidthProperty().unbind();
        propertyNameLabel.setMaxWidth(Region.USE_COMPUTED_SIZE);
        propertyNameLabel.prefWidthProperty().bind(widthProperty());
        propertyNameLabel.minWidthProperty().unbind();
        propertyNameLabel.setMinWidth(Region.USE_COMPUTED_SIZE);
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * Get the field with current value.
     *
     * @return the field with current value.
     */
    @FxThread
    private @NotNull CheckBox getCheckBox() {
        return notNull(checkBox);
    }

    @Override
    @FxThread
    protected void reload() {
        var value = getPropertyValue();
        var checkBox = getCheckBox();
        checkBox.setSelected(Boolean.TRUE.equals(value));
    }

    @FxThread
    @Override
    public boolean isDirty() {
        var currentValue = getCheckBox().isSelected();
        var storedValue = getPropertyValue();
        return !Objects.equals(storedValue, currentValue);
    }

    /**
     * Update the value.
     */
    @FxThread
    private void updateValue(
            @NotNull ObservableValue<? extends Boolean> observable,
            @NotNull Boolean oldValue,
            @NotNull Boolean newValue
    ) {
        if (!isIgnoreListener()) {
            apply();
        }
    }

    @Override
    protected void apply() {
        super.apply();
        var checkBox = getCheckBox();
        changed(checkBox.isSelected(), getPropertyValue());
    }
}
