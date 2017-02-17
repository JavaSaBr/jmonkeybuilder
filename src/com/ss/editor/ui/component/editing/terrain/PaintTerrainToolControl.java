package com.ss.editor.ui.component.editing.terrain;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.ss.editor.control.editing.EditingInput;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of terrain tool to paint textures.
 *
 * @author JavaSaBr
 */
public class PaintTerrainToolControl extends TerrainToolControl {

    public PaintTerrainToolControl(@NotNull final TerrainEditingComponent component) {
        super(component);
    }

    @NotNull
    @Override
    protected ColorRGBA getBrushColor() {
        return ColorRGBA.Blue;
    }

    @Override
    public void startEditing(@NotNull final EditingInput editingInput, @NotNull final Vector3f contactPoint) {
        super.startEditing(editingInput, contactPoint);
    }

    @Override
    public void updateEditing(@NotNull final Vector3f contactPoint) {
    }

    @Override
    public void finishEditing(@NotNull final Vector3f contactPoint) {
        super.finishEditing(contactPoint);
    }
}
