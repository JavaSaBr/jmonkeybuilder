package com.ss.editor.ui.control.material.operation;

import com.jme3.math.ColorRGBA;
import com.jme3.shader.VarType;

/**
 * Операция по изменению цвета в параметрах материала.
 *
 * @author Ronn
 */
public class ColorMaterialParamOperation extends AbstractMaterialParamOperation<ColorRGBA> {

    public ColorMaterialParamOperation(final String paramName, final ColorRGBA newValue, final ColorRGBA oldValue) {
        super(paramName, newValue, oldValue);
    }

    @Override
    protected VarType getVarType() {
        return VarType.Vector4;
    }
}
