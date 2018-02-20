package com.ss.editor.ui.control.property.impl;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.dialog.scene.selector.SceneSelectorDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ElementPropertyControl} to edit elements from scenes.
 *
 * @param <D> the edited object's type.
 * @author JavaSaBr
 */
public abstract class SceneElementPropertyControl<D, T> extends ElementPropertyControl<SceneChangeConsumer, D, T> {

    public SceneElementPropertyControl(@NotNull final Class<T> type, @Nullable final T propertyValue,
                                       @NotNull final String propertyName,
                                       @NotNull final SceneChangeConsumer changeConsumer) {
        super(type, propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected void processAdd() {
        final SceneSelectorDialog<T> dialog = createSceneSelectorDialog();
        dialog.show(this);
    }

    /**
     * Create scene selector dialog node selector dialog.
     *
     * @return the scene selector dialog.
     */
    @FxThread
    protected @NotNull SceneSelectorDialog<T> createSceneSelectorDialog() {
        final SceneChangeConsumer changeConsumer = getChangeConsumer();
        return new SceneSelectorDialog<>(changeConsumer.getCurrentModel(), type, this::processAdd);
    }

    /**
     * Process adding the new element.
     *
     * @param newElement the new element.
     */
    @FxThread
    protected void processAdd(@NotNull final T newElement) {
        changed(newElement, getPropertyValue());
    }
}
