package com.ss.editor.ui.control.model.property;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of the {@link ModelPropertyControl} for editing the {@link Enum} values.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEnumModelPropertyControl<T, E extends Enum<E>> extends ModelPropertyControl<T, E> {

    /**
     * The list of available options of the {@link Enum} value.
     */
    private ComboBox<E> enumComboBox;

    public AbstractEnumModelPropertyControl(@NotNull final E element, @NotNull final String paramName, @NotNull final ModelChangeConsumer modelChangeConsumer, @NotNull final E[] availableValues) {
        super(element, paramName, modelChangeConsumer);
        final ObservableList<E> items = enumComboBox.getItems();
        items.addAll(availableValues);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        enumComboBox = new ComboBox<>();
        enumComboBox.setId(CSSIds.MODEL_PARAM_CONTROL_COMBO_BOX);
        enumComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateCullHint());
        enumComboBox.prefWidthProperty().bind(widthProperty().multiply(0.5));

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
