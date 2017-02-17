package com.ss.editor.control.editing.impl;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.ss.editor.Editor;
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

    protected static final Editor EDITOR = Editor.getInstance();

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
    public void setSpatial(final Spatial spatial) {

        final Spatial prev = getSpatial();
        if (prev instanceof Node) {
            onDetached((Node) prev);
        }

        super.setSpatial(spatial);

        if (spatial instanceof Node) {
            onAttached((Node) spatial);
        }
    }

    @NotNull
    protected Material createWireframeMaterial(@NotNull final ColorRGBA color) {

        final Material material = createMaterial(color);
        material.getAdditionalRenderState().setWireframe(true);

        return material;
    }

    @NotNull
    protected Material createMaterial(@NotNull final ColorRGBA color) {

        final Material material = new Material(EDITOR.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", color);

        return material;
    }

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

    /**
     * Notify about that this control was attached.
     */
    protected void onAttached(@NotNull final Node node) {
    }

    /**
     * Notify about that this control was detached.
     */
    protected void onDetached(@NotNull final Node node) {
    }
}
