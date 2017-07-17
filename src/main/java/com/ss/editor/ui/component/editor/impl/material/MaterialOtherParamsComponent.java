package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.shader.VarType;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.control.material.BooleanMaterialParamControl;
import com.ss.editor.ui.control.material.FloatMaterialParamControl;
import com.ss.editor.ui.control.material.IntegerMaterialParamControl;
import com.ss.editor.ui.control.material.MaterialParamControl;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import com.ss.rlib.ui.util.FXUtils;

/**
 * The component for editing material other properties.
 *
 * @author JavaSaBr
 */
public class MaterialOtherParamsComponent extends AbstractMaterialPropertiesComponent {

    /**
     * Instantiates a new Material other params component.
     *
     * @param changeHandler the change handler
     */
    public MaterialOtherParamsComponent(@NotNull final Consumer<EditorOperation> changeHandler) {
        super(changeHandler);
    }

    @Override
    protected void buildFor(@NotNull final MatParam matParam, @NotNull final Material material) {

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        final VarType varType = matParam.getVarType();

        MaterialParamControl control = null;

        if (varType == VarType.Boolean) {
            control = new BooleanMaterialParamControl(changeHandler, material, matParam.getName());
        } else if (varType == VarType.Int) {
            control = new IntegerMaterialParamControl(changeHandler, material, matParam.getName());
        } else if (varType == VarType.Float) {
            control = new FloatMaterialParamControl(changeHandler, material, matParam.getName());
        }

        if (control == null) return;

        FXUtils.addToPane(control, this);
    }
}
