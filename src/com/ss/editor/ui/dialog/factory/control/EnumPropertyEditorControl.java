package com.ss.editor.ui.dialog.factory.control;

import static com.ss.rlib.util.ClassUtils.unsafeCast;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.factory.PropertyDefinition;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.VarTable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The control to edit enum values.
 *
 * @author JavaSaBr
 */
public class EnumPropertyEditorControl<T extends Enum<T>> extends PropertyEditorControl<T> {

    /**
     * The list of available options of the {@link Enum} value.
     */
    @Nullable
    private ComboBox<T> enumComboBox;

    protected EnumPropertyEditorControl(@NotNull final VarTable vars, @NotNull final PropertyDefinition definition,
                                        @NotNull final Runnable validationCallback) {
        super(vars, definition, validationCallback);

        final T defaultValue = unsafeCast(notNull(definition.getDefaultValue()));
        final T[] enumConstants = unsafeCast(defaultValue.getClass().getEnumConstants());

        final ComboBox<T> enumComboBox = getEnumComboBox();
        enumComboBox.getItems().addAll(enumConstants);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        enumComboBox = new ComboBox<>();
        enumComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> change());
        enumComboBox.prefWidthProperty().bind(widthProperty().multiply(0.5F));

        FXUtils.addClassTo(enumComboBox, CSSClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addToPane(enumComboBox, this);
    }

    /**
     * @return the list of available options of the {@link Enum} value.
     */
    @NotNull
    private ComboBox<T> getEnumComboBox() {
        return notNull(enumComboBox);
    }

    @Override
    protected void reload() {
        super.reload();
        final T value = getPropertyValue();
        final ComboBox<T> enumComboBox = getEnumComboBox();
        enumComboBox.getSelectionModel().select(value);
    }

    @Override
    protected void change() {
        final ComboBox<T> enumComboBox = getEnumComboBox();
        final SingleSelectionModel<T> selectionModel = enumComboBox.getSelectionModel();
        setPropertyValue(selectionModel.getSelectedItem());
        super.change();
    }
}
