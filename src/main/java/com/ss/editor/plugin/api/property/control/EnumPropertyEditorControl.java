package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.ComboBox;
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

    protected EnumPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);

        var defaultValue = ClassUtils.<T>unsafeCast(notNull(definition.getDefaultValue()));
        var enumConstants = EditorUtil.<T>getEnumValues(defaultValue.getClass());

        getEnumComboBox().getItems()
                .addAll(enumConstants);
    }

    @Override
    @FxThread
    protected void createComponents() {
        super.createComponents();

        enumComboBox = new ComboBox<>();
        enumComboBox.prefWidthProperty()
                .bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        enumComboBox.setVisibleRowCount(20);

        FxControlUtils.onSelectedItemChange(enumComboBox, this::change);

        FxUtils.addClass(enumComboBox, CssClasses.PROPERTY_CONTROL_COMBO_BOX);
        FxUtils.addChild(this, enumComboBox);
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
    public void reload() {
        super.reload();
        getEnumComboBox().getSelectionModel()
                .select(getPropertyValue());
    }

    @Override
    @FxThread
    protected void changeImpl() {

        var currentValue = getEnumComboBox()
                .getSelectionModel()
                .getSelectedItem();

        setPropertyValue(currentValue);

        super.changeImpl();
    }
}
