package com.ss.editor.ui.control.material.operation;

import com.jme3.shader.VarType;

/**
 * Операция по изменению флага в параметрах материала.
 *
 * @author Ronn
 */
public class BooleanMaterialParamOperation extends AbstractMaterialParamOperation<Boolean> {

    public BooleanMaterialParamOperation(final String paramName, final Boolean newValue, final Boolean oldValue) {
        super(paramName, newValue, oldValue);
    }

    @Override
    protected VarType getVarType() {
        return VarType.Boolean;
    }
}
