package com.ss.editor.ui.control.app.state.operation;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.ui.component.editor.impl.scene.SceneFileEditor;
import com.ss.editor.ui.control.property.operation.AbstractPropertyOperation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractEditorOperation} to edit properties of app states in the {@link
 * SceneFileEditor}*.
 *
 * @param <D> the type parameter
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class AppStatePropertyOperation<D, T> extends AbstractPropertyOperation<SceneChangeConsumer, D, T> {

    /**
     * Instantiates a new App state property operation.
     *
     * @param target       the target
     * @param propertyName the property name
     * @param newValue     the new value
     * @param oldValue     the old value
     */
    public AppStatePropertyOperation(@NotNull final D target, @NotNull final String propertyName, @Nullable final T newValue,
                                     @Nullable final T oldValue) {
        super(target, propertyName, newValue, oldValue);
    }

    @Override
    protected void redoImpl(@NotNull final SceneChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            apply(target, newValue);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(null, target, propertyName));
        });
    }

    @Override
    protected void undoImpl(@NotNull final SceneChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            apply(target, oldValue);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(null, target, propertyName));
        });
    }
}