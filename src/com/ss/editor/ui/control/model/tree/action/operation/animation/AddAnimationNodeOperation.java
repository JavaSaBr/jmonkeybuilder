package com.ss.editor.ui.control.model.tree.action.operation.animation;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
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

    /**
     * Instantiates a new Add animation node operation.
     *
     * @param animation the animation
     * @param control   the control
     */
    public AddAnimationNodeOperation(@NotNull final Animation animation, @NotNull final AnimControl control) {
        this.animation = animation;
        this.control = control;
    }

    @Override
    protected void redoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            control.addAnim(animation);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyAddedChild(control, animation, 0));
        });
    }

    @Override
    protected void undoImpl(@NotNull final ModelChangeConsumer editor) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> {
            control.removeAnim(animation);
            EXECUTOR_MANAGER.addFXTask(() -> editor.notifyRemovedChild(control, animation));
        });
    }
}
