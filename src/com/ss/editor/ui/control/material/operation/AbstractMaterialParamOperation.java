package com.ss.editor.ui.control.material.operation;

import com.jme3.material.Material;
import com.jme3.shader.VarType;
import com.ss.editor.model.undo.editor.MaterialChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

/**
 * Базовая реализация операции по смене параметра материала.
 *
 * @author Ronn
 */
public abstract class AbstractMaterialParamOperation<T> extends AbstractEditorOperation<MaterialChangeConsumer> {

    /**
     * Название изменяемого параметра.
     */
    private final String paramName;

    /**
     * Новое значение.
     */
    private final T newValue;

    /**
     * Старое значение.
     */
    private final T oldValue;

    public AbstractMaterialParamOperation(final String paramName, final T newValue, final T oldValue) {
        this.paramName = paramName;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    /**
     * @return название изменяемого параметра.
     */
    protected String getParamName() {
        return paramName;
    }

    /**
     * @return тип параметраю
     */
    protected abstract VarType getVarType();

    @Override
    protected void redoImpl(final MaterialChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Material currentMaterial = editor.getCurrentMaterial();

            if(newValue != null) {
                currentMaterial.setParam(getParamName(), getVarType(), newValue);
            } else {
                currentMaterial.clearParam(getParamName());
            }

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeParam(getParamName()));
        });
    }

    @Override
    protected void undoImpl(final MaterialChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Material currentMaterial = editor.getCurrentMaterial();

            if(oldValue != null) {
                currentMaterial.setParam(getParamName(), getVarType(), oldValue);
            } else {
                currentMaterial.clearParam(getParamName());
            }

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeParam(getParamName()));
        });
    }
}
