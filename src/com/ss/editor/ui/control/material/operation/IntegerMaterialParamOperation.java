package com.ss.editor.ui.control.material.operation;

import com.jme3.shader.VarType;

/**
 * Операция по изменению int числа в параметрах материала.
 *
 * @author Ronn
 */
public class IntegerMaterialParamOperation extends AbstractMaterialParamOperation<Integer> {

    public IntegerMaterialParamOperation(final String paramName, final Integer newValue, final Integer oldValue) {
        super(paramName, newValue, oldValue);
    }

    @Override
    protected VarType getVarType() {
        return VarType.Int;
    }
}
