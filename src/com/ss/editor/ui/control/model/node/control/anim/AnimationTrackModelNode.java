package com.ss.editor.ui.control.model.node.control.anim;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Track;
import com.ss.editor.ui.control.tree.node.ModelNode;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * The implementation of node to show {@link Track}.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public abstract class AnimationTrackModelNode<T extends Track> extends ModelNode<T> {

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

    /**
     * Instantiates a new Animation track model node.
     *
     * @param element  the element
     * @param objectId the object id
     */
    public AnimationTrackModelNode(@NotNull final T element, final long objectId) {
        super(element, objectId);
    }

    /**
     * Sets control.
     *
     * @param control the animation control.
     */
    public void setControl(@Nullable final AnimControl control) {
        this.control = control;
        this.cachedName = computeName();
    }

    /**
     * Compute name string.
     *
     * @return the string
     */
    @NotNull
    protected abstract String computeName();

    @NotNull
    @Override
    public String getName() {
        return cachedName;
    }

    /**
     * Gets control.
     *
     * @return the animation control.
     */
    @Nullable
    protected AnimControl getControl() {
        return control;
    }
}
