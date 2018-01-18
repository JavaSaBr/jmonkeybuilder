package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.util.ClassUtils.unsafeCast;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
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
    @FxThread
    protected void createComponents() {
        super.createComponents();

        enumComboBox = new ComboBox<>();
        enumComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> change());
        enumComboBox.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));
        enumComboBox.setVisibleRowCount(20);

        FXUtils.addClassTo(enumComboBox, CssClasses.ABSTRACT_PARAM_CONTROL_COMBO_BOX);
        FXUtils.addToPane(enumComboBox, this);
    }

    /**
     * @return the list of available options of the {@link Enum} value.
     */
    @FxThread
    private @NotNull ComboBox<T> getEnumComboBox() {
        return notNull(enumComboBox);
    }

    @Override
    @FxThread
    protected void reload() {
        super.reload();
        final T value = getPropertyValue();
        final ComboBox<T> enumComboBox = getEnumComboBox();
        enumComboBox.getSelectionModel().select(value);
    }

    @Override
    @FxThread
    protected void changeImpl() {
        final ComboBox<T> enumComboBox = getEnumComboBox();
        final SingleSelectionModel<T> selectionModel = enumComboBox.getSelectionModel();
        setPropertyValue(selectionModel.getSelectedItem());
        super.changeImpl();
    }
}
