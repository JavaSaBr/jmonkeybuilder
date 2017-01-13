package com.ss.editor.ui.control.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.control.material.operation.FloatMaterialParamOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import rlib.ui.control.input.FloatTextField;
import rlib.ui.util.FXUtils;

/**
 * The base implementation of control for editing float material parameter.
 *
 * @author JavaSaBr
 */
public class FloatMaterialParamControl extends MaterialParamControl {

    public static final Insets FIELD_OFFSET = new Insets(0, 6, 0, 0);

    /**
     * The float field.
     */
    private FloatTextField floatField;

    public FloatMaterialParamControl(@NotNull final Consumer<EditorOperation> changeHandler,
                                     @NotNull final Material material,
                                     @NotNull final String parameterName) {
        super(changeHandler, material, parameterName);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        floatField = new FloatTextField();
        floatField.setId(CSSIds.MATERIAL_PARAM_CONTROL_SPINNER);
        floatField.addChangeListener((observable, oldValue, newValue) -> processChange(newValue));
        floatField.prefWidthProperty().bind(widthProperty().multiply(CONTROL_PERCENT_WIDTH2));
        floatField.setScrollPower(5F);

        FXUtils.addToPane(floatField, this);
        FXUtils.addClassTo(floatField, CSSClasses.SPECIAL_FONT_13);

        HBox.setMargin(floatField, FIELD_OFFSET);
    }

    @Override
    protected double getLabelPercentWidth() {
        return LABEL_PERCENT_WIDTH2;
    }

    /**
     * Update a value.
     */
    private void processChange(@Nullable final Float newValue) {
        if (isIgnoreListeners()) return;

        final Float newFValue = newValue == null ? null : newValue;
        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParam param = material.getParam(parameterName);
        final Float oldValue = param == null ? null : (Float) param.getValue();

        execute(new FloatMaterialParamOperation(parameterName, newFValue, oldValue));
    }

    @Override
    public void reload() {
        super.reload();

        final Material material = getMaterial();
        final MatParam param = material.getParam(getParameterName());

        if (param == null) {
            floatField.setValue(0F);
            return;
        }

        floatField.setValue((Float) param.getValue());
        floatField.positionCaret(floatField.getText().length());
    }
}
