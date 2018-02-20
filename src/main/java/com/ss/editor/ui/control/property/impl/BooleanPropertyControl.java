package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.ui.util.FXUtils;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The implementation of the {@link PropertyControl} to change boolean values.
 *
 * @param <C> the type of a {@link ChangeConsumer}.
 * @param <T> the type of an editing object.
 * @author JavaSaBr
 */
public class BooleanPropertyControl<C extends ChangeConsumer, T> extends PropertyControl<C, T, Boolean> {

    /**
     * The {@link CheckBox} with current value.
     */
    @Nullable
    private CheckBox checkBox;

    public BooleanPropertyControl(@Nullable final Boolean propertyValue, @NotNull final String propertyName,
                                  @NotNull final C changeConsumer) {
        super(propertyValue, propertyName, changeConsumer);
    }

    public BooleanPropertyControl(@Nullable final Boolean propertyValue, @NotNull final String propertyName,
                                  @NotNull final C changeConsumer,
                                  @Nullable final SixObjectConsumer<C, T, String, Boolean, Boolean, BiConsumer<T, Boolean>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        checkBox = new CheckBox();
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> updateValue());
        checkBox.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addToPane(checkBox, container);
        FXUtils.addClassTo(checkBox, CssClasses.ABSTRACT_PARAM_CONTROL_CHECK_BOX);
    }

    @Override
    @FxThread
    public void changeControlWidthPercent(final double controlWidthPercent) {

        final CheckBox checkBox = getCheckBox();
        final DoubleProperty widthProperty = checkBox.prefWidthProperty();

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

        final CheckBox checkBox = getCheckBox();
        checkBox.prefWidthProperty().unbind();

        final Label propertyNameLabel = getPropertyNameLabel();
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
     * @return the {@link CheckBox} with current value.
     */
    @FxThread
    private @NotNull CheckBox getCheckBox() {
        return notNull(checkBox);
    }

    @Override
    @FxThread
    protected void reload() {
        final Boolean value = getPropertyValue();
        final CheckBox checkBox = getCheckBox();
        checkBox.setSelected(Boolean.TRUE.equals(value));
    }

    /**
     * Update the value.
     */
    @FxThread
    private void updateValue() {
        if (isIgnoreListener()) return;
        final CheckBox checkBox = getCheckBox();
        changed(checkBox.isSelected(), getPropertyValue());
    }
}
