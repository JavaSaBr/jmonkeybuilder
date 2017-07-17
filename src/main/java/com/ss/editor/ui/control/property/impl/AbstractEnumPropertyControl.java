package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
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
 * The implementation of the {@link AbstractPropertyControl} to edit {@link Enum} values.
 *
 * @param <C> the type of a {@link ChangeConsumer}
 * @param <T> the type of an editing object.
 * @param <E> the type of editing enum.
 * @author JavaSaBr
 */
public abstract class AbstractEnumPropertyControl<C extends ChangeConsumer, T, E extends Enum<?>> extends
        AbstractPropertyControl<C, T, E> {

    /**
     * The list of available options of the {@link Enum} value.
     */
    @Nullable
    private ComboBox<E> enumComboBox;

    /**
     * Instantiates a new Abstract enum property control.
     *
     * @param propertyValue   the property value
     * @param propertyName    the property name
     * @param changeConsumer  the change consumer
     * @param availableValues the available values
     * @param changeHandler   the change handler
     */
    public AbstractEnumPropertyControl(@Nullable final E propertyValue, @NotNull final String propertyName,
                                       @NotNull final C changeConsumer, @NotNull final E[] availableValues,
                                       @NotNull final SixObjectConsumer<C, T, String, E, E, BiConsumer<T, E>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
        final ObservableList<E> items = getEnumComboBox().getItems();
        items.addAll(availableValues);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        enumComboBox = new ComboBox<>();
        enumComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> updateCullHint());
        enumComboBox.prefWidthProperty()
                .bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addToPane(enumComboBox, container);
        FXUtils.addClassTo(enumComboBox, CSSClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
    }

    /**
     * @return the list of available options of the {@link Enum} value.
     */
    @NotNull
    private ComboBox<E> getEnumComboBox() {
        return notNull(enumComboBox);
    }

    /**
     * Update selected {@link Enum} value.
     */
    private void updateCullHint() {
        if (isIgnoreListener()) return;

        final ComboBox<E> enumComboBox = getEnumComboBox();
        final SingleSelectionModel<E> selectionModel = enumComboBox.getSelectionModel();
        final E newValue = selectionModel.getSelectedItem();

        changed(newValue, getPropertyValue());
    }

    @Override
    protected void reload() {

        final E element = getPropertyValue();

        final ComboBox<E> enumComboBox = getEnumComboBox();
        final SingleSelectionModel<E> selectionModel = enumComboBox.getSelectionModel();
        selectionModel.select(element);
    }

    @Override
    protected boolean isSingleRow() {
        return true;
    }
}
