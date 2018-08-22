package com.ss.builder.plugin.api.property.control;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.fx.css.CssClasses;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.ComboBox;
import org.jetbrains.annotations.NotNull;

/**
 * The combo box based property control.
 *
 * @author JavaSaBr
 */
public class ComboBoxPropertyEditorControl<T> extends PropertyEditorControl<T> {

    /**
     * The list of available options.
     */
    @NotNull
    protected final ComboBox<T> comboBox;

    protected ComboBoxPropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
        this.comboBox = new ComboBox<>();
    }

    @Override
    @FxThread
    public void postConstruct() {
        super.postConstruct();

        comboBox.setVisibleRowCount(20);
        comboBox.prefWidthProperty()
                .bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        FxControlUtils.onSelectedItemChange(comboBox, this::changed);

        FxUtils.addClass(comboBox,
                CssClasses.PROPERTY_CONTROL_COMBO_BOX);

        FxUtils.addChild(this, comboBox);
    }

    @Override
    @FxThread
    protected void reloadImpl() {

        comboBox.getSelectionModel()
            .select(getPropertyValue());

        super.reloadImpl();
    }

    @Override
    @FxThread
    protected void changedImpl() {

        setPropertyValue(comboBox.getSelectionModel()
                .getSelectedItem());

        super.changedImpl();
    }
}
