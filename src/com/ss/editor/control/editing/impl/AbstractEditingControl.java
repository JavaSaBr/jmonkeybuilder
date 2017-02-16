package com.ss.editor.control.editing.impl;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.ss.editor.control.editing.EditingControl;
import com.ss.editor.control.editing.EditingInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of an editing control.
 *
 * @author JavaSaBr
 */
public class AbstractEditingControl extends AbstractControl implements EditingControl {

    /**
     * The current editing input.
     */
    @Nullable
    private EditingInput editingInput;

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
    public void startEditing(@NotNull final EditingInput editingInput, @NotNull final Vector3f contactPoint) {
        setEditing(true);
        setEditingInput(editingInput);
    }

    @Override
    public void finishEditing(@NotNull final Vector3f contactPoint) {
        setEditing(false);
    }

    /**
     * @param editingInput the current editing input.
     */
    private void setEditingInput(@Nullable final EditingInput editingInput) {
        this.editingInput = editingInput;
    }

    @Nullable
    @Override
    public EditingInput getCurrentInput() {
        return editingInput;
    }
}
