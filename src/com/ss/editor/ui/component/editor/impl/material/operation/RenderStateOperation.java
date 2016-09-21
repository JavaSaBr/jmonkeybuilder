package com.ss.editor.ui.component.editor.impl.material.operation;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.MaterialChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

/**
 * Базовая реализация операции по изменению рендера материала.
 *
 * @author Ronn
 */
public abstract class RenderStateOperation<T> extends AbstractEditorOperation<MaterialChangeConsumer> {

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * Новое значение параметра.
     */
    private final T newValue;

    /**
     * Старое значение параметра.
     */
    private final T oldValue;

    public RenderStateOperation(final T newValue, final T oldValue) {
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    @Override
    protected void redoImpl(@NotNull final MaterialChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Material currentMaterial = editor.getCurrentMaterial();
            final RenderState renderState = currentMaterial.getAdditionalRenderState();

            apply(renderState, newValue);

            EXECUTOR_MANAGER.addFXTask(editor::notifyChangedRenderState);
        });
    }

    protected abstract void apply(final RenderState renderState, final T value);

    @Override
    protected void undoImpl(@NotNull final MaterialChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Material currentMaterial = editor.getCurrentMaterial();
            final RenderState renderState = currentMaterial.getAdditionalRenderState();

            apply(renderState, oldValue);

            EXECUTOR_MANAGER.addFXTask(editor::notifyChangedRenderState);
        });
    }
}
