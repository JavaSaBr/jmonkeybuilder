package com.ss.editor.ui.control.model.property.operation;

import com.jme3.scene.Spatial;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import java.util.Objects;
import java.util.function.BiConsumer;

import static com.ss.editor.util.GeomUtils.getObjectByIndex;
import static rlib.util.ClassUtils.unsafeCast;

/**
 * Базовая реализация операции по изменению свойства модели.
 *
 * @author Ronn
 */
public class ModelPropertyOperation<D, T> extends AbstractEditorOperation<ModelChangeConsumer> {

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * Название изменяемого свойства.
     */
    private final String propertyName;

    /**
     * Новое значение параметра.
     */
    private final T newValue;

    /**
     * Старое значение параметра.
     */
    private final T oldValue;

    /**
     * Номер части модели.
     */
    private final int index;

    /**
     * Обработчик приминения изменений.
     */
    private BiConsumer<D, T> applyHandler;

    public ModelPropertyOperation(final int index, final String propertyName, final T newValue, final T oldValue) {
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.index = index;
        this.propertyName = propertyName;
    }

    /**
     * @param applyHandler обработчик приминения изменений.
     */
    public void setApplyHandler(final BiConsumer<D, T> applyHandler) {
        this.applyHandler = applyHandler;
    }

    @Override
    protected void redoImpl(final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final D target = unsafeCast(getObjectByIndex(currentModel, index));

            Objects.requireNonNull(target);

            apply(target, newValue);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(target, propertyName));
        });
    }

    /**
     * Приминение изменения на модель.
     */
    protected void apply(final D spatial, final T value) {
        applyHandler.accept(spatial, value);
    }

    @Override
    protected void undoImpl(final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial currentModel = editor.getCurrentModel();
            final D target = unsafeCast(getObjectByIndex(currentModel, index));

            Objects.requireNonNull(target);

            apply(target, oldValue);

            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(target, propertyName));
        });
    }
}