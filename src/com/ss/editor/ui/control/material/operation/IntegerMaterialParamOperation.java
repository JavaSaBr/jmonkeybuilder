package com.ss.editor.ui.control.material.operation;

import com.jme3.shader.VarType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The operation to change integer parameters.
 *
 * @author JavaSaBr
 */
public class IntegerMaterialParamOperation extends AbstractMaterialParamOperation<Integer> {

    public IntegerMaterialParamOperation(@NotNull final String paramName, @Nullable final Integer newValue,
                                         @Nullable final Integer oldValue) {
        super(paramName, newValue, oldValue);
    }

    @NotNull
    @Override
    protected VarType getVarType() {
        return VarType.Int;
    }
}
