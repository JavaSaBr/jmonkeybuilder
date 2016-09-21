package com.ss.editor.ui.control.model.property.operation;

import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * Базовая реализация операции по изменению свойства источника света.
 *
 * @author Ronn
 */
public class LightPropertyOperation<D, T> extends AbstractEditorOperation<ModelChangeConsumer> {

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
    private final D light;

    /**
     * Обработчик приминения изменений.
     */
    private BiConsumer<D, T> applyHandler;

    public LightPropertyOperation(final D light, final String propertyName, final T newValue, final T oldValue) {
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.light = light;
        this.propertyName = propertyName;
    }

    /**
     * @param applyHandler обработчик приминения изменений.
     */
    public void setApplyHandler(final BiConsumer<D, T> applyHandler) {
        this.applyHandler = applyHandler;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            apply(light, newValue);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(light, propertyName));
        });
    }

    /**
     * Приминение изменения на модель.
     */
    protected void apply(final D light, final T value) {
        applyHandler.accept(light, value);
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            apply(light, oldValue);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(light, propertyName));
        });
    }
}