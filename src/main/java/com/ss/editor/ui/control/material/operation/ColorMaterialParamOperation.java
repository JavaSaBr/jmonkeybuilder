package com.ss.editor.ui.control.material.operation;

import com.jme3.math.ColorRGBA;
import com.jme3.shader.VarType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The operation to change color parameters.
 *
 * @author JavaSaBr
 */
public class ColorMaterialParamOperation extends AbstractMaterialParamOperation<ColorRGBA> {

    /**
     * Instantiates a new Color material param operation.
     *
     * @param paramName the param name
     * @param newValue  the new value
     * @param oldValue  the old value
     */
    public ColorMaterialParamOperation(@NotNull final String paramName, @Nullable final ColorRGBA newValue,
                                       @Nullable final ColorRGBA oldValue) {
        super(paramName, newValue, oldValue);
    }

    @NotNull
    @Override
    protected VarType getVarType() {
        return VarType.Vector4;
    }
}
