package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static java.lang.Boolean.TRUE;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
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
        checkBox.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FxControlUtils.onSelectedChange(checkBox, this::updateValue);

        FxUtils.addClass(checkBox,
                CssClasses.ABSTRACT_PARAM_CONTROL_CHECK_BOX);

        FxUtils.addChild(container, checkBox);
    }

    @Override
    @FxThread
    public void changeControlWidthPercent(double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        FxUtils.rebindPrefWidth(getCheckBox(),
                widthProperty().multiply(controlWidthPercent));
    }

    /**
     * Disable the offset of checkbox control.
     */
    @FxThread
    public void disableCheckboxOffset() {
        FxUtils.resetPrefWidth(getCheckBox());
        FxUtils.resetMinMaxWidth(getPropertyNameLabel())
            .prefWidthProperty().bind(widthProperty());
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
        getCheckBox().setSelected(TRUE.equals(getPropertyValue()));
    }

    @FxThread
    @Override
    public boolean isDirty() {
        return !Objects.equals(getPropertyValue(), getCheckBox().isSelected());
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
    protected void apply() {
        super.apply();
        changed(getCheckBox().isSelected(), getPropertyValue());
    }
}
