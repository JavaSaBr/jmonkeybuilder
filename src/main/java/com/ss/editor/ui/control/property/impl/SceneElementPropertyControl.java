package com.ss.editor.ui.control.property.impl;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.dialog.scene.selector.SceneSelectorDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link ElementPropertyControl} to edit elements from scenes.
 *
 * @param <D> the type of an editing object.
 * @param <T> the type of an editing property.
 * @author JavaSaBr
 */
public abstract class SceneElementPropertyControl<D, T> extends ElementPropertyControl<SceneChangeConsumer, D, T> {

    public SceneElementPropertyControl(
            @NotNull Class<T> type,
            @Nullable T propertyValue,
            @NotNull String propertyName,
            @NotNull SceneChangeConsumer changeConsumer
    ) {
        super(type, propertyValue, propertyName, changeConsumer);
    }

    @Override
    @FxThread
    protected void addElement() {
        createSceneSelectorDialog().show(this);
    }

    /**
     * Create a scene selector dialog.
     *
     * @return the scene selector dialog.
     */
    @FxThread
    protected @NotNull SceneSelectorDialog<T> createSceneSelectorDialog() {
        return new SceneSelectorDialog<>(getChangeConsumer().getCurrentModel(), type, this::addElement);
    }

    /**
     * Add the chosen element.
     *
     * @param newElement the new element.
     */
    @FxThread
    protected void addElement(@NotNull T newElement) {
        changed(newElement, getPropertyValue());
    }
}
