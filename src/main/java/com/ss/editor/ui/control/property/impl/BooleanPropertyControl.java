package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CSSClasses;
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
 * @param <C> the type of a {@link ChangeConsumer}
 * @param <T> the type of an editing object
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
    @FXThread
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        checkBox = new CheckBox();
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> updateValue());
        checkBox.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addToPane(checkBox, container);
        FXUtils.addClassTo(checkBox, CSSClasses.ABSTRACT_PARAM_CONTROL_CHECK_BOX);
    }

    @Override
    @FXThread
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
    @FXThread
    public void disableCheckboxOffset() {

        final CheckBox checkBox = getCheckBox();
        checkBox.prefWidthProperty().unbind();
        checkBox.setPrefWidth(Region.USE_COMPUTED_SIZE);

        final Label propertyNameLabel = getPropertyNameLabel();
        propertyNameLabel.maxWidthProperty().unbind();
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * @return the {@link CheckBox} with current value.
     */
    @FXThread
    private @NotNull CheckBox getCheckBox() {
        return notNull(checkBox);
    }

    @Override
    @FXThread
    protected void reload() {
        final Boolean value = getPropertyValue();
        final CheckBox checkBox = getCheckBox();
        checkBox.setSelected(Boolean.TRUE.equals(value));
    }

    /**
     * Update the value.
     */
    @FXThread
    private void updateValue() {
        if (isIgnoreListener()) return;
        final CheckBox checkBox = getCheckBox();
        changed(checkBox.isSelected(), getPropertyValue());
    }
}
