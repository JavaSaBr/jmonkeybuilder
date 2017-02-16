package com.ss.editor.control.editing.impl;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.ss.editor.control.editing.EditingControl;
import org.jetbrains.annotations.NotNull;

/**
 * The base implementation of an editing control.
 *
 * @author JavaSaBr
 */
public class AbstractEditingControl extends AbstractControl implements EditingControl {

    /**
     * The flag of editing state.
     */
    private boolean editing;

    @Override
    protected void controlUpdate(final float tpf) {
    }

    @Override
    protected void controlRender(@NotNull final RenderManager renderManager, @NotNull final ViewPort viewPort) {
    }

    @Override
    public boolean isStartedEditing() {
        return editing;
    }

    /**
     * @param editing the flag of editing state.
     */
    protected void setEditing(final boolean editing) {
        this.editing = editing;
    }

    @Override
    public void startEditing(@NotNull final Vector3f contactPoint) {
        setEditing(true);
    }

    @Override
    public void finishEditing(@NotNull final Vector3f contactPoint) {
        setEditing(false);
    }
}
