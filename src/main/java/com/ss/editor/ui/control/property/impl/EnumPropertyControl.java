package com.ss.editor.ui.control.property.impl;

import static com.ss.editor.util.EditorUtil.getAvailableValues;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.function.SixObjectConsumer;
import com.ss.rlib.ui.util.FXUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The implementation of the {@link PropertyControl} to edit {@link Enum} values.
 *
 * @param <C> the type of a {@link ChangeConsumer}
 * @param <T> the type of an editing object.
 * @param <E> the type of editing enum.
 * @author JavaSaBr
 */
public class EnumPropertyControl<C extends ChangeConsumer, T, E extends Enum<?>> extends PropertyControl<C, T, E> {

    /**
     * The list of available options of the {@link Enum} value.
     */
    @Nullable
    private ComboBox<E> enumComboBox;

    public EnumPropertyControl(@Nullable final E propertyValue, @NotNull final String propertyName,
                               @NotNull final C changeConsumer, @NotNull final E[] availableValues) {
        super(propertyValue, propertyName, changeConsumer);
        final ObservableList<E> items = getEnumComboBox().getItems();
        items.addAll(availableValues);
    }

    public EnumPropertyControl(@NotNull final E propertyValue, @NotNull final String propertyName,
                               @NotNull final C changeConsumer) {
        this(propertyValue, propertyName, changeConsumer, getAvailableValues(propertyValue));
    }

    public EnumPropertyControl(@Nullable final E propertyValue, @NotNull final String propertyName,
                               @NotNull final C changeConsumer, @NotNull final E[] availableValues,
                               @Nullable final SixObjectConsumer<C, T, String, E, E, BiConsumer<T, E>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
        final ObservableList<E> items = getEnumComboBox().getItems();
        items.addAll(availableValues);
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        enumComboBox = new ComboBox<>();
        enumComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> change());
        enumComboBox.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addToPane(enumComboBox, container);
        FXUtils.addClassTo(enumComboBox, CssClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
    }

    /**
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
        if (isIgnoreListener()) return;

        final ComboBox<E> enumComboBox = getEnumComboBox();
        final SingleSelectionModel<E> selectionModel = enumComboBox.getSelectionModel();
        final E newValue = selectionModel.getSelectedItem();

        changed(newValue, getPropertyValue());
    }

    @Override
    @FxThread
    protected void reload() {

        final E element = getPropertyValue();

        final ComboBox<E> enumComboBox = getEnumComboBox();
        final SingleSelectionModel<E> selectionModel = enumComboBox.getSelectionModel();
        selectionModel.select(element);
    }

    @Override
    @FromAnyThread
    protected boolean isSingleRow() {
        return true;
    }
}
