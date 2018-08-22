package com.ss.editor.model.node.layer;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of layers root.
 *
 * @author JavaSaBr
 */
public class LayersRoot {

    /**
     * The change consumer.
     */
    @NotNull
    private final SceneChangeConsumer changeConsumer;

    public LayersRoot(@NotNull final SceneChangeConsumer changeConsumer) {
        this.changeConsumer = changeConsumer;
    }

    /**
     * Get the change consumer.
     *
     * @return the change consumer
     */
    @FromAnyThread
    public @NotNull SceneChangeConsumer getChangeConsumer() {
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
