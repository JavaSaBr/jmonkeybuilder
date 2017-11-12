package com.ss.editor.ui.control.model.node.control.anim;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Track;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.control.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link Track}.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public abstract class AnimationTrackTreeNode<T extends Track> extends TreeNode<T> {

    /**
     * The animation control.
     */
    @Nullable
    private AnimControl control;

    /**
     * The cached name.
     */
    @Nullable
    private String cachedName;

    public AnimationTrackTreeNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    /**
     * Sets control.
     *
     * @param control the animation control.
     */
    @FXThread
    public void setControl(@Nullable final AnimControl control) {
        this.control = control;
        this.cachedName = computeName();
    }

    /**
     * Compute name string.
     *
     * @return the string
     */
    @FXThread
    protected abstract @NotNull String computeName();

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return cachedName;
    }

    /**
     * Gets control.
     *
     * @return the animation control.
     */
    @FXThread
    protected @Nullable AnimControl getControl() {
        return control;
    }
}
