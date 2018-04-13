package com.ss.editor.ui.control.property.impl;

import static com.ss.editor.util.EditorUtil.getAvailableValues;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.ui.util.FxUtils;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link PropertyControl} to edit {@link Enum} values.
 *
 * @param <C> the type of a {@link ChangeConsumer}
 * @param <D> the type of an editing object.
 * @param <E> the type of editing enum.
 * @author JavaSaBr
 */
public class EnumPropertyControl<C extends ChangeConsumer, D, E extends Enum<?>> extends PropertyControl<C, D, E> {

    /**
     * The list of available options of the {@link Enum} value.
     */
    @Nullable
    private ComboBox<E> enumComboBox;

    public EnumPropertyControl(
            @Nullable E propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @NotNull E[] availableValues
    ) {
        super(propertyValue, propertyName, changeConsumer);
        getEnumComboBox().getItems()
                .addAll(availableValues);
    }

    public EnumPropertyControl(
            @NotNull E propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer
    ) {
        this(propertyValue, propertyName, changeConsumer, getAvailableValues(propertyValue));
    }

    public EnumPropertyControl(
            @Nullable E propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @NotNull E[] availableValues,
            @Nullable ChangeHandler<C, D, E> changeHandler
    ) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
        getEnumComboBox().getItems()
                .addAll(availableValues);
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull HBox container) {
        super.createComponents(container);

        enumComboBox = new ComboBox<>();
        enumComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> change());
        enumComboBox.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FxUtils.addChild(container, enumComboBox);
        FxUtils.addClass(enumComboBox, CssClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
    }

    /**
     * Get the list of available options of the {@link Enum} value.
     *
     * @return the list of available options of the {@link Enum} value.
     */
    @FxThread
    private @NotNull ComboBox<E> getEnumComboBox() {
        return notNull(enumComboBox);
    }

    /**
     * Update selected {@link Enum} value.
     */
    @FxThread
    private void change() {

        if (isIgnoreListener()) {
            return;
        }

        var enumComboBox = getEnumComboBox();
        var selectionModel = enumComboBox.getSelectionModel();
        var newValue = selectionModel.getSelectedItem();

        changed(newValue, getPropertyValue());
    }

    @Override
    @FxThread
    protected void reload() {
        getEnumComboBox().getSelectionModel()
                .select(getPropertyValue());
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }
}
