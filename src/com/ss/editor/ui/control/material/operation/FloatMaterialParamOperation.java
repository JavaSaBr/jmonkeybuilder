package com.ss.editor.ui.control.material.operation;

import com.jme3.shader.VarType;

/**
 * Операция по изменению float числа в параметрах материала.
 *
 * @author Ronn
 */
public class FloatMaterialParamOperation extends AbstractMaterialParamOperation<Float> {

    public FloatMaterialParamOperation(final String paramName, final Float newValue, final Float oldValue) {
        super(paramName, newValue, oldValue);
    }

    @Override
    protected VarType getVarType() {
        return VarType.Float;
    }
}
