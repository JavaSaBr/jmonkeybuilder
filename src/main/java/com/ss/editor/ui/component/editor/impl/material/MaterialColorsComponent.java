package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.shader.VarType;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.material.ColorMaterialParamControl;
import com.ss.rlib.ui.util.FXUtils;
import org.jetbrains.annotations.NotNull;

/**
 * The component for editing material color properties.
 *
 * @author JavaSaBr
 */
public class MaterialColorsComponent extends AbstractMaterialPropertiesComponent {

    /**
     * Instantiates a new Material colors component.
     *
     * @param changeConsumer the change consumer
     */
    public MaterialColorsComponent(@NotNull final ChangeConsumer changeConsumer) {
        super(changeConsumer);
    }

    @Override
    protected void buildFor(@NotNull final MatParam matParam, @NotNull final Material material) {

        final VarType varType = matParam.getVarType();
        if (varType != VarType.Vector4) return;

        FXUtils.addToPane(new ColorMaterialParamControl(getChangeConsumer(), material, matParam.getName()), this);
    }
}
