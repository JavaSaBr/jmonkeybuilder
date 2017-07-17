package com.ss.editor.ui.control.layer;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of layers root.
 *
 * @author JavaSaBr
 */
public class LayersRoot {

    @NotNull
    private final SceneChangeConsumer changeConsumer;

    /**
     * Instantiates a new Layers root.
     *
     * @param changeConsumer the change consumer
     */
    public LayersRoot(@NotNull final SceneChangeConsumer changeConsumer) {
        this.changeConsumer = changeConsumer;
    }

    /**
     * Gets change consumer.
     *
     * @return the change consumer
     */
    @NotNull
    public SceneChangeConsumer getChangeConsumer() {
        return changeConsumer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LayersRoot that = (LayersRoot) o;
        return changeConsumer.equals(that.changeConsumer);

    }

    @Override
    public int hashCode() {
        return changeConsumer.hashCode();
    }
}
