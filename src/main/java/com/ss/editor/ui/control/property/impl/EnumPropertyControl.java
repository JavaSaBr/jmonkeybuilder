package com.ss.editor.ui.control.property.impl;

import static com.ss.editor.util.EditorUtil.getAvailableValues;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
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
    @NotNull
    private final ComboBox<E> enumComboBox;

    public EnumPropertyControl(
            @Nullable E propertyValue,
            @NotNull String propertyName,
            @NotNull C changeConsumer,
            @NotNull E[] availableValues
    ) {
        super(propertyValue, propertyName, changeConsumer);
        this.enumComboBox = new ComboBox<>();
        this.enumComboBox.getItems()
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
        this.enumComboBox = new ComboBox<>();
        this.enumComboBox.getItems()
                .addAll(availableValues);
    }

    @Override
    @FxThread
    protected void createControls(@NotNull HBox container) {
        super.createControls(container);

        enumComboBox.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FxControlUtils.onSelectedItemChange(enumComboBox, this::change);

        FxUtils.addClass(enumComboBox,
                CssClasses.PROPERTY_CONTROL_COMBO_BOX);

        FxUtils.addChild(container, enumComboBox);
    }

    /**
     * Update selected {@link Enum} value.
     */
    @FxThread
    private void change() {
        if (!isIgnoreListener()) {
            changed(enumComboBox.getValue(), getPropertyValue());
        }
    }

    @Override
    @FxThread
    protected void reloadImpl() {

        enumComboBox.getSelectionModel()
                .select(getPropertyValue());

        super.reloadImpl();
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }
}
