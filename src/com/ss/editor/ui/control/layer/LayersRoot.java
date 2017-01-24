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

    public LayersRoot(@NotNull final SceneChangeConsumer changeConsumer) {
        this.changeConsumer = changeConsumer;
    }

    @NotNull
    public SceneChangeConsumer getChangeConsumer() {
        return changeConsumer;
    }
}
