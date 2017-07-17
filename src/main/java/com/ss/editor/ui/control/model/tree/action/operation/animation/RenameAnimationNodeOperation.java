package com.ss.editor.ui.control.model.tree.action.operation.animation;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import com.ss.editor.util.AnimationUtils;
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

    /**
     * Instantiates a new Rename animation node operation.
     *
     * @param oldName the old name
     * @param newName the new name
     * @param control the control
     */
    public RenameAnimationNodeOperation(@NotNull final String oldName, @NotNull final String newName,
                                        @NotNull final AnimControl control) {
        this.oldName = oldName;
        this.newName = newName;
        this.control = control;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            final Animation anim = control.getAnim(oldName);
            AnimationUtils.changeName(control, anim, oldName, newName);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(control, anim, "name"));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            final Animation anim = control.getAnim(newName);
            AnimationUtils.changeName(control, anim, newName, oldName);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyChangeProperty(control, anim, "name"));
        });
    }
}
