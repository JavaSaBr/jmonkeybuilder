package com.ss.builder.model.undo.impl.emitter;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.shapes.EmitterShape;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.model.undo.impl.AbstractEditorOperation;
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
    private final EmitterShape prevShape;

    /**
     * The new shape.
     */
    @NotNull
    private final EmitterShape newShape;

    public ChangeEmitterShapeOperation(@NotNull EmitterShape newShape, @NotNull ParticleEmitter emitter) {
        this.newShape = newShape;
        this.prevShape = emitter.getShape();
        this.emitter = emitter;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);
        emitter.setShape(newShape);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyFxReplaced(emitter, prevShape, newShape, true, true);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        emitter.setShape(prevShape);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyFxReplaced(emitter, newShape, prevShape, true, true);
    }

}
