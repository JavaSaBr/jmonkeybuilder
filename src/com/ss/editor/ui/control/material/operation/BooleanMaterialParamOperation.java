package com.ss.editor.ui.control.material.operation;

import com.jme3.shader.VarType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The operation to change boolean parameters.
 *
 * @author JavaSaBr
 */
public class BooleanMaterialParamOperation extends AbstractMaterialParamOperation<Boolean> {

    /**
     * Instantiates a new Boolean material param operation.
     *
     * @param paramName the param name
     * @param newValue  the new value
     * @param oldValue  the old value
     */
    public BooleanMaterialParamOperation(@NotNull final String paramName, @Nullable final Boolean newValue,
                                         @Nullable final Boolean oldValue) {
        super(paramName, newValue, oldValue);
    }

    @NotNull
    @Override
    protected VarType getVarType() {
        return VarType.Boolean;
    }
}
