package com.ss.editor.ui.control.model.property.operation;

import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.ui.component.editor.impl.model.ModelFileEditor;
import com.ss.editor.ui.control.model.property.generic.EditableProperty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * The implementation of the {@link AbstractEditorOperation} for editing editable properties in the {@link
 * ModelFileEditor}.
 *
 * @author JavaSaBr
 */
public class EditablePropertyPropertyOperation<T> extends AbstractEditorOperation<ModelChangeConsumer> {

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
     * The editable property.
     */
    @NotNull
    private final EditableProperty<T, ?> property;

    /**
     * The handler for applying new value.
     */
    private BiConsumer<EditableProperty<T, ?>, T> applyHandler;

    public EditablePropertyPropertyOperation(@NotNull final EditableProperty<T, ?> property, @Nullable final T newValue,
                                             @Nullable final T oldValue) {
        this.newValue = newValue;
        this.oldValue = oldValue;
        this.propertyName = property.getName();
        this.property = property;
    }

    /**
     * @param applyHandler the handler for applying new value.
     */
    public void setApplyHandler(@NotNull final BiConsumer<EditableProperty<T, ?>, T> applyHandler) {
        this.applyHandler = applyHandler;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            applyHandler.accept(property, newValue);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(null, property.getObject(), propertyName));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            applyHandler.accept(property, oldValue);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(null, property.getObject(), propertyName));
        });
    }
}