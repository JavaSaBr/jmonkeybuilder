package com.ss.editor.ui.control.model.tree.action.operation.animation;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.model.undo.impl.AbstractEditorOperation;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to delete an animation.
 *
 * @author JavaSaBr
 */
public class RemoveAnimationNodeOperation extends AbstractEditorOperation<ModelChangeConsumer> {

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

    /**
     * Instantiates a new Remove animation node operation.
     *
     * @param animation the animation
     * @param control   the control
     */
    public RemoveAnimationNodeOperation(@NotNull final Animation animation, @NotNull final AnimControl control) {
        this.animation = animation;
        this.control = control;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            control.removeAnim(animation);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(control, animation));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            control.addAnim(animation);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(control, animation, 0));
        });
    }
}
