package com.ss.editor.model.undo.impl.animation;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to add an animation.
 *
 * @author JavaSaBr
 */
public class AddAnimationNodeOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The animation control.
     */
    @NotNull
    private final AnimControl control;

    /**
     * The animation.
     */
    @NotNull
    private final Animation animation;

    public AddAnimationNodeOperation(@NotNull Animation animation, @NotNull AnimControl control) {
        this.animation = animation;
        this.control = control;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull ModelChangeConsumer editor) {
        super.redoInJme(editor);
        control.addAnim(animation);
    }

    @Override
    @FxThread
    protected void finishRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.finishRedoInFx(editor);
        editor.notifyFxAddedChild(control, animation, -1, true);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        control.removeAnim(animation);
    }

    @Override
    @FxThread
    protected void finishUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.finishUndoInFx(editor);
        editor.notifyFxRemovedChild(control, animation);
    }
}
