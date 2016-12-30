package com.ss.editor.ui.control.model.property.operation;

import static com.ss.editor.util.GeomUtils.getObjectByIndex;
import static rlib.util.ClassUtils.unsafeCast;

import com.jme3.scene.Spatial;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.ui.component.editor.impl.model.ModelFileEditor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * The implementation of the {@link AbstractEditorOperation} for editing models in the {@link ModelFileEditor}.
 *
 * @author JavaSaBr
 */
public class ModelPropertyOperation<D, T> extends AbstractEditorOperation<ModelChangeConsumer> {

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The property name.
     */
    @NotNull
    private final String propertyName;

    /**
     * The new value of the property.
     */
    @Nullable
    private final T newValue;

    /**
     * The old value of the property.
     */
    @Nullable
    private final T oldValue;

    /**
     * The index of node.
     */
    private final int index;

    /**
     * The handler for applying new value.
     */
    private BiConsumer<D, T> applyHandler;

    public ModelPropertyOperation(final int index, @NotNull final String propertyName, @Nullable final T newValue,
                                  @Nullable final T oldValue) {
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.index = index;
        this.propertyName = propertyName;
    }

    /**
     * @param applyHandler the handler for applying new value.
     */
    public void setApplyHandler(@NotNull final BiConsumer<D, T> applyHandler) {
        this.applyHandler = applyHandler;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final D target = unsafeCast(getObjectByIndex(currentModel, index));

            Objects.requireNonNull(target);

            apply(target, newValue);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(null, target, propertyName));
        });
    }

    /**
     * Apply new value of the property to the model.
     */
    protected void apply(@NotNull final D spatial, @Nullable final T value) {
        applyHandler.accept(spatial, value);
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final D target = unsafeCast(getObjectByIndex(currentModel, index));

            Objects.requireNonNull(target);

            apply(target, oldValue);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(null, target, propertyName));
        });
    }
}