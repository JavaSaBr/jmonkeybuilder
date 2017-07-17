package com.ss.editor.ui.component.editor.impl.material;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.shader.VarType;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.control.material.Texture2DMaterialParamControl;
import com.ss.rlib.ui.util.FXUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The component to edit material texture properties.
 *
 * @author JavaSaBr
 */
public class MaterialTexturesComponent extends AbstractMaterialPropertiesComponent {

    /**
     * Instantiates a new Material textures component.
     *
     * @param changeHandler the change handler
     */
    public MaterialTexturesComponent(@NotNull final Consumer<EditorOperation> changeHandler) {
        super(changeHandler);
    }

    @Override
    protected void buildFor(@NotNull final MatParam matParam, @NotNull final Material material) {

        final Consumer<EditorOperation> changeHandler = getChangeHandler();
        final VarType varType = matParam.getVarType();

        if (varType == VarType.Texture2D) {
            FXUtils.addToPane(new Texture2DMaterialParamControl(changeHandler, material, matParam.getName()), this);
        }
    }
}
