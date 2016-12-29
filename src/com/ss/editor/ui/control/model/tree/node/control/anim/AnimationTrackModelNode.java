package com.ss.editor.ui.control.model.tree.node.control.anim;

import com.jme3.animation.AnimControl;
import com.jme3.animation.Track;
import com.ss.editor.ui.control.model.tree.node.ModelNode;

/**
 * The implementation of node for showing {@link Track}.
 *
 * @author JavaSaBr
 */
public abstract class AnimationTrackModelNode<T extends Track> extends ModelNode<T> {

    /**
     * The animation control.
     */
    private AnimControl control;

    public AnimationTrackModelNode(final T element, final long objectId) {
        super(element, objectId);
    }

    /**
     * @param control The animation control.
     */
    public void setControl(final AnimControl control) {
        this.control = control;
    }

    /**
     * @return the animation control.
     */
    protected AnimControl getControl() {
        return control;
    }
}
