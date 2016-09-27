package com.ss.editor.ui.control.model.property;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link ModelPropertyControl} for changing boolean values.
 *
 * @author JavaSaBr
 */
public abstract class AbstractBooleanModelPropertyControl<T> extends ModelPropertyControl<T, Boolean> {

    public static final Insets CHECK_BOX_OFFSET = new Insets(0, 0, 0, 120);

    /**
     * The {@link CheckBox} with current value.
     */
    private CheckBox checkBox;

    public AbstractBooleanModelPropertyControl(@NotNull final Boolean element, @NotNull final String paramName, @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        checkBox = new CheckBox();
        checkBox.setId(CSSIds.MODEL_PARAM_CONTROL_CECHK_BOX);
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> updateValue());

        HBox.setMargin(checkBox, CHECK_BOX_OFFSET);

        FXUtils.addToPane(checkBox, container);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }

    /**
     * @return the {@link CheckBox} with current value.
     */
    private CheckBox getCheckBox() {
        return checkBox;
    }

    @Override
    protected void reload() {
        final Boolean value = getPropertyValue();
        final CheckBox checkBox = getCheckBox();
        checkBox.setSelected(value == Boolean.TRUE);
    }

    /**
     * Update the value.
     */
    private void updateValue() {
        if (isIgnoreListener()) return;
        final CheckBox checkBox = getCheckBox();
        changed(checkBox.isSelected(), getPropertyValue());
    }
}
