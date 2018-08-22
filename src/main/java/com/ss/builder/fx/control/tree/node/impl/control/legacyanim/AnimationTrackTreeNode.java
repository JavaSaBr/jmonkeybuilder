package com.ss.builder.fx.control.tree.node.impl.control.legacyanim;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Track;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.fx.control.tree.node.TreeNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of node to show {@link Track}.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
@Deprecated
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

    public AnimationTrackTreeNode(@NotNull T element, long objectId) {
        super(element, objectId);
    }

    /**
     * Set the animation control.
     *
     * @param control the animation control.
     */
    @FxThread
    public void setControl(@Nullable AnimControl control) {
        this.control = control;
        this.cachedName = computeName();
    }

    /**
     * Compute name string.
     *
     * @return the string
     */
    @FxThread
    protected abstract @NotNull String computeName();

    @Override
    @FromAnyThread
    public @NotNull String getName() {
        return cachedName;
    }

    /**
     * Get the animation control.
     *
     * @return the animation control.
     */
    @FxThread
    protected @Nullable AnimControl getControl() {
        return control;
    }
}
