package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.shader.VarType;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.material.BooleanMaterialParamControl;
import com.ss.editor.ui.control.material.FloatMaterialParamControl;
import com.ss.editor.ui.control.material.IntegerMaterialParamControl;
import com.ss.editor.ui.control.material.MaterialParamControl;
import com.ss.rlib.ui.util.FXUtils;
import org.jetbrains.annotations.NotNull;

/**
 * The component for editing material other properties.
 *
 * @author JavaSaBr
 */
public class MaterialOtherParamsComponent extends AbstractMaterialPropertiesComponent {

    /**
     * Instantiates a new Material other params component.
     *
     * @param changeConsumer the change consumer
     */
    public MaterialOtherParamsComponent(@NotNull final ChangeConsumer changeConsumer) {
        super(changeConsumer);
    }

    @Override
    protected void buildFor(@NotNull final MatParam matParam, @NotNull final Material material) {

        final @NotNull ChangeConsumer changeConsumer = getChangeConsumer();
        final VarType varType = matParam.getVarType();

        MaterialParamControl control = null;

        if (varType == VarType.Boolean) {
            control = new BooleanMaterialParamControl(changeConsumer, material, matParam.getName());
        } else if (varType == VarType.Int) {
            control = new IntegerMaterialParamControl(changeConsumer, material, matParam.getName());
        } else if (varType == VarType.Float) {
            control = new FloatMaterialParamControl(changeConsumer, material, matParam.getName());
        }

        if (control == null) return;

        FXUtils.addToPane(control, this);
    }
}
