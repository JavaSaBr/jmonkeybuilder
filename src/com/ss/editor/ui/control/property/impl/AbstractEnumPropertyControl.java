package com.ss.editor.ui.control.property.impl;

import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.HBox;
import rlib.function.SixObjectConsumer;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit {@link Enum} values.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEnumPropertyControl<C extends ChangeConsumer, T, E extends Enum<?>>
        extends AbstractPropertyControl<C, T, E> {

    /**
     * The list of available options of the {@link Enum} value.
     */
    private ComboBox<E> enumComboBox;

    public AbstractEnumPropertyControl(@Nullable final E propertyValue, @NotNull final String propertyName,
                                       @NotNull final C changeConsumer, @NotNull final E[] availableValues,
                                       @NotNull final SixObjectConsumer<C, T, String, E, E, BiConsumer<T, E>> changeHandler) {
        super(propertyValue, propertyName, changeConsumer, changeHandler);
        final ObservableList<E> items = enumComboBox.getItems();
        items.addAll(availableValues);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        enumComboBox = new ComboBox<>();
        enumComboBox.setId(CSSIds.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        enumComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateCullHint());
        enumComboBox.prefWidthProperty().bind(widthProperty().multiply(CONTROL_WIDTH_PERCENT));

        FXUtils.addToPane(enumComboBox, container);
        FXUtils.addClassTo(enumComboBox, CSSClasses.SPECIAL_FONT_13);
    }

    /**
     * @return the list of available options of the {@link Enum} value.
     */
    private ComboBox<E> getEnumComboBox() {
        return enumComboBox;
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
