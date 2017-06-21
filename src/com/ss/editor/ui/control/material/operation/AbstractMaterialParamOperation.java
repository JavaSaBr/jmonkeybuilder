package com.ss.editor.ui.control.material.operation;

import com.jme3.material.Material;
import com.jme3.shader.VarType;
import com.ss.editor.model.undo.editor.MaterialChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of an editor operation to change material parameter.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public abstract class AbstractMaterialParamOperation<T> extends AbstractEditorOperation<MaterialChangeConsumer> {

    /**
     * The parameter name.
     */
    @NotNull
    private final String paramName;

    /**
     * The new value.
     */
    @Nullable
    private final T newValue;

    /**
     * The prev value.
     */
    @Nullable
    private final T oldValue;

    /**
     * Instantiates a new Abstract material param operation.
     *
     * @param paramName the param name
     * @param newValue  the new value
     * @param oldValue  the old value
     */
    public AbstractMaterialParamOperation(@NotNull final String paramName, @Nullable final T newValue,
                                          @Nullable final T oldValue) {
        this.paramName = paramName;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    /**
     * Gets param name.
     *
     * @return The parameter name..
     */
    @NotNull
    protected String getParamName() {
        return paramName;
    }

    /**
     * Gets var type.
     *
     * @return the parameter type.
     */
    @NotNull
    protected abstract VarType getVarType();

    @Override
    protected void redoImpl(@NotNull final MaterialChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Material currentMaterial = editor.getCurrentMaterial();

            if (newValue != null) {
                currentMaterial.setParam(getParamName(), getVarType(), newValue);
            } else {
                currentMaterial.clearParam(getParamName());
            }

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeParam(getParamName()));
        });
    }

    @Override
    protected void undoImpl(@NotNull final MaterialChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Material currentMaterial = editor.getCurrentMaterial();

            if (oldValue != null) {
                currentMaterial.setParam(getParamName(), getVarType(), oldValue);
            } else {
                currentMaterial.clearParam(getParamName());
            }

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeParam(getParamName()));
        });
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "paramName='" + paramName + '\'' +
                ", newValue=" + newValue +
                ", oldValue=" + oldValue +
                "} " + super.toString();
    }
}
