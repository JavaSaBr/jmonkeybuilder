package com.ss.editor.ui.control.material.operation;

import com.jme3.shader.VarType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The operation to change float parameters.
 *
 * @author JavaSaBr
 */
public class FloatMaterialParamOperation extends AbstractMaterialParamOperation<Float> {

    public FloatMaterialParamOperation(@NotNull final String paramName, @Nullable final Float newValue,
                                       @Nullable final Float oldValue) {
        super(paramName, newValue, oldValue);
    }

    @NotNull
    @Override
    protected VarType getVarType() {
        return VarType.Float;
    }
}
