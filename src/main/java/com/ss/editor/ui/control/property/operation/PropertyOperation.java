package com.ss.editor.ui.control.property.operation;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.EditorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The implementation of the {@link AbstractEditorOperation} to edit properties of objects.
 *
 * @param <C> the type of changed consumer
 * @param <D> the type of edited object
 * @param <T> the type of edited property
 * @author JavaSaBr
 */
public class PropertyOperation<C extends ChangeConsumer, D, T> extends AbstractEditorOperation<C> {

    /**
     * The property name.
     */
    @NotNull
    protected final String propertyName;

    /**
     * The new value of the property.
     */
    @Nullable
    protected final T newValue;

    /**
     * The old value of the property.
     */
    @Nullable
    protected final T oldValue;

    /**
     * The target object.
     */
    protected final D target;

    /**
     * The handler for applying new value.
     */
    private BiConsumer<D, T> applyHandler;

    /**
     * Instantiates a new Abstract property operation.
     *
     * @param target       the target
     * @param propertyName the property name
     * @param newValue     the new value
     * @param oldValue     the old value
     */
    public PropertyOperation(@NotNull final D target, @NotNull final String propertyName, @Nullable final T newValue,
                             @Nullable final T oldValue) {
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.target = target;
        this.propertyName = propertyName;
    }

    @Override
    @FXThread
    protected void redoImpl(@NotNull final C editor) {
        EXECUTOR_MANAGER.addJMETask(() -> {
            apply(target, newValue);
            editor.notifyJMEChangeProperty(target, propertyName);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyFXChangeProperty(target, propertyName));
        });
    }

    @Override
    @FXThread
    protected void undoImpl(@NotNull final C editor) {
        EXECUTOR_MANAGER.addJMETask(() -> {
            apply(target, oldValue);
            editor.notifyJMEChangeProperty(target, propertyName);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyFXChangeProperty(target, propertyName));
        });
    }

    /**
     * Sets apply handler.
     *
     * @param applyHandler the handler for applying new value.
     */
    public void setApplyHandler(@NotNull final BiConsumer<D, T> applyHandler) {
        this.applyHandler = applyHandler;
    }

    /**
     * Apply new value of the property to the model.
     *
     * @param spatial the spatial
     * @param value   the value
     */
    protected void apply(@NotNull final D spatial, @Nullable final T value) {
        try {
            applyHandler.accept(spatial, value);
        } catch (final Exception e) {
            EditorUtil.handleException(LOGGER, this, e);
        }
    }
}