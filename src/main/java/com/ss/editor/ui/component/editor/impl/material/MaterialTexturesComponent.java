package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.shader.VarType;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.material.Texture2DMaterialParamControl;
import com.ss.rlib.ui.util.FXUtils;
import org.jetbrains.annotations.NotNull;

/**
 * The component to edit material texture properties.
 *
 * @author JavaSaBr
 */
public class MaterialTexturesComponent extends AbstractMaterialPropertiesComponent {

    public MaterialTexturesComponent(@NotNull final ChangeConsumer changeConsumer) {
        super(changeConsumer);
    }

    @Override
    protected void buildFor(@NotNull final MatParam matParam, @NotNull final Material material) {

        final ChangeConsumer changeConsumer = getChangeConsumer();
        final VarType varType = matParam.getVarType();

        if (varType == VarType.Texture2D) {
            FXUtils.addToPane(new Texture2DMaterialParamControl(changeConsumer, material, matParam.getName()), this);
        }
    }
}
