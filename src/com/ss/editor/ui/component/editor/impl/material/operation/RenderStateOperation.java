package com.ss.editor.ui.component.editor.impl.material.operation;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.MaterialChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The implementation of an editor operation to edit material render params.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
@SuppressWarnings("Duplicates")
public class RenderStateOperation<T> extends AbstractEditorOperation<MaterialChangeConsumer> {

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The apply handler.
     */
    @NotNull
    private final BiConsumer<RenderState, T> applyHandler;

    /**
     * The new value.
     */
    @Nullable
    private final T newValue;

    /**
     * The old value.
     */
    @Nullable
    private final T oldValue;

    /**
     * Instantiates a new Render state operation.
     *
     * @param newValue     the new value
     * @param oldValue     the old value
     * @param applyHandler the apply handler
     */
    public RenderStateOperation(@Nullable final T newValue, @Nullable final T oldValue,
                                   @NotNull final BiConsumer<RenderState, T> applyHandler) {
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.applyHandler = applyHandler;
    }

    @Override
    protected void redoImpl(@NotNull final MaterialChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Material currentMaterial = editor.getCurrentMaterial();
            final RenderState renderState = currentMaterial.getAdditionalRenderState();

            applyHandler.accept(renderState, newValue);

            EXECUTOR_MANAGER.addFXTask(editor::notifyChangedRenderState);
        });
    }

    @Override
    protected void undoImpl(@NotNull final MaterialChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Material currentMaterial = editor.getCurrentMaterial();
            final RenderState renderState = currentMaterial.getAdditionalRenderState();

            applyHandler.accept(renderState, oldValue);

            EXECUTOR_MANAGER.addFXTask(editor::notifyChangedRenderState);
        });
    }
}
