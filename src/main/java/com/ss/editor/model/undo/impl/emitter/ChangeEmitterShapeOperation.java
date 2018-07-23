package com.ss.editor.model.undo.impl.emitter;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterShape;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} for changing a shape in the {@link ParticleEmitter}.
 *
 * @author JavaSaBr.
 */
public class ChangeEmitterShapeOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The emitter.
     */
    @NotNull
    private final ParticleEmitter emitter;

    /**
     * The prev shape.
     */
    @NotNull
    private volatile EmitterShape prevShape;

    public ChangeEmitterShapeOperation(@NotNull final EmitterShape newShape, @NotNull final ParticleEmitter emitter) {
        this.prevShape = newShape;
        this.emitter = emitter;
    }

    @Override
    protected void redoInFx(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> switchShape(editor));
    }

    private void switchShape(final @NotNull ModelChangeConsumer editor) {

        final EmitterShape shape = emitter.getShape();
        final EmitterShape newShape = prevShape;
        prevShape = shape;
        emitter.setShape(newShape);

        EXECUTOR_MANAGER.addFxTask(() -> editor.notifyFxReplaced(emitter, prevShape, newShape, true, true));
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addJmeTask(() -> switchShape(editor));
    }
}
