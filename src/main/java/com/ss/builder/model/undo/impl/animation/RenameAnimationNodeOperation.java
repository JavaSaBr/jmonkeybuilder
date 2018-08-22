package com.ss.builder.model.undo.impl.animation;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
import com.ss.builder.model.undo.impl.AbstractEditorOperation;
import com.ss.builder.util.AnimationUtils;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} for editing name of an animation.
 *
 * @author JavaSaBr
 */
public class RenameAnimationNodeOperation extends AbstractEditorOperation<ModelChangeConsumer> {

    /**
     * The old name.
     */
    @NotNull
    private final String oldName;

    /**
     * The new name.
     */
    @NotNull
    private final String newName;

    /**
     * The animation control.
     */
    @NotNull
    private final AnimControl control;

    public RenameAnimationNodeOperation(
            @NotNull String oldName,
            @NotNull String newName,
            @NotNull AnimControl control
    ) {
        this.oldName = oldName;
        this.newName = newName;
        this.control = control;
    }

    @Override
    @JmeThread
    protected void redoImpl(@NotNull ModelChangeConsumer editor) {
        super.redoImpl(editor);
        AnimationUtils.changeName(control, control.getAnim(oldName), oldName, newName);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull ModelChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyFxChangeProperty(control, control.getAnim(newName), "name");
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull ModelChangeConsumer editor) {
        super.undoInJme(editor);
        AnimationUtils.changeName(control, control.getAnim(newName), newName, oldName);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull ModelChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyFxChangeProperty(control, control.getAnim(oldName), "name");
    }
}
