package com.ss.editor.ui.control.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.control.material.operation.BooleanMaterialParamOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import javafx.scene.control.CheckBox;
import rlib.ui.util.FXUtils;

/**
 * The implementation of a control for editing boolean properties.
 *
 * @author JavaSaBr
 */
public class BooleanMaterialParamControl extends MaterialParamControl {

    /**
     * THe check box.
     */
    private CheckBox checkBox;

    public BooleanMaterialParamControl(@NotNull final Consumer<EditorOperation> changeHandler,
                                       @NotNull final Material material,
                                       @NotNull final String parameterName) {
        super(changeHandler, material, parameterName);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        checkBox = new CheckBox();
        checkBox.setId(CSSIds.MATERIAL_PARAM_CONTROL_CHECKBOX);
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));
        checkBox.prefWidthProperty().bind(widthProperty().multiply(CONTROL_PERCENT_WIDTH2));

        FXUtils.addToPane(checkBox, this);
        FXUtils.addClassTo(checkBox, CSSClasses.MAIN_FONT_13);
    }

    @Override
    protected double getLabelPercentWidth() {
        return LABEL_PERCENT_WIDTH2;
    }

    /**
     * Update a value.
     */
    private void processChange(@NotNull final Boolean newValue) {
        if (isIgnoreListeners()) return;

        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParam param = material.getParam(parameterName);
        final Boolean oldValue = param == null ? null : (Boolean) param.getValue();

        execute(new BooleanMaterialParamOperation(parameterName, newValue, oldValue));
    }

    @Override
    public void reload() {
        super.reload();

        final Material material = getMaterial();
        final MatParam param = material.getParam(getParameterName());

        if (param == null) {
            checkBox.setSelected(false);
            return;
        }

        final Boolean value = (Boolean) param.getValue();

        checkBox.setSelected(value);
    }
}
