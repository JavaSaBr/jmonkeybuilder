package com.ss.editor.ui.control.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.control.material.operation.IntegerMaterialParamOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import rlib.ui.control.input.IntegerTextField;
import rlib.ui.util.FXUtils;

/**
 * The base implementation of control for editing integer material parameter.
 *
 * @author JavaSaBr
 */
public class IntegerMaterialParamControl extends MaterialParamControl {

    public static final Insets FIELD_OFFSET = new Insets(0, 6, 0, 0);

    /**
     * The integer integerField.
     */
    private IntegerTextField integerField;

    public IntegerMaterialParamControl(final Consumer<EditorOperation> changeHandler, final Material material, final String parameterName) {
        super(changeHandler, material, parameterName);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        integerField = new IntegerTextField();
        integerField.setId(CSSIds.MATERIAL_PARAM_CONTROL_SPINNER);
        integerField.addChangeListener((observable, oldValue, newValue) -> processChange(newValue));
        integerField.prefWidthProperty().bind(widthProperty().multiply(CONTROL_PERCENT_WIDTH2));

        FXUtils.addToPane(integerField, this);
        FXUtils.addClassTo(integerField, CSSClasses.SPECIAL_FONT_13);

        HBox.setMargin(integerField, FIELD_OFFSET);
    }

    @Override
    protected double getLabelPercentWidth() {
        return LABEL_PERCENT_WIDTH2;
    }

    /**
     * Update a value.
     */
    private void processChange(@NotNull final Integer newValue) {
        if (isIgnoreListeners()) return;

        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParam param = material.getParam(parameterName);
        final Integer oldValue = param == null ? null : (Integer) param.getValue();

        execute(new IntegerMaterialParamOperation(parameterName, newValue, oldValue));
    }

    @Override
    public void reload() {
        super.reload();

        final Material material = getMaterial();
        final MatParam param = material.getParam(getParameterName());

        if (param == null) {
            integerField.setValue(0);
            return;
        }

        integerField.setValue((Integer) param.getValue());
        integerField.positionCaret(integerField.getText().length());
    }
}
