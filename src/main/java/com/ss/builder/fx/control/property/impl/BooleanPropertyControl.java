package com.ss.builder.fx.control.property.impl;

import static java.lang.Boolean.TRUE;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.control.property.PropertyControl;
import com.ss.builder.fx.css.CssClasses;
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
    @NotNull
    private final CheckBox checkBox;

    public BooleanPropertyControl(
            @Nullable Boolean propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        this(propertyValue, propertyName, changeConsumer, null);
    }

    public BooleanPropertyControl(
            @Nullable Boolean propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @Nullable ChangeHandler<C, D, Boolean> changeHandler
    ) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
        this.checkBox = new CheckBox();
    }

    @Override
    @FxThread
    protected void createControls(@NotNull HBox container) {
        super.createControls(container);

        checkBox.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FxControlUtils.onSelectedChange(checkBox, this::updateValue);

        FxUtils.addClass(checkBox,
                CssClasses.PROPERTY_CONTROL_CHECK_BOX);

        FxUtils.addChild(container, checkBox);
    }

    @Override
    @FxThread
    public void changeControlWidthPercent(double controlWidthPercent) {
        super.changeControlWidthPercent(controlWidthPercent);

        FxUtils.rebindPrefWidth(checkBox,
                widthProperty().multiply(controlWidthPercent));
    }

    /**
     * Disable the offset of checkbox control.
     */
    @FxThread
    public void disableCheckboxOffset() {
        FxUtils.resetPrefWidth(checkBox);
        FxUtils.resetMinMaxWidth(propertyNameLabel)
                .prefWidthProperty().bind(widthProperty());
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    @Override
    @FxThread
    protected void reloadImpl() {
        checkBox.setSelected(TRUE.equals(getPropertyValue()));
        super.reloadImpl();
    }

    @FxThread
    @Override
    public boolean isDirty() {
        return !Objects.equals(getPropertyValue(), checkBox.isSelected());
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
        changed(checkBox.isSelected(), getPropertyValue());
    }
}
